package com.loopang.company_service.domain.service;

import java.util.UUID;

public interface CompanyUpdater {

  // 담당자 정보 업데이트 (수정자 ID 포함)
  void updateManagerInfo(UUID managerId, String managerName, UUID updatedBy);

  // 허브 정보 업데이트 (수정자 ID 포함)
  void updateHubInfo(UUID hubId, String hubName, UUID updatedBy);
}