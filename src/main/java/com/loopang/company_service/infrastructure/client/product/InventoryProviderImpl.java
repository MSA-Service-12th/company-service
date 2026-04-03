package com.loopang.company_service.infrastructure.client.product;

import com.loopang.common.exception.CustomException;
import com.loopang.company_service.domain.exception.CompanyBadRequestException;
import com.loopang.company_service.domain.exception.CompanyInternalServerException;
import com.loopang.company_service.domain.service.InventoryProvider;
import feign.FeignException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryProviderImpl implements InventoryProvider {

  private final ProductFeignClient productClient;

  @Override
  public boolean hasRemainingStock(UUID companyId) {
    // 1. 파라미터 검증
    if (companyId == null) {
      throw new CompanyBadRequestException("재고 확인을 위한 업체 식별 ID가 누락되었습니다.");
    }

    try {
      // 2. 상품(재고) 서비스 API 호출
      // 예: GET /api/v1/products/exists?companyId={companyId}
      return productClient.checkRemainingStock(companyId);

    } catch (CustomException e) {
      // 도메인 예외가 이미 발생했다면 그대로 전파
      throw e;

    } catch (FeignException.NotFound e) {
      // 상품 서비스에 해당 업체에 대한 상품 기록이 아예 없는 경우
      // 삭제 가능한 상태로 간주하여 false 반환
      log.info("[InventoryProvider] 상품 서비스에 해당 업체의 등록 상품이 없음 (ID: {})", companyId);
      return false;

    } catch (FeignException.BadRequest e) {
      log.error("[InventoryProvider] 잘못된 요청 (ID: {})", companyId);
      throw new CompanyBadRequestException("상품 서비스 요청 형식이 올바르지 않습니다.");

    } catch (feign.RetryableException e) {
      log.error("[InventoryProvider] 네트워크 타임아웃 또는 연결 실패: {}", e.getMessage());
      throw new CompanyInternalServerException("상품 서비스 연결이 원활하지 않아 재고 확인이 불가능합니다.");

    } catch (FeignException e) {
      // 그 외 500 에러 등을 포함한 통신 오류
      log.error("[InventoryProvider] 상품 서비스 통신 오류 (Status: {}): {}", e.status(), e.getMessage());
      throw new CompanyInternalServerException("상품 서비스 상태 확인 중 오류가 발생했습니다.");

    } catch (Exception e) {
      log.error("[InventoryProvider] 예상치 못한 시스템 오류: {}", e.getMessage());
      throw new CompanyInternalServerException("재고 정보를 처리하는 중 내부 오류가 발생했습니다.");
    }
  }
}