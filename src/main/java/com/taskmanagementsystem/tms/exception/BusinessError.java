package com.taskmanagementsystem.tms.exception;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This enum defines all the possible business errors that can occur in the system.
 *
 * <p>
 * The error codes are defined as constants in the enum, and each error code is associated with a
 * human-readable error message. The error codes are used to identify the type of error that has
 * occurred, and the error messages can be used to provide more detailed information about the error.
 *
 * <p>
 * The errors are defined as a static inner class within the enum, which allows us to keep all the
 * error-related information in one place. This makes it easier to maintain and update the errors
 * over time, as all the information is in one place and can be updated in one go.
 */
public enum BusinessError {
  UNEXPECTED(1, "Unexpected error. Please contact administrator."),
  RESOURCE_NOT_FOUND(2, "Resource not found"),

  VALIDATION(3, "Validation error"),
  ;

  private static final Map<BusinessError, URI> errorTypes;

  /*
   * Validates the error codes uniqueness on startup.
   */
  static {
    Set<Integer> checkedErrorCode = new HashSet<>();
    errorTypes = new HashMap<>();
    for (BusinessError error : BusinessError.values()) {
      // Validate uniqueness of error numbers
      if (checkedErrorCode.contains(error.errorCode))
        throw new IllegalStateException(
            "Duplicated Error Code Id" + error,
            new BusinessException("error code should not be repeated", BusinessError.UNEXPECTED));
      checkedErrorCode.add(error.errorCode);

      // Keep formatted error types
      errorTypes.put(error, URI.create(String.format("/probs/e%05d", error.errorCode)));
    }
  }

  private final int errorCode;
  private final String errorMessage;

  BusinessError(int errorCode, String errorMessage) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  public URI getCode() {
    return errorTypes.get(this);
  }

  public String getMessage(String... args) {
    return String.format(errorMessage, (Object[]) args);
  }

  @Override
  public String toString() {
    return "errorCode=" + errorCode + ", errorMessage='" + getMessage() + '\'';
  }
}
