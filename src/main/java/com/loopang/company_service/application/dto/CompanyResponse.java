package com.loopang.company_service.application.dto;

import com.loopang.company_service.domain.entity.Company;
import com.loopang.company_service.domain.vo.CompanyStatus;
import com.loopang.company_service.domain.vo.CompanyType;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyResponse {

  private UUID id;
  private String name;
  private CompanyType type;
  private CompanyStatus status;

  // 주소 정보 평탄화
  private String fullAddress;
  private Double lat;
  private Double lon;

  // 담당자 및 허브 정보
  private UUID managerId;
  private String managerName;
  private UUID hubId;
  private String hubName;

  public static CompanyResponse from(Company company) {
    return CompanyResponse.builder()
        .id(company.getId())
        .name(company.getName())
        .type(company.getType())
        .status(company.getStatus())
        .fullAddress(company.getAddress().getFullAddress())
        .lat(company.getAddress().getLatitude())
        .lon(company.getAddress().getLongitude())
        .managerId(company.getManager().getManagerId())
        .managerName(company.getManager().getManagerName())
        .hubId(company.getHub().getHubId())
        .hubName(company.getHub().getHubName())
        .build();
  }
}