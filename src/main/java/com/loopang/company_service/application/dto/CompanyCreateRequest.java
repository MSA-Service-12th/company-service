package com.loopang.company_service.application.dto;

import com.loopang.company_service.domain.vo.CompanyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyCreateRequest {

  @NotBlank(message = "업체 이름은 필수입니다.")
  @Size(max = 100, message = "업체 이름은 100자를 초과할 수 없습니다.")
  // 엔티티의 NAME_REGEX와 동일한 규칙 적용
  @Pattern(regexp = "^[a-zA-Z0-9가-힣()\\[\\]&\\-_ .]{1,100}$",
      message = "업체 이름에 허용되지 않는 특수문자가 포함되어 있습니다.")
  private String name;

  @NotNull(message = "업체 타입은 필수입니다.")
  private CompanyType type;

  @NotBlank(message = "업체 주소는 필수입니다.")
  private String fullAddress;

  @NotNull(message = "관리자 ID는 필수입니다.")
  private UUID managerId;

  @NotNull(message = "허브 ID는 필수입니다.")
  private UUID hubId;
}