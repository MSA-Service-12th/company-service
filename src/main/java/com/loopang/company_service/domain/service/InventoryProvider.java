package com.loopang.company_service.domain.service;

import java.util.UUID;

public interface InventoryProvider {

  /**
   * 공급 업체 삭제 전, 재고가 남아있다면 삭제를 거부하기 위해 허브 내 잔여 재고가 있는지 확인 (물류 정합성)
   * @param companyId company(상품을 납품한 공급업체)의 식별자
   * @return 존재하지 않으면 false, 해당 업체의 재고 존재 시 true
   */
  boolean hasRemainingStock(UUID companyId);
}
