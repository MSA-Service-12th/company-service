package com.loopang.company_service.infrastructure.client.user;

import com.loopang.common.exception.CustomException;
import com.loopang.company_service.domain.dto.ManagerData;
import com.loopang.company_service.domain.exception.CompanyBadRequestException;
import com.loopang.company_service.domain.exception.CompanyForbiddenException;
import com.loopang.company_service.domain.exception.CompanyInternalServerException;
import com.loopang.company_service.domain.exception.CompanyNotFoundException;
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
  public ManagerData getManagerData(UUID managerId) {
    if (managerId == null)
      throw new CompanyBadRequestException("지정할 담당자 식별 ID가 누락됐습니다.");
    try {
      // 1. 유저 정보 조회
      UserResponse response = userClient.getUser(managerId);

      if (response == null) {
        throw new CompanyNotFoundException("지정된 담당자 정보를 찾을 수 없습니다.");
      }

      // 사용자 권한 검증은 메서드 내부에서 이루어짐
      return response.toData();

      // 커스텀한 도메인 예외 종합 처리
    } catch (CustomException e) {
      throw e;

    } catch (FeignException.BadRequest e) {
      // 400 에러 대응
      throw new CompanyBadRequestException("잘못된 요청 정보입니다.");

    } catch (FeignException.Unauthorized e) {
      // 401 인증 실패 (토큰 만료 등)
      log.error("[UserProvider] 인증 오류: 호출 권한이 없거나 토큰이 만료되었습니다.");
      throw new CompanyForbiddenException("사용자 서비스 인증에 실패했습니다.");

    } catch (FeignException.Forbidden e) {
      // 403 에러 대응
      throw new CompanyForbiddenException("해당 사용자는 업체 담당자로 지정될 권한이 없습니다.");

    } catch (FeignException.NotFound e) {
      // 404 에러 대응
      throw new CompanyNotFoundException("유저 서비스에 등록되지 않은 사용자입니다. ID: " + managerId);

      //---- 네트워크 계층 예외
    } catch (feign.RetryableException e) {
      // 타임아웃 또는 네트워크 단절
      log.error("[UserProvider] 네트워크 타임아웃 또는 서비스 연결 실패: {}", e.getMessage());
      throw new CompanyInternalServerException("사용자 서비스 연결이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");

    } catch (FeignException.InternalServerError e) {
      // 500: 상대방 서비스 내부 폭발
      log.error("[UserProvider] 유저 서비스 내부 서버 오류: {}", e.contentUTF8());
      throw new CompanyInternalServerException("사용자 서비스 내부에서 오류가 발생했습니다.");

      // --- 그 밖의 예외
    } catch (FeignException e) {
      // 그 외 정의되지 않은 모든 Feign 관련 에러 (상태 코드 포함)
      log.error("[UserProvider] 기타 통신 오류 (Status: {}): {}", e.status(), e.getMessage());
      throw new CompanyInternalServerException("사용자 서비스 통신 중 오류가 발생했습니다.");

    } catch (Exception e) {
      log.error("[UserProvider] 알 수 없는 시스템 오류: {}", e.getMessage());
      throw new CompanyInternalServerException("담당자 정보를 처리하는 중 예상치 못한 내부 오류가 발생했습니다.");
    }
  }
}