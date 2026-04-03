package com.loopang.company_service.domain.exception;

import com.loopang.common.exception.ForbiddenException;

public class CompanyForbidenException extends ForbiddenException {

  public CompanyForbidenException(String message) {
    super(message);
  }
}
