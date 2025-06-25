package com.example.quanlybandienthoai.service;

import com.example.quanlybandienthoai.dto.Response.CartItemResponse;

import java.util.List;

public interface CartItemService {
    List<CartItemResponse> getAllOrders();
}
