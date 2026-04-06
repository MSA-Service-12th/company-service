package com.loopang.company_service.infrastructure.service;

import com.loopang.company_service.domain.service.CompanyUpdater;
import com.loopang.company_service.infrastructure.persistence.JpaCompanyRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CompanyUpdaterImpl implements CompanyUpdater {

  private final JpaCompanyRepository companyRepository;

  @Override
  @Transactional
  public void updateManagerInfo(UUID managerId, String managerName, UUID updatedBy) {
    // 벌크 쿼리로 데이터와 수정자 정보를 한 번에 업데이트
    companyRepository.updateManagerNameBulk(managerId, managerName, updatedBy);
  }

  @Override
  @Transactional
  public void updateHubInfo(UUID hubId, String hubName, UUID updatedBy) {
    companyRepository.updateHubNameBulk(hubId, hubName, updatedBy);
  }
}