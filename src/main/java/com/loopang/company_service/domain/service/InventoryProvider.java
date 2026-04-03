package com.loopang.company_service.domain.service;

import java.util.UUID;

public interface InventoryProvider {

  /**
   * 업체 삭제 전, 재고가 남아있다면 삭제를 거부하기 위해 허브 내 잔여 재고가 있는지 확인 (물류 정합성)
   */
  boolean hasRemainingStock(UUID companyId);
}
