package com.loopang.company_service.domain.repository;

import com.loopang.company_service.domain.dto.CompanySearchCondition;
import com.loopang.company_service.domain.entity.Company;
import com.loopang.company_service.domain.vo.CompanyStatus;
import com.loopang.company_service.domain.vo.CompanyType;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 조회 시 구현할 repository 인터페이스
 */
public interface CompanyQueryRepository {
  /**
   * 관리자, 허브, 타입, 상태별 동적 검색 및 전체 목록 조회,
   * Soft Delete(deleted_at IS NULL) 설정 고려
   */
  Page<Company> searchCompanies(
//      String keyword,     // 종합검색 (이름/주소)
//      String name,        // 업체명 단독
//      CompanyType type,
//      CompanyStatus status,
//      String managerName, // 관리자 이름
//      UUID hubId,
      CompanySearchCondition condition,
      Pageable pageable
  );
}
