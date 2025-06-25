package com.example.quanlybandienthoai.dto.Response;

import com.example.quanlybandienthoai.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private long order_id;
    private int total_amount;
    private double total_price;
    private Product product_id;
}
