package com.loopang.company_service.infrastructure.client.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderFeignClient {
  // 해당 업체의 미완료 주문(결제완료 ~ 배송전) 존재 여부 확인
  // 일단 예시 url입니다, 각 서비스 끼리 요청할 땐 권한 제한 없이 요청할 수 있도록 따로 헤더에 명시하는 값이 있을까요?
  @GetMapping("/api/v1/orders/active-check")
  boolean checkActiveOrders(@RequestParam("companyId") UUID companyId);
}