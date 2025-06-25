package com.example.quanlybandienthoai.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private long user_id;
    private String full_name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private LocalDate registrated_date;
}
