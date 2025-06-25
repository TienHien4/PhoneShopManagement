
package com.example.quanlybandienthoai.controller;

import com.example.quanlybandienthoai.dto.ApiMessage;
import com.example.quanlybandienthoai.dto.ApiResponse;
import com.example.quanlybandienthoai.dto.Request.ShoppingCartRequest;
import com.example.quanlybandienthoai.dto.Response.CartItemResponse;
import com.example.quanlybandienthoai.enums.DefinitionCode;
import com.example.quanlybandienthoai.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controller xử lý các thao tác liên quan đến giỏ hàng
 * @author Nguyễn Tiến Hiền
 * @since 22/06/2025
 */
@RestController
@RequestMapping("/carts")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * API thêm sản phẩm vào giỏ hàng
     * @param userId   ID người dùng
     * @param productId ID sản phẩm
     * @param request  Thông tin số lượng cần thêm
     * @return ResponseEntity chứa thông báo thành công
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Void>> addItemToCart(
            @RequestParam long userId,
            @RequestParam long productId,
            @RequestBody ShoppingCartRequest request
    ) {
        shoppingCartService.addItem(request, userId, productId);
        ApiMessage message = new ApiMessage(
                "Thêm sản phẩm vào giỏ hàng thành công",
                "Add item to cart successfully",
                DefinitionCode.SUCCESS.getCode()
        );
        return ResponseEntity.ok().body(new ApiResponse<>(message, null));
    }

    /**
     * API giảm số lượng sản phẩm trong giỏ hàng
     * @param userId   ID người dùng
     * @param productId ID sản phẩm
     * @return ResponseEntity chứa thông báo thành công
     */
    @PostMapping("/reduce")
    public ResponseEntity<ApiResponse<Void>> reduceItem(
            @RequestParam long userId,
            @RequestParam long productId
    ) {
        shoppingCartService.reduceItem(userId, productId);
        ApiMessage message = new ApiMessage(
                "Giảm số lượng sản phẩm thành công",
                "Reduce item in cart successfully",
                DefinitionCode.SUCCESS.getCode()
        );
        return ResponseEntity.ok().body(new ApiResponse<>(message, null));
    }

    /**
     * API xóa sản phẩm khỏi giỏ hàng
     * @param userId   ID người dùng
     * @param productId ID sản phẩm
     * @return ResponseEntity chứa thông báo thành công
     */
    @DeleteMapping("")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @RequestParam long userId,
            @RequestParam long productId
    ) {
        shoppingCartService.deleteItem(userId, productId);
        ApiMessage message = new ApiMessage(
                "Xóa sản phẩm khỏi giỏ hàng thành công",
                "Delete item from cart successfully",
                DefinitionCode.SUCCESS.getCode()
        );
        return ResponseEntity.ok().body(new ApiResponse<>(message, null));
    }

    /**
     * API lấy tất cả sản phẩm trong giỏ hàng của người dùng
     * @param userId ID người dùng
     * @return ResponseEntity chứa danh sách sản phẩm
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getAllItem(@PathVariable int userId) {
        var result = shoppingCartService.getAllItem(userId);
        var message = new ApiMessage(
                "Lấy danh sách sản phẩm của người dùng thành công",
                "Get all items of user with ID: " + userId,
                DefinitionCode.SUCCESS.getCode()
        );
        return ResponseEntity.ok().body(new ApiResponse<>(message, result));
    }
}
