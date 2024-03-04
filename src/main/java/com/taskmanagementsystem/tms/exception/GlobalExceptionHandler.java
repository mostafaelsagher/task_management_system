package com.taskmanagementsystem.tms.exception;

import static org.apache.commons.lang3.StringUtils.ordinalIndexOf;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public final class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  private final HttpHeaders headers;

  GlobalExceptionHandler() {
    super();
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      @NonNull HttpMessageNotReadableException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {
    log(ex);
    String baseType = getBaseType((ServletWebRequest) request);
    ApiError body =
        getApiError(
            baseType,
            BusinessError.UNEXPECTED.getCode(),
            ((HttpStatus) status).getReasonPhrase(),
            ex.getLocalizedMessage(),
            (HttpStatus) status);
    return handleExceptionInternal(ex, body, this.headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotWritable(
      @NonNull HttpMessageNotWritableException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {

    log(ex);
    String baseType = getBaseType((ServletWebRequest) request);
    ApiError body =
        getApiError(
            baseType,
            BusinessError.UNEXPECTED.getCode(),
            ((HttpStatus) status).getReasonPhrase(),
            ex.getLocalizedMessage(),
            (HttpStatus) status);
    return handleExceptionInternal(ex, body, this.headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      @NonNull MissingServletRequestParameterException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {

    log(ex);
    String baseType = getBaseType((ServletWebRequest) request);
    ApiError body =
        getApiError(
            baseType,
            BusinessError.VALIDATION.getCode(),
            ((HttpStatus) status).getReasonPhrase(),
            ex.getLocalizedMessage(),
            (HttpStatus) status);
    return handleExceptionInternal(ex, body, this.headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      @NonNull MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {
    log(ex);
    String baseType = getBaseType((ServletWebRequest) request);
    ApiError body =
        getApiError(
            baseType,
            ex,
            BusinessError.VALIDATION,
            toErrors(ex.getBindingResult().getFieldErrors()));
    return handleExceptionInternal(ex, body, this.headers, body.getStatus(), request);
  }

  /**
   * Handles {@link ConstraintViolationException} by returning an {@link ApiError} with the
   * specified status code.
   *
   * @param ex the exception
   * @param handlerMethod the handler method
   * @param webRequest the current request
   * @param servletRequest the servlet request
   * @param servletResponse the servlet response
   * @param httpMethod the HTTP method
   * @return the error response
   */
  @ExceptionHandler(value = ConstraintViolationException.class)
  ResponseEntity<Object> handle(
      ConstraintViolationException ex,
      HandlerMethod handlerMethod,
      WebRequest webRequest,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      HttpMethod httpMethod) {

    log(ex);
    String baseType = getBaseType((ServletWebRequest) webRequest);
    ApiError body =
        getApiError(baseType, ex, BusinessError.VALIDATION, toErrors(ex.getConstraintViolations()));
    return handleExceptionInternal(ex, body, this.headers, body.getStatus(), webRequest);
  }

  /**
   * Handles {@link TransactionSystemException} by returning an {@link ApiError} with the specified
   * status code.
   *
   * @param ex the exception
   * @param handlerMethod the handler method
   * @param webRequest the current request
   * @param servletRequest the servlet request
   * @param servletResponse the servlet response
   * @param httpMethod the HTTP method
   * @return the error response
   */
  @ExceptionHandler(value = TransactionSystemException.class)
  ResponseEntity<Object> handle(
      TransactionSystemException ex,
      HandlerMethod handlerMethod,
      WebRequest webRequest,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      HttpMethod httpMethod) {

    log(ex);

    if (ex.getRootCause() != null)
      if (ex.getRootCause() instanceof ConstraintViolationException constraintViolationException)
        return handle(
            constraintViolationException,
            handlerMethod,
            webRequest,
            servletRequest,
            servletResponse,
            httpMethod);

    return handleAnyException(
        ex, handlerMethod, webRequest, servletRequest, servletResponse, httpMethod);
  }

  /**
   * Handles {@link BusinessException} by returning an {@link ApiError} with the specified status
   * code.
   *
   * @param businessException the exception
   * @param handlerMethod the handler method
   * @param webRequest the current request
   * @param servletRequest the servlet request
   * @param servletResponse the servlet response
   * @param httpMethod the HTTP method
   * @return the error response
   */
  @ExceptionHandler(value = BusinessException.class)
  ResponseEntity<Object> handleApplicationException(
      BusinessException businessException,
      HandlerMethod handlerMethod,
      WebRequest webRequest,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      HttpMethod httpMethod) {

    log(businessException);
    String baseType = getBaseType((ServletWebRequest) webRequest);
    ApiError apiError =
        getApiError(baseType, businessException, businessException.getBusinessError());
    return handleExceptionInternal(
        businessException, apiError, this.headers, apiError.getStatus(), webRequest);
  }

  @ExceptionHandler(value = AccessDeniedException.class)
  public ResponseEntity<Object> handleAuthorizationException(
          AccessDeniedException ex,
          HandlerMethod handlerMethod,
          WebRequest webRequest,
          HttpServletRequest servletRequest,
          HttpServletResponse servletResponse,
          HttpMethod httpMethod) {

    log(ex);
    throw ex; // Unauthorized access handled by security config
  }

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<Object> handleAnyException(
      Exception ex,
      HandlerMethod handlerMethod,
      WebRequest webRequest,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      HttpMethod httpMethod) {

    log(ex);
    String baseType = getBaseType((ServletWebRequest) webRequest);
    ApiError body = getApiError(baseType, ex, BusinessError.UNEXPECTED);
    return handleExceptionInternal(ex, body, this.headers, body.getStatus(), webRequest);
  }

  private void log(Exception ex) {
    String className = ex.getClass().getSimpleName();
    if (log.isDebugEnabled()) {
      log.warn("{} occurs with message: {}", className, ex.getMessage(), ex);
    } else {
      log.warn("{} occurs with message: {}", className, ex.getMessage());
    }
  }

  private ApiError getApiError(String baseType, Exception ex, BusinessError businessError) {
    return getApiError(baseType, ex, businessError, null);
  }

  private ApiError getApiError(
      String baseType,
      Exception ex,
      BusinessError businessError,
      @Nullable List<ApiSubError> apiSubErrors) {
    ApiError.ApiErrorBuilder error =
        ApiError.builder()
            .type(URI.create(baseType + businessError.getCode()))
            .title(businessError.getMessage())
            .detail(ex.getLocalizedMessage())
            .instance(businessError.getCode())
            .subErrors(apiSubErrors);

    switch (businessError) {
      case RESOURCE_NOT_FOUND -> error.status(HttpStatus.NOT_FOUND);
      case VALIDATION -> error.status(HttpStatus.BAD_REQUEST);
      default -> error.status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return error.build();
  }

  private ApiError getApiError(
      String baseType, URI code, String reason, String message, HttpStatus status) {
    return ApiError.builder()
        .type(URI.create(baseType + code))
        .title(reason)
        .detail(message)
        .instance(code)
        .status(status)
        .build();
  }

  private String getBaseType(ServletWebRequest request) {
    String fullUrl = request.getRequest().getRequestURL().toString();
    return fullUrl.substring(0, ordinalIndexOf(fullUrl, "/", 3));
  }

  private List<ApiSubError> toErrors(List<FieldError> fieldErrors) {
    return fieldErrors.stream().map(this::toError).toList();
  }

  private List<ApiSubError> toErrors(Set<ConstraintViolation<?>> constraintViolations) {
    return constraintViolations.stream().map(this::toError).toList();
  }

  private ApiSubError toError(ConstraintViolation<?> cv) {
    return new ApiValidationError(
        cv.getRootBeanClass().getSimpleName(),
        ((PathImpl) cv.getPropertyPath()).getLeafNode().asString(),
        cv.getInvalidValue(),
        cv.getMessage());
  }

  private ApiSubError toError(FieldError fieldError) {
    return new ApiValidationError(
        fieldError.getObjectName(),
        fieldError.getField(),
        Objects.toString(fieldError.getRejectedValue(), null),
        fieldError.getDefaultMessage());
  }
}
