package com.loopang.company_service.domain.repository;

import com.loopang.company_service.domain.entity.Company;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {

  Company save(Company company);

  Optional<Company> findById(UUID id);

  boolean existsByNameAndDeletedAtIsNull(String name);

  void flush();
}
