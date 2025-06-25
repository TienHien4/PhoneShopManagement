package com.example.quanlybandienthoai.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @NotBlank(message = "Họ tên không được để trống")
    private String full_name;
    @Size(min = 9, max = 11, message = "SDT cần nhâp đúng")
    private String phone;
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
}
