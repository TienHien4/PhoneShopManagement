package com.example.quanlybandienthoai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * ApiMessage định nghĩa thông tin trả về trong API
 *
 * @author Nguyễn Tiến Hiền
 * @since 22/06/2025
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    // message chung trả về
    private ApiMessage message;
    // kết quả của quá trình truy truy vấn, thêm sửa... của các đối tượng
    private T result;
}
