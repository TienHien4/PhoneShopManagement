package com.example.quanlybandienthoai.dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @JsonProperty("user_id")
    private Long userId;
    private List<OrderItemRequest> items;
}