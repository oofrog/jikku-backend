package com.jikku.backend.global.exception;


import com.jikku.backend.global.apiPayload.ApiResponse;
import com.jikku.backend.global.apiPayload.code.BaseErrorCode;
import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@NullMarked
@RestControllerAdvice
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException e) {
        log.warn("BaseException: {}", e.getMessage());
        BaseErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException e) {
        log.warn("ConstraintViolationException: {}", e.getMessage());
        return ResponseEntity.status(GeneralErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponse.onFailure(GeneralErrorCode.INVALID_INPUT_VALUE, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("MethodArgumentTypeMismatchException: {}", ex.getMessage());
        return ResponseEntity.status(GeneralErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponse.onFailure(GeneralErrorCode.INVALID_INPUT_VALUE));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
      log.warn("IllegalArgumentException: {}", e.getMessage());
      return ResponseEntity.status(GeneralErrorCode.INVALID_INPUT_VALUE.getStatus())
        .body(ApiResponse.onFailure(GeneralErrorCode.INVALID_INPUT_VALUE, e.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            MissingServletRequestPartException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("MissingServletRequestPartException: {}", ex.getMessage());
        return ResponseEntity.status(GeneralErrorCode.MULTIPART_FILE_ERROR.getStatus())
                .body(ApiResponse.onFailure(GeneralErrorCode.MULTIPART_FILE_ERROR));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("MethodArgumentNotValidException: {}", ex.getMessage());
        return ResponseEntity.status(GeneralErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponse.onFailure(GeneralErrorCode.INVALID_INPUT_VALUE, createValidationMessage(ex.getBindingResult())));
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("HandlerMethodValidationException: {}", ex.getMessage());
        return ResponseEntity.status(GeneralErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponse.onFailure(GeneralErrorCode.INVALID_INPUT_VALUE));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("HttpMessageNotReadableException: {}", ex.getMessage());
        return ResponseEntity.status(GeneralErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponse.onFailure(GeneralErrorCode.INVALID_INPUT_VALUE));
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("HttpRequestMethodNotSupportedException: {}", ex.getMessage());
        return ResponseEntity.status(status)
                .body(ApiResponse.onFailure(GeneralErrorCode.METHOD_NOT_ALLOWED));
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("NoHandlerFoundException: {}", ex.getMessage());
        return ResponseEntity.status(status)
                .body(ApiResponse.onFailure(GeneralErrorCode.NOT_FOUND));
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("NoResourceFoundException: {}", ex.getMessage());
        return ResponseEntity.status(status)
                .body(ApiResponse.onFailure(GeneralErrorCode.NOT_FOUND));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.warn("DataIntegrityViolationException: {}", e.getMessage());
        return ResponseEntity.status(GeneralErrorCode.DUPLICATE_RESOURCE.getStatus())
                .body(ApiResponse.onFailure(GeneralErrorCode.DUPLICATE_RESOURCE));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled Exception", e);
        return ResponseEntity.status(GeneralErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.onFailure(GeneralErrorCode.INTERNAL_SERVER_ERROR));
    }

    private String createValidationMessage(BindingResult bindingResult) {
        String message = bindingResult.getAllErrors().stream()
                .map(this::formatValidationError)
                .filter(errorMessage -> errorMessage != null && !errorMessage.isBlank())
                .distinct()
                .collect(Collectors.joining(", "));

        return message.isBlank() ? GeneralErrorCode.INVALID_INPUT_VALUE.getMessage() : message;
    }

    private String formatValidationError(ObjectError error) {
        if (error instanceof FieldError fieldError) {
            return fieldError.getField() + ": " + fieldError.getDefaultMessage();
        }

        return error.getDefaultMessage();
    }
}
