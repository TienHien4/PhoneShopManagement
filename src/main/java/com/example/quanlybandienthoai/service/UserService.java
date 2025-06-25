package com.example.quanlybandienthoai.service;

import com.example.quanlybandienthoai.dto.Request.LoginRequest;
import com.example.quanlybandienthoai.dto.Request.LogoutRequest;
import com.example.quanlybandienthoai.dto.Request.UserRequest;
import com.example.quanlybandienthoai.dto.Request.UserUpdateRequest;
import com.example.quanlybandienthoai.dto.Response.LoginResponse;
import com.example.quanlybandienthoai.dto.Response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createCustomer(UserRequest request);
    UserResponse updateCustomer(long id, UserUpdateRequest request);
    UserResponse getCustomerById(long id);
    List<UserResponse> getCustomers();
    LoginResponse login(LoginRequest request);
    void logout(LogoutRequest request);
}
