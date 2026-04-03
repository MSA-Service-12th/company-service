package com.loopang.company_service.infrastructure.client.hub;

import com.loopang.common.exception.InternalServerException;
import com.loopang.common.exception.NotFoundException;
import com.loopang.company_service.domain.dto.HubData;
import com.loopang.company_service.domain.service.HubProvider;
import com.loopang.company_service.infrastructure.client.hub.dto.HubResponse;
import feign.FeignException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HubProviderImpl implements HubProvider {

  private final HubFeignClient hubClient;

  @Override
  public HubData getValidHubInfo(UUID hubId) {
    try {
      HubResponse response = hubClient.getHub(hubId);

      if (response == null) {
        throw new NotFoundException("존재하지 않는 허브입니다. ID: " + hubId);
      }

      // toData() 호출 시 내부 검증 로직이 실행됩니다.
      return response.toData();

    } catch (FeignException.NotFound e) {
      throw new NotFoundException("허브 서비스에서 해당 정보를 찾을 수 없습니다. ID: " + hubId);
    } catch (Exception e) {
      log.error("[HubProvider] 허브 서비스 호출 중 알 수 없는 오류 발생: {}", e.getMessage());
      throw new InternalServerException("허브 정보 확인 중 서버 오류가 발생했습니다.");
    }
  }
}