
package com.example.quanlybandienthoai.controller;

import com.example.quanlybandienthoai.dto.ApiMessage;
import com.example.quanlybandienthoai.dto.ApiResponse;
import com.example.quanlybandienthoai.dto.Request.LoginRequest;
import com.example.quanlybandienthoai.dto.Request.LogoutRequest;
import com.example.quanlybandienthoai.dto.Request.UserRequest;
import com.example.quanlybandienthoai.dto.Response.LoginResponse;
import com.example.quanlybandienthoai.dto.Response.UserResponse;
import com.example.quanlybandienthoai.enums.DefinitionCode;
import com.example.quanlybandienthoai.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý các API xác thực như đăng nhập, đăng ký, đăng xuất
 * @author Nguyễn Tiến Hiền
 * @since 23/06/2025
 */
@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * API đăng nhập
     * @param request thông tin đăng nhập (email, password)
     * @return mã người dung
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        var result = userService.login(request);
        var message = new ApiMessage("Đăng nhập thành công", "Login successfully", DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok().body(new ApiResponse<>(message, result));
    }

    /**
     * API đăng ký người dùng mới
     * @param request thông tin người dùng đăng ký
     * @return Thông tin người dùng vừa đăng ký
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody @Valid UserRequest request) {
        var result = userService.createCustomer(request);
        var message = new ApiMessage("Tạo tài khoản thành công", "User registered successfully",
                DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok().body(new ApiResponse<>(message, result));
    }

    /**
     * API đăng xuất
     * @param request mã người dùng
     * @return Thông báo đăng xuất thành công
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest request) {
        var message = new ApiMessage("Đăng xuất thành công", "Logout success", DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok().body(new ApiResponse<>(message, null));
    }
}
