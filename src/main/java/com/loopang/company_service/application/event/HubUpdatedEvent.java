package com.loopang.company_service.application.event;

import java.util.UUID;

// Hub 서비스에서 발행할 이벤트 (업체 서비스에서 구독)
public record HubUpdatedEvent(
    UUID hubId,
    String hubName,
    UUID updatedBy
) {

}
