package com.loopang.company_service.infrastructure.kafka;

import com.loopang.common.messaging.IdempotentConsumer;
import com.loopang.common.util.JsonUtil;
import com.loopang.company_service.application.CompanyService;
import com.loopang.company_service.application.event.HubUpdatedEvent;
import com.loopang.company_service.application.event.ManagerUpdatedEvent;
import com.loopang.company_service.domain.event.CompanyEventSubscriber;
import com.loopang.company_service.infrastructure.event.dto.DeletionFinishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyKafkaConsumer {

  private final CompanyService companyService;
  private final JsonUtil jsonUtil;
  private final CompanyEventSubscriber eventSubscriber;

  /**
   * 담당자(User) 정보 수정 이벤트 구독
   */
  @IdempotentConsumer("company-service-manager-update")
  @KafkaListener(topics = "user-update-topic", groupId = "company-service-group")
  public void consumeManagerUpdate(ConsumerRecord<String, String> record) {
    try {
      ManagerUpdatedEvent event = jsonUtil.fromJson(record.value(), ManagerUpdatedEvent.class);
      log.info("담당자 정보 수정 이벤트 수신: {}", event.managerId());
      eventSubscriber.handleManagerUpdate(event);
    } catch (Exception e) {
      log.error("담당자 수정 이벤트 역직렬화 실패. record value: {}, error: {}", record.value(), e.getMessage());
      throw e;
    }
  }

  /**
   * 허브(Hub) 정보 수정 이벤트 구독
   */
  @IdempotentConsumer("company-service-hub-update")
  @KafkaListener(topics = "hub-update-topic", groupId = "company-service-group")
  public void consumeHubUpdate(ConsumerRecord<String, String> record) {
    try {
      HubUpdatedEvent event = jsonUtil.fromJson(record.value(), HubUpdatedEvent.class);
      log.info("허브 정보 수정 이벤트 수신: {}", event.hubId());
      eventSubscriber.handleHubUpdate(event);
    } catch (Exception e) {
      log.error("허브 수정 이벤트 역직렬화 실패. record value: {}, error: {}", record.value(), e.getMessage());
      throw e;
    }
  }

  /**
   * 배송/주문/상품 서비스의 클린업 완료 이벤트를 구독
   */
  @IdempotentConsumer("company-deletion-confirm-inbox")
  @KafkaListener(
      topics = {"delivery-cleanup-topic", "order-cleanup-topic", "product-cleanup-topic"},
      groupId = "company-service-group"
  )
  public void consumeDeleteFinished(ConsumerRecord<String, String> record) {
    // 1. 역직렬화 (실패 시 Poison Pill 방지를 위해 즉시 return)
    DeletionFinishedEvent event = deserializeEvent(record);
    if (event == null) {
      return;
    }

    log.info("클린업 신호 수신 - 서비스: {}, 업체 ID: {}, 성공: {}",
        event.getServiceName(), event.getCompanyId(), event.isSuccess());

    // 2. 실패 신호 처리 (재시도를 위해 예외 발생)
    if (!event.isSuccess()) {
      log.error("업체 클린업 실패 보고 수신: 업체 ID = {}, 서비스 = {}, 사유 = {}",
          event.getCompanyId(), event.getServiceName(), event.getErrorMessage());
      throw new RuntimeException(
          "Cleanup failed at " + event.getServiceName() + ": " + event.getErrorMessage());
    }

    // 3. 비즈니스 로직 실행 (삭제 확정)
    try {
      companyService.confirmDeleteCompany(event.getCompanyId(), event.getServiceName());
    } catch (Exception e) {
      log.error("업체 최종 삭제 확정 처리 중 오류 (재시도 예정): {}", e.getMessage());
      throw e;
    }
  }

  /**
   * 역직렬화 헬퍼 메서드
   */
  private DeletionFinishedEvent deserializeEvent(ConsumerRecord<String, String> record) {
    try {
      return jsonUtil.fromJson(record.value(), DeletionFinishedEvent.class);
    } catch (Exception e) {
      log.error("클린업 이벤트 역직렬화 실패 (Poison Pill 방지). record: {}, error: {}",
          record.value(), e.getMessage());
      return null;
    }
  }

}