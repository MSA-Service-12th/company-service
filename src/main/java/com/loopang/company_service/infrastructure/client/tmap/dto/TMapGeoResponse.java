package com.loopang.company_service.infrastructure.client.tmap.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TMapGeoResponse {

  private CoordinateInfo coordinateInfo;

  @Getter
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class CoordinateInfo {
    private String coordType;
    private String totalCount;
    private List<Coordinate> coordinate;
  }

  @Getter
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Coordinate {
    private String newLat;
    private String newLon;

    @JsonProperty("city_do")
    private String cityDo;

    @JsonProperty("gu_gun")
    private String guGun;

    private String legalDong;   // 법정동 (구주소 체계) 추가
    private String newRoadName; // 도로명 (신주소 체계)
  }
}