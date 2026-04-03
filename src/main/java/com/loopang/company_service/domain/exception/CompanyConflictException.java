package com.loopang.company_service.domain.exception;

import com.loopang.common.exception.ConflictException;

public class CompanyConflictException extends ConflictException {

  public CompanyConflictException(String message) {
    super(message);
  }
}
