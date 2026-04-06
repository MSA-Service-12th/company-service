package com.loopang.company_service.domain.event;

import java.util.UUID;

/**
 * 업체 최종 삭제 완료 이벤트 모든 클린업 프로세스가 종료되고 업체가 최종 삭제되었음을 알립니다.
 */
public record CompanyTerminatedEvent(
    UUID companyId
) {

}