package com.loopang.company_service.infrastructure.client.user;

import com.loopang.common.exception.InternalServerException;
import com.loopang.common.exception.NotFoundException;
import com.loopang.company_service.domain.dto.ManagerData;
import com.loopang.company_service.domain.service.ManagerProvider;
import com.loopang.company_service.infrastructure.client.user.dto.UserResponse;
import feign.FeignException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ManagerProviderImpl implements ManagerProvider {

  private final UserFeignClient userClient;

  @Override
  public ManagerData getManagerData(UUID userId) {
    try {
      // 1. 유저 정보 조회
      UserResponse response = userClient.getUser(userId);

      if (response == null) {
        throw new NotFoundException("지정된 담당자 정보를 찾을 수 없습니다.");
      }

      // 사용자 권한 검증은 메서드 내부에서 이루어짐
      return response.toData();

    } catch (FeignException.NotFound e) {
      throw new NotFoundException("유저 서비스에 해당 사용자가 존재하지 않습니다. ID: " + userId);
    } catch (Exception e) {
      log.error("[ManagerProvider] 유저 서비스 호출 실패: {}", e.getMessage());
      throw new InternalServerException("유저 정보를 확인하는 중 오류가 발생했습니다.");
    }
  }
}