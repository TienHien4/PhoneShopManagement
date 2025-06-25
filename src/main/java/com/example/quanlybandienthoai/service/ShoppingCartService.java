package com.example.quanlybandienthoai.service;

import com.example.quanlybandienthoai.dto.Request.ShoppingCartRequest;
import com.example.quanlybandienthoai.dto.Response.CartItemResponse;

import java.util.List;

public interface ShoppingCartService {
    void addItem(ShoppingCartRequest request, long userId, long productId);
    void deleteItem(long userId, long productId);
    List<CartItemResponse> getAllItem(long userId);
    void reduceItem(long userId, long productId);
}
