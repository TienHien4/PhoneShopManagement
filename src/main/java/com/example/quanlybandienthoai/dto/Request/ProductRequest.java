package com.example.quanlybandienthoai.dto.Request;

import com.example.quanlybandienthoai.entity.Brand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String product_name;
    @NotBlank(message = "Thông số sản phẩm không được để trống")
    private String specification;
    @Min(value = 0, message = "Giá phải lớn hơn 0")
    private double price;
    private String image;
    private String release_date;
    private long brand_id;
}
