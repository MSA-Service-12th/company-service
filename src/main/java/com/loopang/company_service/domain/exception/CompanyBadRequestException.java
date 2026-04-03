package com.loopang.company_service.domain.exception;

import com.loopang.common.exception.BadRequestException;

public class CompanyBadRequestException extends BadRequestException {

  public CompanyBadRequestException(String message) {
    super(message);
  }

  public CompanyBadRequestException(String message, String field) {
    super(message, field);
  }
}
