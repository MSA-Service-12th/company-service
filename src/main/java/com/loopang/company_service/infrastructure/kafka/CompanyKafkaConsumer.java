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
    } catch (RuntimeException e) {
      log.error("담당자 수정 이벤트 역직렬화 실패. record value: {}, error: {}", record.value(), e.getMessage());
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
    } catch (RuntimeException e) {
      log.error("허브 수정 이벤트 역직렬화 실패. record value: {}, error: {}", record.value(), e.getMessage());
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
    try {
      DeletionFinishedEvent event = jsonUtil.fromJson(record.value(), DeletionFinishedEvent.class);

      log.info("클린업 완료 신호 수신 - 서비스: {}, 업체 ID: {}, 성공 여부: {}",
          event.getServiceName(), event.getCompanyId(), event.isSuccess());

      if (event.isSuccess()) {
        companyService.confirmDeleteCompany(event.getCompanyId());
      } else {
        log.error("업체 클린업 실패 보고 수신: 업체 ID = {}, 원인 서비스 = {}",
            event.getCompanyId(), event.getServiceName());
      }
    } catch (RuntimeException e) {
      log.error("클린업 완료 이벤트 역직렬화 실패. record value: {}, error: {}", record.value(), e.getMessage());
      // 중요: 삭제 확정 로직에서 예외가 발생하면 업체가 'DELETING' 상태로 영원히 남을 수 있으므로,
      // 이 경우 로그를 아주 상세히 남겨 수동 조치가 가능하게 해야 합니다.
    }
  }
}