package com.loopang.company_service.infrastructure.client.tmap;

import com.loopang.common.exception.BadRequestException;
import com.loopang.common.exception.InternalServerException;
import com.loopang.company_service.domain.dto.CoordinateData;
import com.loopang.company_service.domain.service.AddressProvider;
import com.loopang.company_service.infrastructure.client.tmap.dto.TMapGeoResponse;
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
    try {
      // 1. T-map API 호출
      TMapGeoResponse response = tMapFeignClient.getGeoInfo(apiKey, fullAddress, "1", "WGS84GEO");

      // 2. 검색 결과 존재 여부 검증 (추출한 validateResponse 활용)
      validateResponse(response, fullAddress);

      // 3. 응답 데이터 추출 (가장 정확도가 높은 첫 번째 결과 사용)
      var coordinate = response.getCoordinateInfo().getCoordinate().getFirst();

      // dongDoro 결정 로직: 도로명이 있으면 도로명, 없으면 법정동 사용
      String dongDoro = (coordinate.getNewRoadName() != null && !coordinate.getNewRoadName().isBlank())
          ? coordinate.getNewRoadName()
          : coordinate.getLegalDong();

      return new CoordinateData(
          Double.parseDouble(coordinate.getNewLon()),
          Double.parseDouble(coordinate.getNewLat()),
          coordinate.getCityDo(),
          coordinate.getGuGun(),
          dongDoro // 통합된 동/도로명 정보
      );

    } catch (BadRequestException e) {
      throw e;
    } catch (Exception e) {
      log.error("T-map API 호출 중 예외 발생: ", e);
      throw new InternalServerException("주소 서비스 이용 중 문제가 발생했습니다.");
    }
  }

  private void validateResponse(TMapGeoResponse response, String fullAddress) {
    if (response == null || response.getCoordinateInfo() == null ||
        CollectionUtils.isEmpty(response.getCoordinateInfo().getCoordinate())) {
      throw new BadRequestException("유효하지 않은 주소입니다: " + fullAddress);
    }

    var coordinate = response.getCoordinateInfo().getCoordinate().getFirst();

    if (coordinate.getNewLat() == null || coordinate.getNewLon() == null) {
      throw new BadRequestException("좌표 정보를 찾을 수 없습니다: " + fullAddress);
    }

    // 법정동과 도로명이 모두 없는 경우에만 에러 처리
    boolean hasDong = coordinate.getLegalDong() != null && !coordinate.getLegalDong().isBlank();
    boolean hasRoad = coordinate.getNewRoadName() != null && !coordinate.getNewRoadName().isBlank();

    if (!hasDong && !hasRoad) {
      throw new BadRequestException("동/도로명 정보를 추출할 수 없는 주소입니다: " + fullAddress);
    }
  }
}