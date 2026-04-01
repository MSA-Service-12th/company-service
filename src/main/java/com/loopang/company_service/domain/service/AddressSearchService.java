package com.loopang.company_service.domain.service;

import com.loopang.company_service.domain.vo.CompanyAddress;

public interface AddressSearchService {
  /**
   * 전체 주소와 상세 주소를 받아 검증된 CompanyAddress VO를 생성하여 반환
   */
  CompanyAddress search(String fullAddress, String detailAddress);
}