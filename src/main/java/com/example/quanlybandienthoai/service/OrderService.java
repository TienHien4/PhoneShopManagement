package com.example.quanlybandienthoai.service;

import com.example.quanlybandienthoai.dto.Request.OrderRequest;
import com.example.quanlybandienthoai.dto.Response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest request);
    List<OrderResponse> getAllOrders();
    void deleteOrder(Long orderId);
}
