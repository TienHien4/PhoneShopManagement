package com.example.quanlybandienthoai.enums;

public enum DefinitionCode {
    SUCCESS(1000),
    VALIDATION_ERROR(1001),
    EXISTS(1002),
    NOT_FOUND(1003);

    private final int code;

    DefinitionCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

