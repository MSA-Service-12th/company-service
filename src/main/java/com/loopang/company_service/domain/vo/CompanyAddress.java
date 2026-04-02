package com.loopang.company_service.domain.vo;


import com.loopang.common.exception.BadRequestException;
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
    return new CompanyAddress(lon, lat, city, gu, dong, detail, full);
  }

  private static void validateAddress(Double lon, Double lat, String full) {
    if (lon == null || lat == null || full == null) {
      throw new BadRequestException("주소 필수 값이 누락되었습니다.");
    }
  }
}
