package com.loopang.company_service.infrastructure.client.order;

import com.loopang.common.exception.CustomException;
import com.loopang.company_service.domain.exception.CompanyBadRequestException;
import com.loopang.company_service.domain.exception.CompanyInternalServerException;
import com.loopang.company_service.domain.service.OrderProvider;
import feign.FeignException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProviderImpl implements OrderProvider {

  private final OrderFeignClient orderClient;

  @Override
  public boolean hasActiveOrders(UUID companyId) {
    // 1. 파라미터 검증
    if (companyId == null) {
      throw new CompanyBadRequestException("주문 상태 확인을 위한 업체 식별 ID가 누락되었습니다.");
    }

    try {
      // 2. 주문 서비스 API 호출
      // 예: GET /api/v1/orders/active-check?companyId={companyId}
      return orderClient.checkActiveOrders(companyId);

    } catch (CustomException e) {
      // 이미 정의된 도메인 예외는 그대로 상위로 던짐
      throw e;

    } catch (FeignException.NotFound e) {
      // 주문 서비스에 해당 업체 관련 주문 기록이 전혀 없는 경우
      // 활성 주문이 없는 것으로 간주하여 삭제 로직 진행 허용 (false 반환)
      log.info("[OrderProvider] 주문 서비스에 해당 업체 관련 주문 기록 없음 (ID: {})", companyId);
      return false;

    } catch (FeignException.BadRequest e) {
      log.error("[OrderProvider] 잘못된 요청 형식 (ID: {})", companyId);
      throw new CompanyBadRequestException("주문 서비스 요청 형식이 올바르지 않습니다.");

    } catch (feign.RetryableException e) {
      // 타임아웃 또는 네트워크 장애
      log.error("[OrderProvider] 주문 서비스 연결 실패 또는 타임아웃: {}", e.getMessage());
      throw new CompanyInternalServerException("주문 서비스 응답이 지연되어 상태를 확인할 수 없습니다.");

    } catch (FeignException e) {
      // 그 외 5xx 에러 등 모든 통신 에러
      log.error("[OrderProvider] 주문 서비스 통신 오류 (Status: {}): {}", e.status(), e.getMessage());
      throw new CompanyInternalServerException("주문 상태 확인 중 서비스 통신 오류가 발생했습니다.");

    } catch (Exception e) {
      log.error("[OrderProvider] 예상치 못한 시스템 오류 발생: {}", e.getMessage());
      throw new CompanyInternalServerException("주문 정보를 처리하는 중 내부 서버 오류가 발생했습니다.");
    }
  }
}