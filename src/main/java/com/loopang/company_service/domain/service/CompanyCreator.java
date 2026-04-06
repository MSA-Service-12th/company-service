package com.loopang.company_service.domain.service;

import com.loopang.company_service.domain.entity.Company;
import com.loopang.company_service.domain.vo.CompanyAddress;
import com.loopang.company_service.domain.vo.CompanyType;
import com.loopang.company_service.domain.vo.HubInfo;
import com.loopang.company_service.domain.vo.ManagerInfo;
import java.util.UUID;

public interface CompanyCreator {

  /**
   * 업체 이름,타입,주소,관리자정보,관리허브정보를 받아 신규 업체를 저장하는 메서드
   *
   */
  Company save(String name, CompanyType type, CompanyAddress address, ManagerInfo manager,
      HubInfo hub);
}
