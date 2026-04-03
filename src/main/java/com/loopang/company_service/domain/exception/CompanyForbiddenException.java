package com.loopang.company_service.domain.exception;

import com.loopang.common.exception.ErrorCodeSpec;
import com.loopang.common.exception.ForbiddenException;

public class CompanyForbiddenException extends ForbiddenException {

  public CompanyForbiddenException() {
  }

  public CompanyForbiddenException(String message) {
    super(message);
  }

  public CompanyForbiddenException(ErrorCodeSpec errorCode) {
    super(errorCode);
  }
}
