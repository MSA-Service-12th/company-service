package com.loopang.company_service.domain.entity;

import com.loopang.common.exception.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompanyStatus {

  OPEN("운영중"),
  CLOSED("운영중단");
//  ON_LEAVE("휴무"); // 고도화 고려

  private final String description;

  /**
   * 업체 운영 가능 여부 확인 메서드 비즈니스 요구사항: "운영 중" 상태일 때만 상품 주문 및 배송 처리가 가능함
   */
  public boolean isOperating() {
    return this == OPEN;
  }

  /**
   * 특정 상태로 변경 가능한지 확인 (예: CLOSED 상태에서는 다시 OPEN으로만 변경 가능 등)
   */
  public void validateTransitionTo(CompanyStatus nextStatus) {
    // 1. 동일한 상태로의 변경 허용
    if (this == nextStatus) {
      return;
    }

    // 2. 상태 전환 규칙 (현재는 모두 허용하지만 확장성을 위해 switch 구성)
    boolean isAllowed = switch (this) {
      case OPEN -> nextStatus == CLOSED;  // 영업 -> 폐업 허용
      case CLOSED -> nextStatus == OPEN;  // 폐업 -> 영업 재개 허용
      // default -> false; // 나중에 여기서 필터링
    };

    if (!isAllowed) {
      throw new BadRequestException(
          String.format("업체 상태를 %s에서 %s(으)로 변경할 수 없습니다.", this.name(), nextStatus.name())
      );
    }

  }
}