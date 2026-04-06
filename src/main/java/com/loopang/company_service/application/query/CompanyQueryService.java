package com.loopang.company_service.application.query;

import com.loopang.company_service.application.dto.CompanyResponse;
import com.loopang.company_service.application.dto.CompanySearchRequest;
import com.loopang.company_service.domain.entity.Company;
import com.loopang.company_service.domain.exception.CompanyNotFoundException;
import com.loopang.company_service.domain.repository.CompanyQueryRepository;
import com.loopang.company_service.domain.repository.CompanyRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyQueryService {

  private final CompanyRepository companyRepository;
  private final CompanyQueryRepository companyQueryRepository;

  /**
   * 업체 단건 상세 조회
   */
  public CompanyResponse getCompany(UUID companyId) {
    // 엔티티의 @SQLRestriction("deleted_at IS NULL") 덕분에 삭제된 데이터는 조회되지 않음
    Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new CompanyNotFoundException("해당 업체를 찾을 수 없습니다."));
    return CompanyResponse.from(company);
  }

  /**
   * 업체 조건부 페이징 검색
   */
  public Page<CompanyResponse> searchCompanies(CompanySearchRequest request, Pageable pageable) {
    // QueryDSL 기반 Repository의 검색 기능 호출
    return companyQueryRepository.searchCompanies(request.toCondition(), pageable)
        .map(CompanyResponse::from);
  }
}