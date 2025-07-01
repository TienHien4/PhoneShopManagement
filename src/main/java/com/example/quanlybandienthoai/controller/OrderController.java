package com.example.quanlybandienthoai.controller;

import com.example.quanlybandienthoai.dto.ApiMessage;
import com.example.quanlybandienthoai.dto.ApiResponse;
import com.example.quanlybandienthoai.dto.Request.OrderRequest;
import com.example.quanlybandienthoai.dto.Response.OrderResponse;
import com.example.quanlybandienthoai.enums.DefinitionCode;
import com.example.quanlybandienthoai.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller xử lý các yêu cầu liên quan đến đơn hàng.
 * 
 * @author Nguyễn Tiến Hiền
 * @since 25/06/2025
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * API đặt một đơn hàng mới.
     * 
     * @param request OrderRequest chứa thông tin đơn hàng cần đặt.
     * @return ResponseEntity chứa thông tin đơn hàng sau khi đặt.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(@RequestBody OrderRequest request) {
        var result = orderService.placeOrder(request);
        var message = new ApiMessage("Đặt hàng thành công", "Order placed successfully",
                DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok(new ApiResponse<>(message, result));
    }

    /**
     * API lấy danh sách tất cả đơn hàng.
     * 
     * @return ResponseEntity chứa danh sách các OrderResponse và thông báo thành
     *         công.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        var result = orderService.getAllOrders();
        var message = new ApiMessage("Lấy tất cả đơn hàng thành công", "Fetched all orders",
                DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok(new ApiResponse<>(message, result));
    }

    /**
     * API xóa đơn hàng theo ID.
     * 
     * @param orderId ID của đơn hàng cần xóa.
     * @return ResponseEntity chứa thông báo xác nhận xóa thành công.
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        var message = new ApiMessage("Xóa đơn hàng thành công", "Order deleted", DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok(new ApiResponse<>(message, null));
    }
    /**
     * API tìm kiếm đơn hàng theo id.
     * 
     * @param orderId ID của đơn hàng cần tìm kiếm.
     * @return ResponseEntity chứa thông tin đơn hàng khớp với id và thông báo
     *         thành công.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> searchOrderById(@PathVariable long orderId) {
        var result = orderService.searchOrderById(orderId);
        var message = new ApiMessage("Tìm kiếm đơn hàng thành công", "Search success",
                DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok(new ApiResponse<>(message, result));
    }
    /**
     * API phân trang danh sách đơn hàng.
     * 
     * @param pageNo   Số trang cần lấy dữ liệu.
     * @param pageSize Số lượng đơn hàng trên mỗi trang.
     * @return ResponseEntity chứa danh sách các OrderResponse đã được phân trang và
     *         thông báo thành công.
     */
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> pagination(@RequestParam int pageNo,
                                                                       @RequestParam int pageSize) {
        var result = orderService.pagination(pageNo, pageSize);
        var message = new ApiMessage("Phân trang đơn hàng thành công", "Pagination success",
                DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok(new ApiResponse<>(message, result));
    }
}
