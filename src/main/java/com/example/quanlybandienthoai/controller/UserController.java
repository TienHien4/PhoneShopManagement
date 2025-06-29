
package com.example.quanlybandienthoai.controller;

import com.example.quanlybandienthoai.dto.ApiMessage;
import com.example.quanlybandienthoai.dto.ApiResponse;
import com.example.quanlybandienthoai.dto.Request.UserRequest;
import com.example.quanlybandienthoai.dto.Request.UserUpdateRequest;
import com.example.quanlybandienthoai.dto.Response.UserResponse;
import com.example.quanlybandienthoai.enums.DefinitionCode;
import com.example.quanlybandienthoai.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controller xử lý các yêu cầu liên quan đến người dùng
 * @author Nguyễn Tiến Hiền
 * @since 22/06/2025
 */

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * API lấy danh sách tất cả khách hàng
     * @return ResponseEntity chứa danh sách khách hàng và thông báo
     */
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllCustomer() {
        var result = userService.getCustomers();
        var message = new ApiMessage(
                "Lấy danh sách khách hàng thành công",
                "Get all customers successfully",
                DefinitionCode.SUCCESS.getCode()
        );
        return ResponseEntity.ok().body(new ApiResponse<>(message, result));
    }
    /**
     * API đăng ký người dùng mới
     * @param request thông tin người dùng đăng ký
     * @return Thông tin người dùng vừa đăng ký
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody @Valid UserRequest request) {
        var result = userService.createCustomer(request);
        var message = new ApiMessage("Tạo tài khoản thành công", "User registered successfully",
                DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok().body(new ApiResponse<>(message, result));
    }

    /**
     * API cập nhật thông tin khách hàng theo ID
     * @param id      ID khách hàng
     * @param request Thông tin mới của khách hàng
     * @return ResponseEntity chứa thông tin khách hàng sau khi cập nhật
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateCustomer(@PathVariable int id, @RequestBody @Valid UserUpdateRequest request) {
        var result = userService.updateCustomer(id, request);
        var message = new ApiMessage(
                "Sửa thông tin khách hàng thành công",
                "Customer updated",
                DefinitionCode.SUCCESS.getCode()
        );
        return ResponseEntity.ok().body(new ApiResponse<>(message, result));
    }

    /**
     * API tìm kiếm người dùng theo ID
     * @param id ID người dùng
     * @return ResponseEntity chứa thông tin người dùng
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> findUserById(@PathVariable int id) {
        var result = userService.getCustomerById(id);
        var message = new ApiMessage(
                "Tìm người dùng thành công",
                "Find user successfully",
                DefinitionCode.SUCCESS.getCode()
        );
        return ResponseEntity.ok().body(new ApiResponse<>(message, result));
    }
}
