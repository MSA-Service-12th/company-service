package com.loopang.company_service.domain.event;

import com.loopang.company_service.domain.vo.CompanyStatus;
import java.util.UUID;

/**
 * 외부 서비스(배송, 주문 등)로 전파될 업체 수정 이벤트 payload
 */
public record CompanyUpdatedEvent(
    UUID companyId,
    String name,
    CompanyStatus status
) {

}