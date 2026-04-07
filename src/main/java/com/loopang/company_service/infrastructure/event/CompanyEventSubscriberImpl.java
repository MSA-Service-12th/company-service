package com.loopang.company_service.infrastructure.event;

import com.loopang.company_service.application.event.HubUpdatedEvent;
import com.loopang.company_service.application.event.ManagerUpdatedEvent;
import com.loopang.company_service.domain.event.CompanyEventSubscriber;
import com.loopang.company_service.domain.service.CompanyUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyEventSubscriberImpl implements CompanyEventSubscriber {

  private final CompanyUpdater companyUpdater;

  @Override
  public void handleManagerUpdate(ManagerUpdatedEvent event) {
    try {
      log.info("담당자 정보 벌크 업데이트 시작: ID={}, Name={}", event.managerId(), event.managerName());

      companyUpdater.updateManagerInfo(
          event.managerId(),
          event.managerName(),
          event.updatedBy()
      );

      log.info("담당자 정보 벌크 업데이트 완료: ID={}", event.managerId());
    } catch (Exception e) {
      // 런타임 예외 발생 시 로그를 남기고 상위(Consumer)로 던져 트랜잭션 롤백 유도
      log.error("담당자 정보 업데이트 중 오류 발생: ID={}, Error={}", event.managerId(), e.getMessage());
      throw e;
    }
  }

  @Override
  public void handleHubUpdate(HubUpdatedEvent event) {
    try {
      log.info("허브 정보 벌크 업데이트 시작: ID={}, Name={}", event.hubId(), event.hubName());

      companyUpdater.updateHubInfo(
          event.hubId(),
          event.hubName(),
          event.updatedBy()
      );

      log.info("허브 정보 벌크 업데이트 완료: ID={}", event.hubId());
    } catch (Exception e) {
      log.error("허브 정보 업데이트 중 오류 발생: ID={}, Error={}", event.hubId(), e.getMessage());
      throw e;
    }
  }
}