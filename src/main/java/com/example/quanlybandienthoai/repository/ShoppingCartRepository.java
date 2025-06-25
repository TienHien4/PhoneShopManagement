package com.example.quanlybandienthoai.repository;

import com.example.quanlybandienthoai.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
      /**
       * Xóa giỏ hàng theo mã người dùng đã dăng nhập.
       * @param userId, mã người dùng
       */
      void deleteByUser_UserId(Long userId);


}
