package com.taskmanagementsystem.tms.exception;

import lombok.Getter;

/**
 * This class is used to represent the business errors that can occur during the execution of the application.
 * It contains a list of {@link BusinessError} objects, which represent the different types of errors that can occur.
 */
@Getter
public class BusinessException extends RuntimeException {

  private final BusinessError businessError;

  public BusinessException(BusinessError businessError) {
    this.businessError = businessError;
  }

  public BusinessException(String debugMessage, BusinessError businessError) {
    super(debugMessage);
    this.businessError = businessError;
  }

  public BusinessException(String debugMessage, Throwable ex, BusinessError businessError) {
    super(debugMessage, ex);
    this.businessError = businessError;
  }

  @Override
  public String toString() {
    return "BusinessException {errors = " + businessError + '}';
  }
}
