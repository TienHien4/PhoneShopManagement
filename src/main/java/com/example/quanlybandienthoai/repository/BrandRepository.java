package com.example.quanlybandienthoai.repository;

import com.example.quanlybandienthoai.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    /**
     * Kiểm tra xem thương hiệu đã tồn tại hay chưa.
     * @param brandName, tên thương hiệu mà người dùng nhập vào
     * @return thông tin true đã tồn tại, ngược lại false
     */
    boolean existsByBrandName(String brandName);
    /**
     * Tìm kiếm thương hiệu theo từ khóa.
     *
     * @param keyword từ khóa mà người dùng nhập
     * @return danh sách thương hiệu
     */
    @Query("select b from Brand b where b.brandName like concat('%', ?1, '%') " +
            "or b.country like concat('%', ?1, '%') ")
    List<Brand> findByByKeyword(String keyword);
}
