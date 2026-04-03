package com.loopang.company_service.domain.dto;

import com.loopang.company_service.domain.exception.CompanyBadRequestException;

/**
 * T-map FullTextGeocoding 응답에서 추출한 도메인용 좌표 데이터
 */
public record CoordinateData(
    Double longitude,   // newLon -> longitude
    Double latitude,    // newLat -> latitude
    String cityDo,      // city_do -> cityDo
    String guGun,       // gu_gun -> guGun
    String dongDoro     // newRoadName -> dongDoro (도로명 위주)
) {
  public CoordinateData {
    if (longitude == null || latitude == null) {
      throw new CompanyBadRequestException("위경도 정보는 필수입니다.");
    }
  }
}