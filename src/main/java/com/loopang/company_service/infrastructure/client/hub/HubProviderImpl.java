package com.loopang.company_service.infrastructure.client.hub;

import com.loopang.common.exception.CustomException;
import com.loopang.company_service.domain.dto.HubData;
import com.loopang.company_service.domain.exception.CompanyBadRequestException;
import com.loopang.company_service.domain.exception.CompanyInternalServerException;
import com.loopang.company_service.domain.exception.CompanyNotFoundException;
import com.loopang.company_service.domain.service.HubProvider;
import com.loopang.company_service.infrastructure.client.hub.dto.HubResponse;
import feign.FeignException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubProviderImpl implements HubProvider {

  private final HubFeignClient hubClient;

  @Override
  public HubData getValidHubInfo(UUID hubId) {
    // 1. 파라미터 검증
    if (hubId == null) {
      throw new CompanyBadRequestException("업체가 소속될 허브 식별 ID가 누락되었습니다.");
    }

    try {
      // 2. 허브 서비스 API 호출
      HubResponse response = hubClient.getHub(hubId);

      if (response == null) {
        throw new CompanyNotFoundException("해당 ID의 허브 정보를 찾을 수 없습니다.");
      }

      // 3. 도메인 데이터 변환 (필수 필드 검증 포함)
      return response.toData();

    } catch (CustomException e) {
      // HubResponse.toData() 등에서 던진 커스텀 예외 그대로 전파
      throw e;

    } catch (FeignException.NotFound e) {
      // 404 에러 대응
      throw new CompanyNotFoundException("허브 서비스에 존재하지 않는 허브입니다. ID: " + hubId);

    } catch (FeignException.Unauthorized e) {
      // 401 에러 대응
      log.error("[HubProvider] 허브 서비스 인증 실패: 토큰이 유효하지 않습니다.");
      throw new CompanyInternalServerException("허브 서비스 인증에 실패했습니다.");

    } catch (feign.RetryableException e) {
      // 타임아웃 또는 연결 실패
      log.error("[HubProvider] 네트워크 문제로 인한 연결 실패 (ID: {})", hubId);
      throw new CompanyInternalServerException("허브 서비스와의 연결이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");

    } catch (FeignException.InternalServerError e) {
      // 500 에러 대응
      log.error("[HubProvider] 허브 서비스 내부 서버 오류: {}", e.contentUTF8());
      throw new CompanyInternalServerException("허브 서비스 내부 문제로 정보를 가져올 수 없습니다.");

    } catch (FeignException e) {
      // 그 외 기타 통신 오류 (503 등)
      log.error("[HubProvider] 기타 통신 오류 (Status: {}): {}", e.status(), e.getMessage());
      throw new CompanyInternalServerException("허브 서비스 상태 확인 중 오류가 발생했습니다.");

    } catch (Exception e) {
      // 정의되지 않은 시스템 오류
      log.error("[HubProvider] 알 수 없는 오류 발생: ", e);
      throw new CompanyInternalServerException("허브 정보를 처리하는 중 예상치 못한 내부 오류가 발생했습니다.");
    }
  }
}