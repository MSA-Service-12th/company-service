package com.loopang.company_service.infrastructure.client.tmap.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 여기 명시되지 않은 필드 응답값은 무시
public class TMapGeoResponse {

  private CoordinateInfo coordinateInfo;

  @Getter
  @NoArgsConstructor
  public static class CoordinateInfo {

    private List<Coordinate> coordinate;
  }

  @Getter
  @NoArgsConstructor
  public static class Coordinate {

    /* 위경도 정보 */
    private String newLat; // 위도
    private String newLon; // 경도

    /* 행정구역 정보 */
    @JsonProperty("city_do")
    private String cityDo;
    @JsonProperty("gu_gun")
    private String guGun;

    private NewAddressList newAddressList;
  }

  @Getter
  @NoArgsConstructor
  public static class NewAddressList {

    private List<NewAddress> newAddress;
  }

  @Getter
  @NoArgsConstructor
  public static class NewAddress {

    /* 동/도로명 정보 */
    private String centerDong; // 법정동명
    private String roadName;   // 도로명
  }
}