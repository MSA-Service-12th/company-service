package com.loopang.company_service.domain.dto;

import com.loopang.company_service.domain.vo.CompanyStatus;
import com.loopang.company_service.domain.vo.CompanyType;
import java.util.UUID;

public record CompanySearchCondition(String keyword,
                                     String name,
                                     CompanyType type,
                                     CompanyStatus status,
                                     String managerName,
                                     UUID hubId) {

}
