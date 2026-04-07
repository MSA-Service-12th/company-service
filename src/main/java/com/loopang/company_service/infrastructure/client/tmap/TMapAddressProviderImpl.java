package com.loopang.company_service.infrastructure.client.tmap;

import com.loopang.common.exception.CustomException;
import com.loopang.company_service.domain.dto.CoordinateData;
import com.loopang.company_service.domain.exception.CompanyBadRequestException;
import com.loopang.company_service.domain.exception.CompanyInternalServerException;
import com.loopang.company_service.domain.service.AddressProvider;
import com.loopang.company_service.infrastructure.client.tmap.dto.TMapGeoResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class TMapAddressProviderImpl implements AddressProvider {

  private final TMapFeignClient tMapFeignClient;

  @Value("${tmap.api.key}")
  private String apiKey;

  @Override
  public CoordinateData findCoordinate(String fullAddress) {
    // 1. 입력값 검증 (Fail-Fast)
    if (fullAddress == null || fullAddress.isBlank()) {
      throw new CompanyBadRequestException("좌표 변환을 위한 주소 값이 비어있습니다.");
    }

    try {
      // 2. T-map API 호출
      TMapGeoResponse response = tMapFeignClient.getGeoInfo(apiKey, fullAddress, "1", "WGS84GEO");

      // 3. 응답 데이터 구조 검증
      validateResponse(response, fullAddress);

      // 4. 데이터 추출 및 변환
      var coordinate = response.getCoordinateInfo().getCoordinate().getFirst();

      // 문자열 좌표를 double로 안전하게 변환
      double lon = safeParseDouble(coordinate.getNewLon(), "longitude (newLon)");
      double lat = safeParseDouble(coordinate.getNewLat(), "latitude (newLat)");

      String dongDoro =
          (coordinate.getNewRoadName() != null && !coordinate.getNewRoadName().isBlank())
              ? coordinate.getNewRoadName()
              : coordinate.getLegalDong();

      return new CoordinateData(
          lon,
          lat,
          coordinate.getCityDo(),
          coordinate.getGuGun(),
          dongDoro,
          coordinate.getDetailAddress()
      );

    } catch (CustomException e) {
      // validateResponse에서 던진 예외 그대로 전파
      throw e;

    } catch (FeignException.Unauthorized | FeignException.Forbidden e) {
      // API Key 인증 실패 또는 권한(쿼터 초과 등) 문제
      log.error("[TMapProvider] API 키 인증 실패 또는 사용량 제한 초과: {}", e.getMessage());
      throw new CompanyInternalServerException("주소 서비스 인증 오류가 발생했습니다. 관리자에게 문의하세요.");

    } catch (FeignException.NotFound e) {
      // T-map 서버에서 404를 던진 경우 (주소가 없는 경우 포함)
      throw new CompanyBadRequestException("해당 주소를 찾을 수 없습니다: " + fullAddress);

    } catch (feign.RetryableException e) {
      // 외부망 연결 타임아웃
      log.error("[TMapProvider] T-map 서버 연결 타임아웃: {}", e.getMessage());
      throw new CompanyInternalServerException("주소 변환 서비스 응답이 지연되고 있습니다.");

    } catch (Exception e) {
      log.error("[TMapProvider] T-map API 호출 중 예상치 못한 오류 발생: ", e);
      throw new CompanyInternalServerException("주소 서비스 이용 중 내부 오류가 발생했습니다.");
    }
  }

  // 숫자 변환 시 발생할 수 있는 NumberFormatException 방지
  private double safeParseDouble(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      log.error("[TMap] 좌표 필드({}) 값이 비어있습니다.", fieldName);
      throw new CompanyInternalServerException("T-map 응답에서 필수 좌표 정보를 찾을 수 없습니다.");
    }
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      log.error("[TMap] 좌표 필드({}) 숫자 변환 실패. 수신값: {}", fieldName, value);
      throw new CompanyInternalServerException("유효하지 않은 좌표 형식이 수신되었습니다.");
    }
  }

  private void validateResponse(TMapGeoResponse response, String fullAddress) {
    if (response == null || response.getCoordinateInfo() == null ||
        CollectionUtils.isEmpty(response.getCoordinateInfo().getCoordinate())) {
      throw new CompanyBadRequestException("유효하지 않거나 결과가 없는 주소입니다: " + fullAddress);
    }

    var coordinate = response.getCoordinateInfo().getCoordinate().getFirst();

    // 좌표값 존재 여부 확인
    if (coordinate.getNewLat() == null || coordinate.getNewLat().isBlank() ||
        coordinate.getNewLon() == null || coordinate.getNewLon().isBlank()) {
      throw new CompanyBadRequestException("주소에 대한 좌표 정보를 추출할 수 없습니다: " + fullAddress);
    }

    // 법정동/도로명 존재 여부 확인
    boolean hasDong = coordinate.getLegalDong() != null && !coordinate.getLegalDong().isBlank();
    boolean hasRoad = coordinate.getNewRoadName() != null && !coordinate.getNewRoadName().isBlank();

    if (!hasDong && !hasRoad) {
      throw new CompanyBadRequestException("주소 체계가 올바르지 않은 지역입니다: " + fullAddress);
    }
  }
}