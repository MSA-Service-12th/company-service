package com.loopang.company_service.domain.service;

import com.loopang.company_service.domain.dto.HubData;
import java.util.UUID;

public interface HubProvider {

  /**
   * 업체 생성 시 허브가 존재하고 업체 등록이 가능한 상태인지 검증
   */
  void validateHubForCompany(UUID hubId);

  /**
   * 업체 생성 시 업체 엔티티에 저장할 허브 정보 dto를 가져옴(유효성 검증)
   */
  HubData getHubInfo(UUID hubId);
}
