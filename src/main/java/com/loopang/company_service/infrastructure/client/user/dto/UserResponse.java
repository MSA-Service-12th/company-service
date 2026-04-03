package com.loopang.company_service.infrastructure.client.user.dto;

import com.loopang.common.exception.ForbiddenException;
import com.loopang.company_service.domain.dto.ManagerData;
import java.util.UUID;

public record UserResponse(
    UUID userId,
    String username,
    String role // "MASTER", "HUB_MANAGER", "COMPANY_MANAGER" 등
) {

  /**
   * 도메인 계층의 ManagerData로 변환 이 과정에서 해당 유저가 '업체 담당자'로 지정될 수 있는 권한인지 검증합니다.
   */
  public ManagerData toData() {
    // 업체 담당자로 지정 가능한 권한 리스트 체크
    // (비즈니스 요구사항에 따라 MASTER나 HUB_MANAGER도 포함될 수 있음)
    if (!isAssignableRole(this.role)) {
      throw new ForbiddenException("해당 사용자는 업체 담당자로 지정할 수 있는 권한이 없습니다. (현재 권한: " + role + ")");
    }

    return new ManagerData(userId, username);
  }

  private boolean isAssignableRole(String role) {
    if (role == null) {
      return false;
    }
    // 업체 담당자로 지정 가능한 권한 정의
    return role.equals("COMPANY_MANAGER") || role.equals("MASTER") || role.equals("HUB_MANAGER");
  }
}