package com.loopang.company_service.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompanyType {
  SUPPLIER("공급업체"),
  RECEIVER("수령업체");

  private final String description;

  /**
   * 업체가 공급업체(SUPPLIER)인지 확인
   */
  public boolean isSupplier() {
    return this == SUPPLIER;
  }

  /**
   * 업체가 수령업체(RECEIVER)인지 확인
   */
  public boolean isReceiver() {
    return this == RECEIVER;
  }
}