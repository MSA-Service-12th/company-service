package com.loopang.company_service.infrastructure.client.delivery;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.UUID;

@FeignClient(name = "delivery-service")
public interface DeliveryFeignClient {
  // 해당 업체가 출발지/목적지인 배송 중 물량이 있는지 확인
  // 일단 예시 url입니다, 각 서비스 끼리 요청할 땐 권한 제한 없이 요청할 수 있도록 따로 헤더에 명시하는 값이 있을까요?
  @GetMapping("/api/v1/deliveries/active-check")
  boolean checkActiveDeliveries(@RequestParam("companyId") UUID companyId);
}