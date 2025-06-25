package com.example.quanlybandienthoai.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long order_id;
    private Long user_id;
    private int total_quantity;
    private double total_price;
    private LocalDateTime order_date;
    private List<OrderItemResponse> items;
}
