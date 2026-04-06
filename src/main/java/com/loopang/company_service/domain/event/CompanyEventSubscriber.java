package com.loopang.company_service.domain.event;

import com.loopang.company_service.application.event.HubUpdatedEvent;
import com.loopang.company_service.application.event.ManagerUpdatedEvent;

public interface CompanyEventSubscriber {

  /**
   * 담당자 정보 변경 이벤트 처리
   */
  void handleManagerUpdate(ManagerUpdatedEvent event);

  /**
   * 허브 정보 변경 이벤트 처리
   */
  void handleHubUpdate(HubUpdatedEvent event);
}
