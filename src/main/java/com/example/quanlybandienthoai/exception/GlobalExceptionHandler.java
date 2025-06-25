
package com.example.quanlybandienthoai.exception;

import com.example.quanlybandienthoai.dto.ApiMessage;
import com.example.quanlybandienthoai.dto.ApiResponse;
import com.example.quanlybandienthoai.enums.DefinitionCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * GlobalExceptionHandler xử lý các exception phát sinh trong toàn bộ ứng dụng
 * @author Nguyễn Tiến Hiền
 * @since 22/06/2025
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý exception do AppException ném ra
     *
     * @param exception AppException được throw từ service hoặc controller
     * @return ResponseEntity chứa thông báo lỗi và mã lỗi
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handlerAppException(AppException exception) {
        ApiMessage message = new ApiMessage(
                exception.getUserMessage(),
                exception.getMessage(),
                exception.getDefinitionCode().getCode()
        );
        return ResponseEntity.badRequest().body(new ApiResponse<>(message, null));
    }


    /**
     * Xử lý lỗi validate @Valid khi truyền dữ liệu không hợp lệ từ phía client
     *
     * @param exception MethodArgumentNotValidException được ném ra khi validate thất bại
     * @return ResponseEntity chứa thông báo lỗi validation
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Object>> handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String userMsg = exception.getFieldError().getDefaultMessage();
        ApiMessage message = new ApiMessage(
                userMsg,
                exception.getFieldError().getDefaultMessage(),
                DefinitionCode.VALIDATION_ERROR.getCode()
        );
        return ResponseEntity.badRequest().body(new ApiResponse<>(message, null));
    }
}
