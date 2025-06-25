package com.example.quanlybandienthoai.dto.Response;

import com.example.quanlybandienthoai.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private long product_id;
    private String product_name;
    private String specification;
    private double price;
    private String image;
    private LocalDate release_date;
    private Brand brand;

}
