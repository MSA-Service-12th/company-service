package com.loopang.company_service.domain.entity;

import com.loopang.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_company_deletion_inbox",
    uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "service_name"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyDeletionInbox extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "company_id", nullable = false)
  private UUID companyId;

  @Column(name = "service_name", nullable = false)
  private String serviceName; // "DELIVERY", "ORDER", "PRODUCT"

  public CompanyDeletionInbox(UUID companyId, String serviceName) {
    this.companyId = companyId;
    this.serviceName = serviceName;
  }
}
