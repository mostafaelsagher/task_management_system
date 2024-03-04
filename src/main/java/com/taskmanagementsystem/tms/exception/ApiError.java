package com.taskmanagementsystem.tms.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

/**
 * Value object representing an API error.
 */
@Value
@Builder
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

  @NonNull URI type;
  @NonNull String title;
  @NonNull HttpStatus status;
  @NonNull String detail;
  @NonNull URI instance;

  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
      timezone = "UTC")
  Instant timestamp = Instant.now();

  List<ApiSubError> subErrors;

}
