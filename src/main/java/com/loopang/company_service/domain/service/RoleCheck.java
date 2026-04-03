package com.loopang.company_service.domain.service;

import java.util.List;

/**
 * 도메인 행위별 권한 검증을 위한 핵심 인터페이스
 */
public interface RoleCheck {

  // 단일 권한 확인: 특정 역할 하나만 허용할 때
  boolean hasRole(String requiredRole);

  // 다중 권한 확인: 여러 역할 중 하나라도 포함될 때 (예: MASTER 또는 HUB_MANAGER)
  boolean hasRole(List<String> requiredRoles);
}