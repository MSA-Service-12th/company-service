package com.loopang.company_service.domain.service;

import java.util.UUID;

/**
 * 주문 도메인과의 협업을 위한 인터페이스
 */
public interface OrderProvider {
  /**
   * 해당 업체와 관련된 '완료되지 않은' 주문이 존재하는지 확인
   * (결제 완료 ~ 배송 전 단계의 주문들)
   */
  boolean hasActiveOrders(UUID companyId);
}