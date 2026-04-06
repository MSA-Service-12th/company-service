package com.loopang.company_service.application.dto;

import com.loopang.company_service.domain.vo.CompanyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 1. 업체 기본 정보 수정 (이름, 상태)
@Getter
@NoArgsConstructor
public class CompanyInfoUpdateRequest {

  @NotBlank(message = "업체 이름은 공백으로 둘 수 없습니다.")
  @Size(max = 100)
  @Pattern(regexp = "^[a-zA-Z0-9가-힣()\\[\\]&\\-_ .]{1,100}$")
  private String name;

  private CompanyStatus status;
}
