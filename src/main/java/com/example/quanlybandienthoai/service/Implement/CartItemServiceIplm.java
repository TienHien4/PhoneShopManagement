package com.example.quanlybandienthoai.service.Implement;

import com.example.quanlybandienthoai.dto.Response.CartItemResponse;
import com.example.quanlybandienthoai.entity.CartItem;
import com.example.quanlybandienthoai.repository.CartItemRepository;
import com.example.quanlybandienthoai.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Nguyễn Tiến Hiền
 * @since 25/06/2025
 */
@Service
public class CartItemServiceIplm implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    /**
     * Lấy danh sách tất cả các mục trong giỏ hàng
     * @return danh sách các CartItemResponse để hiển thị thông tin đã định dạng
     */
    @Override
    public List<CartItemResponse> getAllOrders() {
        List<CartItem> listOrders = cartItemRepository.findAll();

        var response = listOrders.stream().map(cartItem ->
                new CartItemResponse(
                        cartItem.getOrder_id(),
                        cartItem.getTotal_amount(),
                        cartItem.getTotal_price(),
                        cartItem.getProduct()
                )
        ).toList();

        return response;
    }
}
