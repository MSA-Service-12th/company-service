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
   * @return 존재하지 않거나, 업체 담당자로 지정될 수 없는 사용자일 경우 예외(NotFound/Forbidden)
   */
  ManagerData getManagerData(UUID managerId);
}