package com.loopang.company_service.infrastructure.client.order;

import com.loopang.company_service.domain.service.OrderProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProviderImpl implements OrderProvider {

  private final OrderFeignClient orderClient;

  @Override
  public boolean hasActiveOrders(UUID companyId) {
    try {
      // 주문 서비스에서 완료되지 않은 모든 주문 확인
      return orderClient.checkActiveOrders(companyId);
    } catch (Exception e) {
      log.error("주문 서비스 연동 중 오류 발생 (CompanyID: {}): {}", companyId, e.getMessage());
      throw new RuntimeException("주문 상태 확인이 불가능하여 업체를 삭제할 수 없습니다.");
    }
  }
}