package com.loopang.company_service.domain.service;

import com.loopang.company_service.domain.dto.CoordinateData;

public interface AddressProvider {

  /**
   * 전체 주소를 받아 검증된 CoordinateData(domain dto)를 생성하여 반환
   */
  CoordinateData findCoordinate(String fullAddress);
}