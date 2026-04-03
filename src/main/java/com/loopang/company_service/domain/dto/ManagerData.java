package com.loopang.company_service.domain.dto;

import java.util.UUID;

/**
 * 업체 담당자 식별을 위한 최소 도메인 DTO
 */
public record ManagerData(
    UUID id,
    String name
) {

  public ManagerData {
    if (id == null) {
      throw new IllegalArgumentException("담당자 ID는 필수입니다.");
    }

    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("담당자명은 필수입니다.");
    }
  }
}
