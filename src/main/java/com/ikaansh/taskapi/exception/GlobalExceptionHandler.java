package com.ikaansh.taskapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// 1. Handle @Valid DTO Validation Errors (400 Bad Request)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
			HttpServletRequest request) {

		Map<String, String> errors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}

		ApiErrorResponse response = ApiErrorResponse.builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.BAD_REQUEST.value()).error("Validation Failed").message("Invalid request parameters")
				.path(request.getRequestURI()).validationErrors(errors).build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	// 2. Handle Resource Not Found (404 Not Found)
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
			HttpServletRequest request) {

		return buildErrorResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request.getRequestURI());
	}

	// 3. Handle Task Ownership / Access Denied (403 Forbidden)
	@ExceptionHandler({ UnauthorizedAccessException.class, AccessDeniedException.class })
	public ResponseEntity<ApiErrorResponse> handleAccessDenied(Exception ex, HttpServletRequest request) {

		return buildErrorResponse(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), request.getRequestURI());
	}

	// 4. Handle Invalid Login Credentials (401 Unauthorized)
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex,
			HttpServletRequest request) {

		return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid email or password",
				request.getRequestURI());
	}

	// 5. Handle Duplicate Email or Bad Arguments (400 Bad Request)
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
			HttpServletRequest request) {

		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI());
	}

	// 6. Fallback for Unexpected Errors (500 Internal Server Error)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {

		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(),
				request.getRequestURI());
	}

	private ResponseEntity<ApiErrorResponse> buildErrorResponse(HttpStatus status, String error, String message,
			String path) {

		ApiErrorResponse response = ApiErrorResponse.builder().timestamp(LocalDateTime.now()).status(status.value())
				.error(error).message(message).path(path).build();

		return ResponseEntity.status(status).body(response);
	}
}