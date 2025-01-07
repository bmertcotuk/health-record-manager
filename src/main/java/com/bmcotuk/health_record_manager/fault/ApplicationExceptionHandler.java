package com.bmcotuk.health_record_manager.fault;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorRestResponse> handleAppException(ApplicationException e) {
        final String errorCode = e.getErrorCode();
        final String errorDescription = ApplicationStatusCodes.getDescription(errorCode);
        log.info("handleApplicationException::errorCode={}::errorDescription={}", errorCode, errorDescription);
        final ErrorRestResponse errorResponse = new ErrorRestResponse(errorCode, errorDescription);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorRestResponse> handleAccessDeniedException(AccessDeniedException e) {
        final String errorCode = ApplicationStatusCodes.ERR_ACCESS_DENIED;
        final String errorDescription = ApplicationStatusCodes.getDescription(errorCode);
        log.info("handleAccessDeniedException: {}, {}", errorCode, errorDescription);
        final ErrorRestResponse errorResponse = new ErrorRestResponse(errorCode, errorDescription);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorRestResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final BindingResult errors = e.getBindingResult();
        final ValidationErrorRestResponse errorResponse = convertErrors(errors, DefaultMessageSourceResolvable::getDefaultMessage);
        log.info("handleMethodArgumentNotValidException: {}", errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRestResponse> handleGlobalException(Exception e) {
        final String errorCode = ApplicationStatusCodes.ERR_INTERNAL_SERVER_ERROR;
        final String errorDescription = ApplicationStatusCodes.getDescription(errorCode);
        log.error("handleGlobalException::errorCode={}::errorDescription={}", errorCode, errorDescription, e);
        final ErrorRestResponse errorResponse = new ErrorRestResponse(errorCode, errorDescription);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ValidationErrorRestResponse convertErrors(
            Errors errors,
            Function<DefaultMessageSourceResolvable, String> errorCodeExtractor) {
        final List<ErrorRestResponse> globalErrors = errors.getGlobalErrors().stream()
                .map(error -> new ErrorRestResponse(errorCodeExtractor.apply(error)))
                .collect(Collectors.toList());
        final Map<String, ErrorRestResponse> mappedFieldErrors = errors.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,
                        fieldError -> new ErrorRestResponse(errorCodeExtractor.apply(fieldError)), (lhs, rhs) -> rhs));
        return new ValidationErrorRestResponse(globalErrors, mappedFieldErrors);
    }
}
