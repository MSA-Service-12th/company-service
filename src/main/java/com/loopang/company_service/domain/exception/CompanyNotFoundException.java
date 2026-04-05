package com.loopang.company_service.domain.exception;

import com.loopang.common.exception.ErrorCodeSpec;
import com.loopang.common.exception.NotFoundException;

public class CompanyNotFoundException extends NotFoundException {

  public CompanyNotFoundException(String message) {
    super(message);
  }

  public CompanyNotFoundException(ErrorCodeSpec errorCode) {
    super(errorCode);
  }
}
