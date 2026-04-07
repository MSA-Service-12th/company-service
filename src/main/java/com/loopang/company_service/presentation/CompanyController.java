package com.loopang.company_service.presentation;

import com.loopang.company_service.application.CompanyService;
import com.loopang.company_service.application.dto.CompanyCreateRequest;
import com.loopang.company_service.application.dto.CompanyInfoUpdateRequest;
import com.loopang.company_service.application.dto.CompanyResponse;
import com.loopang.company_service.application.dto.CompanySearchRequest;
import com.loopang.company_service.application.query.CompanyQueryService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

  private final CompanyService companyService;
  private final CompanyQueryService companyQueryService;

  // 1. 업체 생성: MASTER, HUB_MANAGER 권한 필요
  @PostMapping
  @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
  public ResponseEntity<CompanyResponse> createCompany(
      @RequestBody @Valid CompanyCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(companyService.createCompany(request));
  }

  // 2. 업체 수정: MASTER, HUB_MANAGER, COMPANY_MANAGER 권한 필요
  @PatchMapping("/{companyId}")
  @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'COMPANY_MANAGER')")
  public ResponseEntity<Void> updateCompany(
      @PathVariable UUID companyId,
      @RequestBody @Valid CompanyInfoUpdateRequest request) {
    companyService.updateCompany(companyId, request);
    return ResponseEntity.ok().build();
  }

  // 3. 업체 삭제: MASTER, HUB_MANAGER 권한 필요 (보통 업체 담당자는 삭제 불가)
  @DeleteMapping("/{companyId}")
  @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
  public ResponseEntity<Void> deleteCompany(@PathVariable UUID companyId) {
    companyService.deleteCompany(companyId);
    return ResponseEntity.noContent().build();
  }

  // 4. 상세 조회: 인증된 모든 사용자 가능
  @GetMapping("/{companyId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<CompanyResponse> getCompany(@PathVariable UUID companyId) {
    return ResponseEntity.ok(companyQueryService.getCompany(companyId));
  }

  // 5. 목록 검색: 인증된 모든 사용자 가능 (조회 서비스 내부에서 필터링)
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Page<CompanyResponse>> searchCompanies(
      CompanySearchRequest request, Pageable pageable) {
    return ResponseEntity.ok(companyQueryService.searchCompanies(request, pageable));
  }
}
