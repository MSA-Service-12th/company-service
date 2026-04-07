package com.loopang.company_service.application.dto;

import com.loopang.company_service.domain.dto.CompanySearchCondition;
import com.loopang.company_service.domain.vo.CompanyStatus;
import com.loopang.company_service.domain.vo.CompanyType;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter // 쿼리 파라미터 바인딩을 위해 Setter가 필요할 수 있음
@NoArgsConstructor
public class CompanySearchRequest {

  @Size(max = 20, message = "검색어는 20자 이내여야 합니다.")
  private String keyword;

  private String name;

  private CompanyType type;

  private CompanyStatus status;

  private String managerName;

  private UUID hubId;

  /**
   * 애플리케이션 DTO를 도메인 조건 객체(Condition)로 변환
   */
  public CompanySearchCondition toCondition() {
    return new CompanySearchCondition(
        keyword,
        name,
        type,
        status,
        managerName,
        hubId
    );
  }
}