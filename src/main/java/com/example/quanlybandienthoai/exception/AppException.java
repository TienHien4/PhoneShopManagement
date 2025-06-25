package com.example.quanlybandienthoai.exception;

import com.example.quanlybandienthoai.enums.DefinitionCode;

/**
 * AppException định nghĩa thông tin trả về mỗi khi xảy ra lỗi
 *
 * @author Nguyễn Tiến Hiền
 * @since 22/06/2025
 */
public class AppException extends RuntimeException {
    private final DefinitionCode definitionCode;
    private final String userMessage;

    public AppException(DefinitionCode definitionCode, String userMessage, String internalMessage) {
        super(internalMessage);
        this.definitionCode = definitionCode;
        this.userMessage = userMessage;
    }

    public DefinitionCode getDefinitionCode() {
        return definitionCode;
    }

    public String getUserMessage() {
        return userMessage;
    }
}

