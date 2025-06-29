package com.example.quanlybandienthoai.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private long user_id;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String address;
    private LocalDate registrated_date;
    private Set<String> roles;
}
