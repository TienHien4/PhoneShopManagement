package com.example.quanlybandienthoai.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "Họ tên không được để trống")
    private String full_name;
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email cần đúng định dạng")
    private String email;
    private String password;
    @Size(min = 9, max = 11, message = "SDT cần nhâp đúng")
    private String phone;
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
}
