package com.loopang.company_service.infrastructure.event.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 타 서비스(배송, 주문 등)에서 업체 연관 데이터 삭제 완료 후 보내는 이벤트
 */
@Getter
@NoArgsConstructor
public class DeletionFinishedEvent {

  private UUID companyId;
  private String serviceName; // 어느 서비스에서 완료되었는지
  private boolean success;    // 정리 성공 여부
}