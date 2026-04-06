package com.loopang.company_service.application;

import com.loopang.common.event.Events;
import com.loopang.common.event.OutboxEvent;
import com.loopang.company_service.application.dto.CompanyCreateRequest;
import com.loopang.company_service.application.dto.CompanyInfoUpdateRequest;
import com.loopang.company_service.domain.dto.CoordinateData;
import com.loopang.company_service.domain.dto.HubData;
import com.loopang.company_service.domain.dto.ManagerData;
import com.loopang.company_service.domain.entity.Company;
import com.loopang.company_service.domain.event.CompanyDeletedEvent;
import com.loopang.company_service.domain.event.CompanyTerminatedEvent;
import com.loopang.company_service.domain.event.CompanyUpdatedEvent;
import com.loopang.company_service.domain.exception.CompanyBadRequestException;
import com.loopang.company_service.domain.repository.CompanyRepository;
import com.loopang.company_service.domain.service.AddressProvider;
import com.loopang.company_service.domain.service.CompanyCreator;
import com.loopang.company_service.domain.service.HubProvider;
import com.loopang.company_service.domain.service.ManagerProvider;
import com.loopang.company_service.domain.vo.CompanyAddress;
import com.loopang.company_service.domain.vo.CompanyStatus;
import com.loopang.company_service.domain.vo.HubInfo;
import com.loopang.company_service.domain.vo.ManagerInfo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

  private final CompanyRepository companyRepository;
  private final CompanyCreator creator;
  private final AddressProvider addressProvider;
  private final ManagerProvider managerProvider;
  private final HubProvider hubProvider;

  /**
   * 업체 생성 (전체 프로세스) &#064;Transactional을 붙이지 않아 외부 API 호출 시에만 DB 커넥션을 점유하지 않음
   */
  public UUID createCompany(CompanyCreateRequest request) {
    // 1. 업체명 중복 체크 (Soft Delete 고려)

    if (companyRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
      throw new CompanyBadRequestException("이미 존재하는 업체 이름입니다: " + request.getName());
    }

    // 2. 외부 인프라 서비스 연동 (트랜잭션 외부 수행)

    // (1) T-map을 통한 주소 및 좌표 획득
    CoordinateData coords = addressProvider.findCoordinate(request.getFullAddress());
    CompanyAddress address = CompanyAddress.create(
        coords.lon(),
        coords.lat(),
        coords.cityDo(),
        coords.guGun(),
        coords.dongDoro(),
        coords.detailAddress(),
        request.getFullAddress()
    );

    // (2) User-Service를 통한 매니저 정보 확인
    ManagerData managerData = managerProvider.getManagerData(request.getManagerId());
    ManagerInfo manager = ManagerInfo.create(managerData.id(),
        managerData.name());

    // (3) Hub-Service를 통한 허브 정보 확인
    HubData hubData = hubProvider.getValidHubInfo(request.getHubId());
    HubInfo hub = HubInfo.create(hubData.id(), hubData.name());

    // 3. 실제 DB 저장 (별도 트랜잭션 메서드 호출)
    return creator.save(request.getName(), request.getType(), address, manager, hub);
  }

  @Transactional
  public void updateCompany(UUID companyId, CompanyInfoUpdateRequest request) {
    // 1. 엔티티 조회
    Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new CompanyBadRequestException("해당 업체를 찾을 수 없습니다."));

    // 2. 이름 변경 요청이 있는 경우에만 중복 체크 (성능 최적화)
    if (request.getName() != null && !request.getName().equals(company.getName())) {
      if (companyRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
        throw new CompanyBadRequestException("이미 사용 중인 업체 이름입니다.");
      }
    }

    // 3. 엔티티 상태 변경 (Dirty Checking)
    CompanyStatus newStatus = (request.getStatus() != null)
        ? request.getStatus()
        : null;

    company.updateInfo(request.getName(), newStatus);

    // 4. Outbox 이벤트 발행
    // 공통 모듈의 OutboxEventListener가 이 이벤트를 받아 p_outbox에 저장합니다.
    Events.trigger(OutboxEvent.of(
        "COMPANY",         // domainType
        company.getId(),             // domainId
        "company-update-topic",      // eventType (Kafka Topic명으로 사용됨)
        new CompanyUpdatedEvent(     // payload
            company.getId(),
            company.getName(),
            company.getStatus()
        )
    ));
  }

  /**
   * 업체 삭제 1단계: 업체 삭제 예약 (사용자 요청)
   */
  @Transactional
  public void deleteCompany(UUID companyId) {
    // 1. 엔티티 조회
    Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new CompanyBadRequestException("해당 업체를 찾을 수 없습니다."));

    // 2. 상태 변경 (DELETING) - 내부에서 validateNotDeleted() 호출되어 수정 중복 차단
    company.markAsDeleting();

    // 3. 삭제 예약 이벤트 발행 (Outbox)
    // 타 서비스(배송, 주문)가 이 이벤트를 구독하여 클린업 수행
    Events.trigger(OutboxEvent.of(
        "COMPANY",
        company.getId(),
        "company-deleting-topic",
        new CompanyDeletedEvent(company.getId(), company.getStatus())
    ));

    log.info("업체 삭제 예약 완료: ID={}", companyId);
  }

  /**
   * 2단계: 업체 최종 삭제 확정 (시스템/이벤트 요청)
   */
  @Transactional
  public void confirmDeleteCompany(UUID companyId) {
    // 1. 엔티티 조회 (삭제 중인 상태이므로 일반 findById로 조회 가능해야 함)
    Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new CompanyBadRequestException("최종 삭제할 업체를 찾을 수 없습니다."));

    // 2. 상태 전이 검증 (DELETING -> TERMINATED)
    company.getStatus().validateTransitionTo(CompanyStatus.TERMINATED);

    // 3. 최종 삭제 처리 (이름 변경 및 super.delete 호출)
    // 시스템 자동 확정 시에도 최초 삭제 요청자 정보를 남기기 위해 updatedBy 활용 가능
    UUID deletedBy = company.getUpdatedBy();
    company.terminate(deletedBy);

    // 4. 최종 완료 이벤트 발행 (필요 시 다른 서비스에 전파)
    Events.trigger(OutboxEvent.of(
        "COMPANY",
        company.getId(),
        "company-terminated-topic",
        new CompanyTerminatedEvent(company.getId())
    ));

    log.info("업체 최종 삭제 확정 완료: ID={}", companyId);
  }

}