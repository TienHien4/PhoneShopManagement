package com.example.quanlybandienthoai.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
    private Long product_id;
    private String product_name;
    private int quantity;
    private double price;
}
