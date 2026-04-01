package com.loopang.company_service.infrastructure.client.tmap;

import com.loopang.company_service.infrastructure.client.tmap.dto.TMapGeoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "t-map-client", url = "https://apis.openapi.sk.com/tmap")
public interface TMapFeignClient {

  @GetMapping(value = "/geo/fullAddrGeo", consumes = "application/json")
  TMapGeoResponse getGeoInfo(
      @RequestHeader("appKey") String appKey,
      @RequestParam("fullAddr") String fullAddr,
      @RequestParam(value = "version", defaultValue = "1") String version,
      @RequestParam(value = "coordType", defaultValue = "WGS84GEO") String coordType
  );
}