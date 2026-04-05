package com.loopang.company_service.domain.vo;

import com.loopang.company_service.domain.exception.CompanyBadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ManagerInfo {

  @Column(name = "manager_id")
  private UUID managerId;

  @Column(name = "manager_name")
  private String managerName;

  private ManagerInfo(UUID managerId, String managerName) {
    this.managerId = managerId;
    this.managerName = managerName;
  }

  public static ManagerInfo create(UUID managerId, String managerName) {
    // 1. Manager ID 검증
    if (managerId == null) {
      throw new CompanyBadRequestException("업체 관리자 정보 등록 시, 업체 담당자 식별자(ID)는 필수입니다.");
    }

    // 2. Manager Name 검증
    if (managerName == null || managerName.isBlank()) {
      throw new CompanyBadRequestException("업체 관리자 정보 등록 시, 업체 담당자 이름은 필수입니다.");
    }

    return new ManagerInfo(managerId, managerName);
  }
}