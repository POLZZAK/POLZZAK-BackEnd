package com.polzzak.presentation.api;

import com.polzzak.application.PolzzakException;
import com.polzzak.application.dto.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
        log.error("[MethodArgumentNotValidException] ", ex);
        List<String> errorMessages = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(it -> it.getField() + " " + it.getDefaultMessage())
            .collect(Collectors.toList());

        return ResponseEntity
            .status(ErrorCode.REQUEST_RESOURCE_NOT_VALID.getHttpStatus())
            .body(ApiResponse.error(ErrorCode.REQUEST_RESOURCE_NOT_VALID.getCode(), errorMessages));
    }

    @ExceptionHandler(PolzzakException.class)
    public ResponseEntity<ApiResponse> polzzakException(final PolzzakException e) {
        log.error("[PolzzakException] ", e);

        return ResponseEntity
            .status(e.getErrorCode().getHttpStatus())
            .body(ApiResponse.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> illegalArgumentException(final IllegalArgumentException e) {
        log.error("[IllegalArgumentException] ", e);

        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(InvalidContentTypeException.class)
    public ResponseEntity<ApiResponse> invalidContentTypeException(final InvalidContentTypeException e) {
        log.error("[InvalidContentTypeException] ", e);

        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> runtimeException(final RuntimeException e) {
        log.error("[RuntimeException] ", e);

        return ResponseEntity
            .internalServerError()
            .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
    }
}
