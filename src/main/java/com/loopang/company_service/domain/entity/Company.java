package com.loopang.company_service.domain.entity;

import com.loopang.common.domain.BaseUserEntity;
import com.loopang.company_service.domain.exception.CompanyBadRequestException;
import com.loopang.company_service.domain.exception.CompanyForbiddenException;
import com.loopang.company_service.domain.vo.CompanyAddress;
import com.loopang.company_service.domain.vo.CompanyStatus;
import com.loopang.company_service.domain.vo.CompanyType;
import com.loopang.company_service.domain.vo.HubInfo;
import com.loopang.company_service.domain.vo.ManagerInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(
    name = "p_companies",
    indexes = {
        // 1. 업체명 검색 및 중복 체크용
        @Index(name = "idx_company_name_status", columnList = "deleted_at, name, status"),

        // 2. 허브별/타입별/상태별 복합 필터링용
        @Index(name = "idx_company_hub_type_status", columnList = "hub_id, deleted_at, type, status"),

        // 3. 기본 목록 조회 시 최신순 정렬용
        @Index(name = "idx_company_list_order", columnList = "deleted_at, created_at DESC")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
/* Soft Delete 설정 */
@SQLRestriction("deleted_at IS NULL")
public class Company extends BaseUserEntity {

  // 임의로 정한 인젝션 방지용 이름 규칙
  // (, ), [ , ], &, -,  _ , . 특수문자만 가능
  //공백 및 한글, 영문, 숫자 포함
  //최대 100자 (String)
  private static final String NAME_REGEX = "^[a-zA-Z0-9가-힣()\\[\\]&\\-_ .]{1,100}$";

  private static final Pattern COMPANY_NAME_PATTERN = Pattern.compile(
      NAME_REGEX);

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, length = 100, unique = true)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CompanyType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CompanyStatus status;

  /* 업체 주소 (T-map API 데이터 포함 VO) */
  @Embedded
  private CompanyAddress address;

  /* 담당자 정보 VO */
  @Embedded
  private ManagerInfo manager;

  /* 관리 허브 정보 VO */
  @Embedded
  private HubInfo hub;

  @Builder
  private Company(String name, CompanyType type,
      CompanyAddress address, ManagerInfo manager, HubInfo hub) {
    // 무결성 검증
    validateRequiredFields(name, type, address, hub);
    this.name = name.trim();
    this.type = type;
    this.status = CompanyStatus.OPEN;
    this.address = address;
    this.manager = manager;
    this.hub = hub;
  }

  private void validateRequiredFields(String name, CompanyType type, CompanyAddress address,
      HubInfo hub) {

    // 업체 이름 무결성 검증
    validateCompanyName(name);

    if (type == null) {
      throw new CompanyBadRequestException("업체 타입은 필수입니다.");
    }
    if (address == null) {
      throw new CompanyBadRequestException("업체 주소 정보는 필수입니다.");
    }
    if (hub == null || hub.getHubId() == null) {
      throw new CompanyBadRequestException("업체는 반드시 특정 허브에 소속되어야 합니다.");
    }
    if (manager == null || manager.getManagerId() == null) {
      throw new CompanyBadRequestException("업체는 반드시 관리자가 존재해야 합니다.");
    }
  }

  private void validateCompanyName(String name) {
    if (name == null || name.isBlank()) {
      throw new CompanyBadRequestException("업체 이름은 필수입니다.");
    }

    String trimmedName = name.trim();

    if (trimmedName.length() > 100) {
      throw new CompanyBadRequestException("업체 이름은 100자를 초과할 수 없습니다.");
    }
    if (!COMPANY_NAME_PATTERN.matcher(trimmedName).matches()) {
      throw new CompanyBadRequestException("업체 이름 형식이 올바르지 않습니다.");
    }
  }

  private void validateNotDeleted() {
    if (super.isDeleted()) { // BaseUserEntity의 삭제 여부 확인 메서드
      throw new CompanyForbiddenException("이미 삭제된 업체입니다.");
    }
  }

  // --- 비즈니스 로직 (Static Factory & Domain Methods) ---

  /**
   * 업체 생성 정적 팩토리 메서드
   */
  public static Company create(String name, CompanyType type, CompanyAddress address,
      ManagerInfo manager, HubInfo hub) {
    return Company.builder()
        .name(name)
        .type(type)
        .address(address)
        .manager(manager)
        .hub(hub)
        .build();
  }

  /**
   * 업체 기본 정보 수정 (이름, 주소, 운영 상태)
   */
  public void updateCompanyInfo(String name, CompanyAddress address, CompanyStatus status) {

    validateNotDeleted(); // 업체 삭제 여부 최우선 검증

    // 이름 무결성 검증
    validateCompanyName(name);

    // 상태 무결성 검증
    this.status.validateTransitionTo(status);

    this.name = name.trim();
    this.address = address;
    this.status = status;
  }


  /**
   * 담당자 정보 업데이트
   */
  public void updateManagerInfo(ManagerInfo manager) {
    validateNotDeleted(); // 업체 삭제 여부 최우선 검증
    if (manager == null) {
      throw new CompanyBadRequestException("업데이트할 관리자 정보가 없습니다.");
    }
    this.manager = manager;
  }

  /**
   * 관리 허브 정보 업데이트
   */
  public void updateHubInfo(HubInfo hub) {
    validateNotDeleted(); // 업체 삭제 여부 최우선 검증
    if (hub == null) {
      throw new CompanyBadRequestException("업데이트할 허브 정보가 없습니다.");
    }
    this.hub = hub;
  }

  /**
   * 운영 가능 여부 확인
   */
  public boolean isAvailable() {
    return this.status.isOperating() && !super.isDeleted();
  }

  /**
   * 공급 업체 여부 확인
   */
  public boolean isSupplier() {
    return type.isSupplier();
  }

  /**
   * 수령 업체 여부 확인
   */
  public boolean isReceiver() {
    return type.isReceiver();
  }

  /**
   * 업체 삭제 전 삭제중 상태 변경(시간차 공격 방지)
   */
  public void markAsDeleting() {
    this.status = CompanyStatus.DELETING;
  }

  /**
   * 업체 삭제 (Soft Delete 활용)
   *
   * @param userId 삭제를 수행하는 관리자 ID
   */
  public void deleteCompany(UUID userId) {
    validateNotDeleted(); // 업체 삭제 여부 최우선 검증
    if (userId == null) {
      throw new CompanyBadRequestException("삭제를 수행하는 사용자 ID가 없습니다.");
    }
    // 업체명 뒤에 삭제 시간과 UUID 일부를 붙여 Unique 제약 조건 충돌 방지
    // 예: "루팡물류" -> "루팡물류_deleted_20260403_a1b2c3d4"
    this.name = String.format("%s_deleted_%s_%s",
        this.name,
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
        UUID.randomUUID().toString().substring(0, 8)
    );
    super.delete(userId); // BaseUserEntity의 delete(userId)
  }
}