package com.loopang.company_service.infrastructure.persistence;

import com.loopang.company_service.domain.entity.CompanyDeletionInbox;
import com.loopang.company_service.domain.repository.CompanyDeletionInboxRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCompanyDeletionInboxRepository
    extends JpaRepository<CompanyDeletionInbox, UUID>, CompanyDeletionInboxRepository {

  boolean existsByCompanyIdAndServiceName(UUID companyId, String serviceName);

  long countByCompanyId(UUID companyId);

  // 벌크 삭제를 위한 설정
  @Modifying(clearAutomatically = true) // 벌크 연산 후 영속성 컨텍스트를 비워 데이터 불일치 방지
  @Query("delete from CompanyDeletionInbox c where c.companyId = :companyId")
  void deleteByCompanyId(@Param("companyId") UUID companyId);
}