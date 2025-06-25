package com.example.quanlybandienthoai.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    private int total_amount;
    private double total_price;
    private long product_id;
}
