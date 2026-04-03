package com.loopang.company_service.domain.vo;


import com.loopang.company_service.domain.exception.CompanyBadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CompanyAddress {

  @Column(nullable = false)
  private Double longitude;   // 경도

  @Column(nullable = false)
  private Double latitude;    // 위도

  @Column(length = 15)
  private String cityDo;      // 시/도

  @Column(length = 20)
  private String guGun;       // 군/구

  @Column(length = 40)
  private String dongDoro;    // 동/도로명

  @Column(length = 100)
  private String detailAddress;

  private String fullAddress;

  public static CompanyAddress create(Double lon, Double lat, String city, String gu, String dong,
      String detail, String full) {
    validateAddress(lon, lat, full);
    // 1. 필수 값 및 공백 검증
    validateNotBlank(city, "시/도");
    validateNotBlank(gu, "군/구");
    validateNotBlank(dong, "동/도로명");
    validateNotBlank(full, "전체 주소");

    // 2. DB 컬럼 길이에 맞춘 길이 검증
    validateLength(city, 15, "시/도");
    validateLength(gu, 20, "군/구");
    validateLength(dong, 40, "동/도로명");
    validateLength(detail, 100, "상세 주소");
    return new CompanyAddress(lon, lat, city, gu, dong, detail, full);
  }

  private static void validateAddress(Double lon, Double lat, String full) {
    if (lon == null || lat == null || full == null) {
      throw new CompanyBadRequestException("주소 필수 값이 누락되었습니다.");
    }
  }

  private static void validateNotBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new CompanyBadRequestException(fieldName + " 정보는 필수이며 공백일 수 없습니다.");
    }
  }

  private static void validateLength(String value, int max, String fieldName) {
    if (value != null && value.length() > max) {
      throw new CompanyBadRequestException(fieldName + " 정보가 허용된 길이(" + max + "자)를 초과했습니다.");
    }
  }
}
