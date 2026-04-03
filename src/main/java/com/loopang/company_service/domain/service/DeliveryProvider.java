package com.loopang.company_service.domain.service;

import java.util.UUID;

/**
 * 배송 도메인과의 협업을 위한 인터페이스
 */
public interface DeliveryProvider {

  /**
   * 해당 업체가 출발지(Supplier) 또는 목적지(Receiver)로 지정된
   * '미완료' 배송 건이 존재하는지 확인합니다.
   */
  boolean hasActiveDeliveries(UUID companyId);

  /**
   * (확장 가능성) 특정 업체로 향하고 있는 배송 목록의 상태를 일괄 확인하거나
   * 업체 정지 시 배송 도메인에 알림을 보낼 때 사용합니다.
   */
}