package dev.earlydreamer.kirini.exception;

import dev.earlydreamer.kirini.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 핸들러
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if ("UNAUTHORIZED".equalsIgnoreCase(e.getErrorCode())) {
            status = HttpStatus.UNAUTHORIZED;
        } else if ("FORBIDDEN".equalsIgnoreCase(e.getErrorCode())) {
            status = HttpStatus.FORBIDDEN;
        }
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(e.getMessage(), e.getErrorCode()))
                ;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthException(AuthenticationException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("인증이 필요합니다.", "UNAUTHORIZED"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("권한이 없습니다.", "FORBIDDEN"));
    }

    /**
     * Bean Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("입력값이 올바르지 않습니다.");

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(message, "VALIDATION_ERROR"))
                ;
    }

    /**
     * 기타 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("서버 오류가 발생했습니다.", "INTERNAL_ERROR"))
                ;
    }
}
