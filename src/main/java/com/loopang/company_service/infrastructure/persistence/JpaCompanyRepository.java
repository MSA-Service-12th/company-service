package com.loopang.company_service.infrastructure.persistence;

import com.loopang.company_service.domain.entity.Company;
import com.loopang.company_service.domain.repository.CompanyRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaCompanyRepository extends CompanyRepository, JpaRepository<Company, UUID> {

  boolean existsByNameAndDeletedAtIsNull(
      @NotBlank(message = "업체 이름은 필수입니다.") @Size(max = 100, message = "업체 이름은 100자를 초과할 수 없습니다.") @Pattern(regexp = "^[a-zA-Z0-9가-힣()\\[\\]&\\-_ .]{1,100}$",
          message = "업체 이름에 허용되지 않는 특수문자가 포함되어 있습니다.") String name);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE Company c SET " +
      "c.manager.managerName = :newName, " +
      "c.updatedBy = :updatedBy, " +
      "c.updatedAt = CURRENT_TIMESTAMP " + // updatedAt도 수동 갱신(벌크 업데이트는 영속성 관리 x)
      "WHERE c.manager.managerId = :managerId AND c.deletedAt IS NULL")
  int updateManagerNameBulk(
      @Param("managerId") UUID managerId,
      @Param("newName") String newName,
      @Param("updatedBy") UUID updatedBy
  );

  @Modifying(clearAutomatically = true)
  @Query("UPDATE Company c SET " +
      "c.hub.hubName = :newName, " +
      "c.updatedBy = :updatedBy, " +
      "c.updatedAt = CURRENT_TIMESTAMP " +
      "WHERE c.hub.hubId = :hubId AND c.deletedAt IS NULL")
  int updateHubNameBulk(
      @Param("hubId") UUID hubId,
      @Param("newName") String newName,
      @Param("updatedBy") UUID updatedBy
  );
}
