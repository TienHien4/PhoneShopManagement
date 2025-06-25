package com.example.quanlybandienthoai.repository;

import com.example.quanlybandienthoai.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    /**
     * Xóa tất cả các item trong giỏ hàng theo mã giỏ hàng khi thực hiện đặt đơn hàng.
     * @param cartId, mã giỏ hàng mà người dùng nhập vào
     */
    void deleteAllByShoppingCart_ShoppingCartId(Long cartId);

}
