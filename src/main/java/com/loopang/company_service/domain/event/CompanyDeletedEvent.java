package com.loopang.company_service.domain.event;

import com.loopang.company_service.domain.vo.CompanyStatus;
import java.util.UUID;

/**
 * 업체 삭제 예약 이벤트 타 서비스에게 해당 업체 관련 데이터 정리를 요청합니다.
 */
public record CompanyDeletedEvent(
    UUID companyId,
    CompanyStatus status // DELETING 상태임을 전달
) {

}