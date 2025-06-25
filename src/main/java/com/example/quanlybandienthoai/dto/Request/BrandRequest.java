package com.example.quanlybandienthoai.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandRequest {
    private String brand_name;

    private String country;
}
