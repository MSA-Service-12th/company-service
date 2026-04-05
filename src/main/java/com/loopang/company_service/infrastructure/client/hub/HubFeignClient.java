package com.loopang.company_service.infrastructure.client.hub;

import com.loopang.company_service.infrastructure.client.hub.dto.HubResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub-service")
public interface HubFeignClient {

  // 일단 예시 url입니다, 각 서비스 끼리 요청할 땐 권한 제한 없이 요청할 수 있도록 따로 헤더에 명시하는 값이 있을까요?
  @GetMapping("/api/v1/hubs/{hubId}")
  HubResponse getHub(@PathVariable UUID hubId);
}