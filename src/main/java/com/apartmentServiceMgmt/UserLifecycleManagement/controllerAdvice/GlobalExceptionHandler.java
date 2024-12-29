package com.apartmentServiceMgmt.UserLifecycleManagement.controllerAdvice;

import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.apartmentServiceMgmt.model.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = generateErrorResponse("ERR-404", ex.getMessage(), request.getDescription(false));
        log.error("Error occurred:{}",ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ErrorResponse generateErrorResponse(String code, String message, String description) {
    	ErrorResponse errorResponse = new ErrorResponse();
    	errorResponse.setCode(code);
    	errorResponse.setMessage(message);
    	errorResponse.setDetails(description);
    	return errorResponse;
	}

	@ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex, WebRequest request) {
        ErrorResponse errorResponse = generateErrorResponse("ERR-400", ex.getMessage(), request.getDescription(false));
        log.error("Error occurred:{}",ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler({UnauthorizedAccessException.class ,AuthorizationDeniedException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccessException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = generateErrorResponse("ERR-401", ex.getMessage(), request.getDescription(false));
        log.error("Error occurred:{}",ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

	@ExceptionHandler(UnauthenticatedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthenticatedAccessException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = generateErrorResponse("ERR-403", ex.getMessage(), request.getDescription(false));
        log.error("Error occurred:{}",ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
	
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponse errorResponse = generateErrorResponse("ERR-400", "Validation failed", errors);
        log.error("Error occurred:{}",ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = generateErrorResponse("ERR-500", "An unexpected error occurred", ex.getMessage());
        log.error("Error occurred:{}",ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
