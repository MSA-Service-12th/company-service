package com.loopang.company_service.infrastructure.client.delivery;

import com.loopang.company_service.domain.service.DeliveryProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryProviderImpl implements DeliveryProvider {

  private final DeliveryFeignClient deliveryClient;

  @Override
  public boolean hasActiveDeliveries(UUID companyId) {
    try {
      // 배송 서비스에서 해당 업체의 활성 배송 건수나 존재 여부를 반환하는 API 호출
      // 예: GET /api/v1/deliveries/active-check?companyId={companyId}
      return deliveryClient.checkActiveDeliveries(companyId);
    } catch (Exception e) {
      log.error("배송 서비스 연동 중 오류 발생 (CompanyID: {}): {}", companyId, e.getMessage());
      // 서비스 장애 시 안전을 위해 삭제를 차단하거나 예외를 던짐
      throw new RuntimeException("배송 상태 확인이 불가능하여 업체를 삭제할 수 없습니다.");
    }
  }
}