package com.loopang.company_service.domain.service;

import java.util.List;

public interface RoleCheck {

  /**
   * @param requiredRole 검증할 권한 명칭 (null 이거나 빈 문자열일 경우 false 반환)
   */
  boolean hasRole(String requiredRole);

  /**
   * @param requiredRoles 검증할 권한 리스트 (null 이거나 빈 리스트일 경우 false 반환)
   */
  boolean hasRole(List<String> requiredRoles);
}