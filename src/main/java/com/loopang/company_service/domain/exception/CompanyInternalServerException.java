package com.loopang.company_service.domain.exception;

import com.loopang.common.exception.ErrorCodeSpec;
import com.loopang.common.exception.InternalServerException;

public class CompanyInternalServerException extends InternalServerException {

  public CompanyInternalServerException() {
  }

  public CompanyInternalServerException(String message) {
    super(message);
  }

  public CompanyInternalServerException(ErrorCodeSpec errorCode) {
    super(errorCode);
  }
}
