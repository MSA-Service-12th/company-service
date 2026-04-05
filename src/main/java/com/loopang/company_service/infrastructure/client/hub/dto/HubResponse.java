package com.loopang.company_service.infrastructure.client.hub.dto;

import com.loopang.common.exception.BadRequestException;
import com.loopang.company_service.domain.dto.HubData;
import com.loopang.company_service.domain.exception.CompanyBadRequestException;
import java.util.UUID;

public record HubResponse(
    UUID hubId,
    String name,
    String centerLocation // 허브의 주소나 위치 정보 (필요시)
) {

  /**
   * 도메인 계층의 HubData로 변환하며 데이터의 유효성을 검증합니다.
   */
  public HubData toData() {
    // 1. 필수 값 존재 여부 검증 (Fail-Fast)
    if (hubId == null) {
      throw new CompanyBadRequestException("허브 서비스로부터 유효한 허브 식별자를 받지 못했습니다.");
    }

    if (name == null || name.isBlank()) {
      throw new CompanyBadRequestException("허브 서비스로부터 유효한 허브 명칭을 받지 못했습니다.");
    }

    // 2. 깨끗한 도메인 DTO로 변환하여 반환
    return new HubData(hubId, name);
  }
}