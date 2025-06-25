package com.example.quanlybandienthoai.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartResponse {
        private Long shopping_cart_id;
        private int total_product;
        private double total_price;
        private List<CartItemResponse> orders;
        private UserResponse user;
}

