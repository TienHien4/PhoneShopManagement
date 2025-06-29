package com.example.quanlybandienthoai.service;

import com.example.quanlybandienthoai.dto.Request.LoginRequest;
import com.example.quanlybandienthoai.dto.Request.LogoutRequest;
import com.example.quanlybandienthoai.dto.Request.RefreshTokenRequest;
import com.example.quanlybandienthoai.dto.Response.LoginResponse;
import com.example.quanlybandienthoai.dto.Response.RefreshTokenResponse;

import java.text.ParseException;

public interface AuthService {
    LoginResponse login(LoginRequest request) throws ParseException;
    void logout(LogoutRequest request);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
}
