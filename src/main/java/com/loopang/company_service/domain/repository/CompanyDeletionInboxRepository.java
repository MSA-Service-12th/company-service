package com.loopang.company_service.domain.repository;

import com.loopang.company_service.domain.entity.CompanyDeletionInbox;
import java.util.UUID;

/**
 * 업체 삭제 시 체크해야 할 삭제 완료 이벤트(신호) 목록 관리용 repository
 */
public interface CompanyDeletionInboxRepository {

  /**
   * 신호 저장 메서드
   */
  CompanyDeletionInbox save(CompanyDeletionInbox inbox);

  /**
   * 특정 업체의 특정 서비스 신호가 이미 존재하는지 확인 (중복 방지)
   */
  boolean existsByCompanyIdAndServiceName(UUID companyId, String serviceName);

  /**
   * 특정 업체에 대해 수집된 클린업 신호의 총 개수 확인
   */
  long countByCompanyId(UUID companyId);

  /**
   * 최종 삭제 완료 후 해당 업체의 인박스 데이터 정리
   */
  void deleteByCompanyId(UUID companyId);
}