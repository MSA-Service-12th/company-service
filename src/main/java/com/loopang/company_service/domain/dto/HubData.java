package com.loopang.company_service.domain.dto;

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
      throw new IllegalArgumentException("허브 ID는 필수입니다.");
    }
  }
}