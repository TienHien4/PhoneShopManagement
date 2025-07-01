package com.example.quanlybandienthoai.service;

import com.example.quanlybandienthoai.dto.Request.LoginRequest;
import com.example.quanlybandienthoai.dto.Request.LogoutRequest;
import com.example.quanlybandienthoai.dto.Request.UserRequest;
import com.example.quanlybandienthoai.dto.Request.UserUpdateRequest;
import com.example.quanlybandienthoai.dto.Response.LoginResponse;
import com.example.quanlybandienthoai.dto.Response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse updateUser(long id, UserUpdateRequest request);
    UserResponse getUserById(long id);
    List<UserResponse> getUsers();
    List<UserResponse> getUserByKeyword(String keyword);
    Page<UserResponse> pagination(int pageNo, int pageSize);

}
