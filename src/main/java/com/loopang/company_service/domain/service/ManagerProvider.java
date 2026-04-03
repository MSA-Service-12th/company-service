package com.loopang.company_service.domain.service;

import com.loopang.company_service.domain.dto.ManagerData;
import java.util.UUID;

/**
 * 유저 서비스로부터 업체 담당자 정보를 조회하기 위한 인터페이스
 */
public interface ManagerProvider {

  /**
   * 담당자 ID를 통해 유효한 사용자인지 확인하고 기본 정보를 반환
   *
   * @param managerId 담당자(User)의 식별자
   * @return 존재하지 않거나 권한이 없는 경우 null 또는 예외 발생
   */
  ManagerData getManagerData(UUID managerId);

  /**
   * (선택 사항) 해당 유저가 '업체 관리자' 권한을 가지고 있는지 별도로 체크가 필요할 때
   */
  void validateManagerRole(UUID managerId);
}