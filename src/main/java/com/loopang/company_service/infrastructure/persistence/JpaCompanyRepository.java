package com.loopang.company_service.infrastructure.persistence;

import com.loopang.company_service.domain.entity.Company;
import com.loopang.company_service.domain.repository.CompanyRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCompanyRepository extends CompanyRepository, JpaRepository<Company, UUID> {

}
