package com.example.quanlybandienthoai.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponse {
    private long brand_id;

    private String brand_name;

    private String country;
}
