package com.loopang.company_service.application.event;

import java.util.UUID;

// User 서비스에서 발행할 이벤트 (업체 서비스에서 구독)
public record ManagerUpdatedEvent(
    UUID managerId,
    String managerName,
    UUID updatedBy // 이벤트를 발생시킨 주체
) {

}
