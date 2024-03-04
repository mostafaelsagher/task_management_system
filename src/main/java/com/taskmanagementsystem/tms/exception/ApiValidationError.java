package com.taskmanagementsystem.tms.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiValidationError(String object, String field, Object rejectedValue, String message)
    implements ApiSubError {}
