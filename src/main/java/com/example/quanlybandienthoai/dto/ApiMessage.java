package com.example.quanlybandienthoai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * ApiMessage định nghĩa thông tin trả về cho message trong API
 *
 * @author Nguyễn Tiến Hiền
 * @since 22/06/2025
 */
public class ApiMessage {
    // user message (message trả về cho user)
    private String userMessage;
    // internal message (message trả vể cho backend developer)
    private String internalMessage;
    // code (message trả về cho client developer)
    private int code;
}
