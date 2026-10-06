package com.linkhub.exception;

import com.linkhub.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.linkhub.exception.JobNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // =========================
    // BAD REQUEST
    // =========================

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(
            BadRequestException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .data(null)
                                .build()
                );
    }


    // =========================
    // USER NOT FOUND
    // =========================

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFound(
            UserNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ApiResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .data(null)
                                .build()
                );
    }


    // =========================
    // PROFILE NOT FOUND
    // =========================

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleProfileNotFound(
            ProfileNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ApiResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .data(null)
                                .build()
                );
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleProjectNotFound(
            ProjectNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ApiResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .data(null)
                                .build()
                );
    }


    // =========================
    // VALIDATION ERROR
    // =========================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        String message = "Validation failed";

        if (ex.getBindingResult().getFieldError() != null) {

            message = ex.getBindingResult()
                    .getFieldError()
                    .getDefaultMessage();
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.builder()
                                .success(false)
                                .message(message)
                                .data(null)
                                .build()
                );
    }


    // =========================
    // ALL OTHER ERRORS
    // =========================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(
            Exception ex) {

        log.error("Unhandled API request failure", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ApiResponse.builder()
                                .success(false)
                                .message("Server error. Please try again.")
                                .data(null)
                                .build()
                );
    }

    @ExceptionHandler(RepositoryNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleRepositoryNotFound(RepositoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.builder()
                .success(false).message(ex.getMessage()).data(null).build());
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleAlreadyExists(ResourceAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.builder()
                .success(false).message(ex.getMessage()).data(null).build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.builder()
                .success(false).message(ex.getMessage()).data(null).build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.builder()
                .success(false).message("Username/email or password is incorrect").data(null).build());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleRouteNotFound(
            NoResourceFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.builder()
                        .success(false)
                        .message("API route not found")
                        .data(null)
                        .build());
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleJobNotFound(
            JobNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ApiResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .data(null)
                                .build()
                );
    }
}
