
package com.example.quanlybandienthoai.controller;

import com.example.quanlybandienthoai.dto.ApiMessage;
import com.example.quanlybandienthoai.dto.ApiResponse;
import com.example.quanlybandienthoai.dto.Request.ProductRequest;
import com.example.quanlybandienthoai.dto.Response.ProductResponse;
import com.example.quanlybandienthoai.enums.DefinitionCode;
import com.example.quanlybandienthoai.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controller xử lý các API liên quan đến sản phẩm
 * @author Nguyễn Tiến Hiền
 * @since 22/06/2025
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * API tạo mới sản phẩm
     * @param request Thông tin sản phẩm cần tạo
     * @return ProductResponse chứa thông tin sản phẩm vừa tạo
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody @Valid ProductRequest request) {
        var result = productService.createProduct(request);
        ApiMessage message = new ApiMessage("Tạo sản phẩm thành công",
                "Product created successfully", DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok().body(new ApiResponse<>(message, result));
    }

    /**
     * API cập nhật thông tin sản phẩm
     * @param id      Mã sản phẩm cần cập nhật
     * @param request Thông tin cập nhật
     * @return ProductResponse chứa thông tin mới
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable int id,
            @RequestBody @Valid ProductRequest request) {
        var result = productService.updateProduct(id, request);
        var message = new ApiMessage("Cập nhật sản phẩm thành công",
                "Product updated successfully", DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok().body(new ApiResponse<>(message, result));
    }

    /**
     * API xóa sản phẩm theo ID
     * @param id Mã sản phẩm cần xóa
     * @return Thông báo xóa thành công
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        var message = new ApiMessage("Xóa sản phẩm thành công",
                "Product deleted successfully", DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok().body(new ApiResponse<>(message, null));
    }

    /**
     * API tìm sản phẩm theo ID
     * @param id Mã sản phẩm
     * @return Thông tin sản phẩm
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable int id) {
        var result = productService.getProductById(id);
        var message = new ApiMessage("Tìm sản phẩm theo mã thành công",
                "Find product by ID successfully", DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok().body(new ApiResponse<>(message, result));
    }

    /**
     * API tìm kiếm sản phẩm theo từ khóa (tên sản phẩm)
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách sản phẩm phù hợp
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductByKeyword(@RequestParam String keyword) {
        var result = productService.getProductsByKeyword(keyword);
        var message = new ApiMessage("Tìm sản phẩm theo từ khóa thành công",
                "Find products by keyword successfully", DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok().body(new ApiResponse<>(message, result));
    }

    /**
     * API phân trang danh sách sản phẩm
     * @param pageNo   Trang số (bắt đầu từ 1)
     * @param pageSize Số lượng sản phẩm mỗi trang (default = 8)
     * @return Page<ProductResponse> chứa thông tin phân trang
     */
    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsPagination(
            @RequestParam int pageNo,
            @RequestParam(defaultValue = "8") int pageSize) {
        var result = productService.Pagination(pageNo, pageSize);
        var message = new ApiMessage("Lấy danh sách sản phẩm thành công",
                "Get products successfully", DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok().body(new ApiResponse<>(message, result));
    }
}
