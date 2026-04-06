package com.loopang.company_service.infrastructure.persistence;

import com.loopang.company_service.domain.entity.CompanyDeletionInbox;
import com.loopang.company_service.domain.repository.CompanyDeletionInboxRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCompanyDeletionInboxRepository
    extends JpaRepository<CompanyDeletionInbox, UUID>, CompanyDeletionInboxRepository {

  boolean existsByCompanyIdAndServiceName(UUID companyId, String serviceName);

  long countByCompanyId(UUID companyId);

  void deleteByCompanyId(UUID companyId);
}