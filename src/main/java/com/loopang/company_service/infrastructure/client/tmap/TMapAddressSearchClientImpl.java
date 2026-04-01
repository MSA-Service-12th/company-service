package com.loopang.company_service.infrastructure.client.tmap;

import com.loopang.common.exception.BadRequestException;
import com.loopang.common.exception.InternalServerException;
import com.loopang.company_service.domain.service.AddressSearchService;
import com.loopang.company_service.domain.vo.CompanyAddress;
import com.loopang.company_service.infrastructure.client.tmap.dto.TMapGeoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class TMapAddressSearchClientImpl implements AddressSearchService {

  private final TMapFeignClient tMapFeignClient;

  @Value("${tmap.api.key}")
  private String apiKey;

  @Override
  public CompanyAddress search(String fullAddress, String detailAddress) {
    try {
      // 1. T-map API 호출
      TMapGeoResponse response = tMapFeignClient.getGeoInfo(apiKey, fullAddress, "1", "WGS84GEO");

      // 2. 검색 결과 존재 여부 검증
      validateResponse(response, fullAddress);

      // 3. 응답 데이터 추출 (가장 정확도가 높은 첫 번째 결과 사용)
      var coordinate = response.getCoordinateInfo().getCoordinate().getFirst();
      var addressInfo = coordinate.getNewAddressList().getNewAddress().getFirst();

      // 4. 도메인 VO 생성 및 반환
      // String으로 오는 위경도를 Double로 변환
      return CompanyAddress.create(Double.parseDouble(coordinate.getNewLon()),
          Double.parseDouble(coordinate.getNewLat()), coordinate.getCityDo(), coordinate.getGuGun(),
          addressInfo.getCenterDong(), detailAddress, fullAddress);

    } catch (BadRequestException e) {
      throw e;
    } catch (Exception e) {
      log.error("T-map API 호출 중 예외 발생: ", e);
      throw new InternalServerException("주소 서비스 이용 중 문제가 발생했습니다.");
    }
  }

  private void validateResponse(TMapGeoResponse response, String fullAddress) {
    if (response == null || response.getCoordinateInfo() == null || CollectionUtils.isEmpty(
        response.getCoordinateInfo().getCoordinate())) {
      throw new BadRequestException("유효하지 않은 주소입니다: " + fullAddress);
    }

    var coordinate = response.getCoordinateInfo().getCoordinate().getFirst();

    if (CollectionUtils.isEmpty(coordinate.getNewAddressList().getNewAddress())) {
      throw new BadRequestException("상세 주소 정보가 존재하지 않습니다: " + fullAddress);
    }
  }
}