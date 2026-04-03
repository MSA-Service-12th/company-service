package com.loopang.company_service.infrastructure.client.delivery;

import com.loopang.common.exception.InternalServerException;
import com.loopang.company_service.domain.exception.CompanyBadRequestException;
import com.loopang.company_service.domain.service.DeliveryProvider;
import feign.FeignException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryProviderImpl implements DeliveryProvider {

  private final DeliveryFeignClient deliveryClient;

  @Override
  public boolean hasActiveDeliveries(UUID companyId) {
    if (companyId == null) {
      throw new CompanyBadRequestException("요청 대상의 업체 ID는 필수 사항입니다.");
    }
    try {
      // 배송 서비스에서 해당 업체의 활성 배송 건수나 존재 여부를 반환하는 API 호출
      // 예: GET /api/v1/deliveries/active-check?companyId={companyId}
      return deliveryClient.checkActiveDeliveries(companyId);

      // FeignException은 응답 코드를 기준으로 예외를 분류 ex. FeignException.NotFound == HttpCode 404
    } catch (FeignException.NotFound e) {
      // 배송 서비스 측에서 해당 업체에 대한 기록이 아예 없는 경우
      // 배송 데이터가 없으므로 활성 배송이 없는(false) 것으로 간주하거나,
      // 서비스 정책에 따라 예외 또는 false로 처리
      log.info("[DeliveryProvider] 배송 서비스에 업체 기록이 존재하지 않음 (ID: {})", companyId);
      return false;

    } catch (FeignException.BadRequest e) {
      // 호출 인자 오류 등 (400)
      log.error("[DeliveryProvider] 배송 서비스 호출 인자 오류 (ID: {})", companyId);
      throw new CompanyBadRequestException("배송 서비스 요청 형식이 올바르지 않습니다.");

    } catch (FeignException e) {
      // 그 외 Feign 통신 관련 오류 (500, 503, Timeout 등)
      log.error("[DeliveryProvider] 배송 서비스 통신 장애 (Status: {}, ID: {})", e.status(), companyId);
      throw new InternalServerException("배송 서비스와 통신할 수 없어 업체 삭제 진행이 불가능합니다.");

    } catch (Exception e) {
      // 예측하지 못한 런타임 오류
      log.error("[DeliveryProvider] 알 수 없는 오류 발생 (ID: {})", companyId, e);
      throw new InternalServerException("배송 상태 확인 중 서버 내부 오류가 발생했습니다.");
    }
  }
}
