package com.loopang.company_service.domain.exception;

import com.loopang.common.exception.BadRequestException;
import com.loopang.common.exception.ErrorCodeSpec;

public class CompanyBadRequestException extends BadRequestException {

  public CompanyBadRequestException(String message) {
    super(message);
  }

  public CompanyBadRequestException(String message, String field) {
    super(message, field);
  }

  public CompanyBadRequestException(ErrorCodeSpec errorCode) {
    super(errorCode);
  }
}
