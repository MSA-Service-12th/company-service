package com.loopang.company_service.infrastructure.service;

import com.loopang.company_service.domain.entity.Company;
import com.loopang.company_service.domain.repository.CompanyRepository;
import com.loopang.company_service.domain.service.CompanyCreator;
import com.loopang.company_service.domain.vo.CompanyAddress;
import com.loopang.company_service.domain.vo.CompanyType;
import com.loopang.company_service.domain.vo.HubInfo;
import com.loopang.company_service.domain.vo.ManagerInfo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CompanyCreatorImpl implements CompanyCreator {

  private final CompanyRepository companyRepository;

  @Transactional
  public UUID save(String name, CompanyType type, CompanyAddress address, ManagerInfo manager,
      HubInfo hub) {
    Company company = Company.create(name, type, address, manager, hub);
    return companyRepository.save(company).getId();
  }
}