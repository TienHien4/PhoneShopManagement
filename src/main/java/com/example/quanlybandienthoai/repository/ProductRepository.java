package com.example.quanlybandienthoai.repository;

import com.example.quanlybandienthoai.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Tìm kiếm sản phẩm theo theo từ khóa (tên, mô tả).
     *
     * @param keyword, từ khóa mà người dùng nhập vào
     * @return thông tin danh sách sản phẩm nếu tìm thấy
     */
    @Query("select p from Product p where p.product_name like concat('%', ?1, '%')"
           + "or p.specification like concat('%', ?1, '%')"
    )
    List<Product> getProductsByKeyword(String keyword);

}
