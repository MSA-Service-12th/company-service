package com.loopang.company_service.domain.exception;

import com.loopang.common.exception.ErrorCodeSpec;
import com.loopang.common.exception.UnAuthorizedException;

public class CompanyUnAuthorizedException extends UnAuthorizedException {

  public CompanyUnAuthorizedException() {
  }

  public CompanyUnAuthorizedException(String message) {
    super(message);
  }

  public CompanyUnAuthorizedException(ErrorCodeSpec errorCode) {
    super(errorCode);
  }
}
