package com.loopang.company_service.domain.dto;

import com.loopang.company_service.domain.exception.CompanyBadRequestException;
import java.util.UUID;

/**
 * 업체 소속 허브 확인을 위한 최소 도메인 DTO
 */
public record HubData(
    UUID id,
    String name
) {

  public HubData {
    if (id == null) {
      throw new CompanyBadRequestException("허브 ID는 필수입니다.");
    }

    if (name == null || name.isBlank()) {
      throw new CompanyBadRequestException("허브명은 필수입니다.");
    }
  }
}