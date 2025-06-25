package com.example.quanlybandienthoai.repository;

import com.example.quanlybandienthoai.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    /**
     * Kiểm tra xem thương hiệu đã tồn tại hay chưa.
     * @param brandName, tên thương hiệu mà người dùng nhập vào
     * @return thông tin true đã tồn tại, ngược lại false
     */
    boolean existsByBrandName(String brandName);
}
