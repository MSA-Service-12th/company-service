package com.loopang.company_service.domain.vo;

import com.loopang.company_service.domain.exception.CompanyBadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HubInfo {

  @Column(name = "hub_id", nullable = false)
  private UUID hubId;

  @Column(name = "hub_name")
  private String hubName;

  public static HubInfo create(UUID hubId, String hubName) {
    // 1. Hub ID 검증
    if (hubId == null) {
      throw new CompanyBadRequestException("허브 정보 등록 시, 소속 허브 식별자(ID)는 필수 입력 값입니다.");
    }

    // 2. Hub Name 검증
    if (hubName == null || hubName.isBlank()) {
      throw new CompanyBadRequestException("허브 정보 등록 시, 소속 허브 명칭은 필수 입력 값입니다.");
    }

    return new HubInfo(hubId, hubName);
  }
}