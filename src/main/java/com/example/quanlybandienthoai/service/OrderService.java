package com.example.quanlybandienthoai.service;

import com.example.quanlybandienthoai.dto.Request.OrderRequest;
import com.example.quanlybandienthoai.dto.Response.OrderResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest request);

    List<OrderResponse> getAllOrders();

    void deleteOrder(Long orderId);

    // Tìm kiếm đơn hàng theo id
    OrderResponse searchOrderById(Long orderId);

    // Phân trang danh sách đơn hàng
    Page<OrderResponse> pagination(int pageNo, int pageSize);
}
