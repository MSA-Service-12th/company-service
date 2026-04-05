package com.loopang.company_service.domain.exception;

import com.loopang.common.exception.ConflictException;
import com.loopang.common.exception.ErrorCodeSpec;

public class CompanyConflictException extends ConflictException {

  public CompanyConflictException(String message) {
    super(message);
  }

  public CompanyConflictException(ErrorCodeSpec errorCode) {
    super(errorCode);
  }
}
