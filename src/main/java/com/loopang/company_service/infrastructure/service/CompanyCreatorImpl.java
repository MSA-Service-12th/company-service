package com.loopang.company_service.infrastructure.service;

import com.loopang.company_service.domain.entity.Company;
import com.loopang.company_service.domain.exception.CompanyBadRequestException;
import com.loopang.company_service.domain.repository.CompanyRepository;
import com.loopang.company_service.domain.service.CompanyCreator;
import com.loopang.company_service.domain.vo.CompanyAddress;
import com.loopang.company_service.domain.vo.CompanyType;
import com.loopang.company_service.domain.vo.HubInfo;
import com.loopang.company_service.domain.vo.ManagerInfo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CompanyCreatorImpl implements CompanyCreator {

  private final CompanyRepository companyRepository;

  @Transactional
  public UUID save(String name, CompanyType type, CompanyAddress address, ManagerInfo manager,
      HubInfo hub) {
    // 1. 업체명 중복 체크 (Soft Delete 고려)
    if (companyRepository.existsByNameAndDeletedAtIsNull(name)) {
      throw new CompanyBadRequestException("이미 존재하는 업체 이름입니다: " + name);
    }
    try {
      Company company = Company.create(name, type, address, manager, hub);
      Company savedCompany = companyRepository.save(company);
      // flush를 강제하여 트랜잭션 종료 전 제약 조건 위반을 감지
      companyRepository.flush();
      return savedCompany.getId();
    } catch (DataIntegrityViolationException e) {
      // 찰나의 순간에 중복이 발생한 경우 (Race Condition 방어)
      throw new CompanyBadRequestException("동시에 동일한 업체명이 등록되었습니다: " + name);
    }
  }
}