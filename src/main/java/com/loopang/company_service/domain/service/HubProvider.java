package com.loopang.company_service.domain.service;

import com.loopang.company_service.domain.dto.HubData;
import java.util.UUID;

public interface HubProvider {

  /**
   * 업체 생성 시 허브가 존재하고 업체 등록이 가능한 상태인지 검증한 뒤(유효성 검증), 업체 엔티티에 저장할 허브 정보 dto를 가져옴
   * @param hubId hub(업체 관리 허브)의 식별자
   * @return 존재하지 않거나, 관리 허브로 지정될 수 없는 경우 예외(NotFound/Forbidden)
   */
  HubData getValidHubInfo(UUID hubId);
}
