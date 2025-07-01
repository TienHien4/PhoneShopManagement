package com.example.quanlybandienthoai.controller;

import com.example.quanlybandienthoai.dto.ApiMessage;
import com.example.quanlybandienthoai.dto.ApiResponse;
import com.example.quanlybandienthoai.dto.Request.BrandRequest;
import com.example.quanlybandienthoai.dto.Response.BrandResponse;
import com.example.quanlybandienthoai.enums.DefinitionCode;
import com.example.quanlybandienthoai.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller xử lý các thao tác liên quan đến Brand
 * 
 * @author Nguyễn Tiến Hiền
 * @since 24/06/2025
 */
@RestController
@RequestMapping("/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * API tạo mới một thương hiệu.
     * 
     * @param request BrandRequest chứa thông tin thương hiệu cần tạo.
     * @return ResponseEntity chứa thông điệp và thông tin thương hiệu vừa được tạo.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@RequestBody @Valid BrandRequest request) {
        var result = brandService.createBrand(request);
        var message = new ApiMessage("Tạo brand thành công", "Brand created", DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok(new ApiResponse<>(message, result));
    }

    /**
     * API cập nhật thông tin thương hiệu theo ID.
     * 
     * @param id      ID thương hiệu cần cập nhật.
     * @param request BrandRequest chứa thông tin thương hiệu mới.
     * @return ResponseEntity chứa thông tin thương hiệu sau khi cập nhật.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(@PathVariable long id,
            @RequestBody @Valid BrandRequest request) {
        var result = brandService.updateBrand(id, request);
        var message = new ApiMessage("Cập nhật brand thành công", "Brand updated", DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok(new ApiResponse<>(message, result));
    }

    /**
     * API xoá thương hiệu theo ID.
     * 
     * @param id ID thương hiệu cần xoá.
     * @return ResponseEntity chứa thông điệp xác nhận xoá thành công.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable long id) {
        brandService.deleteBrand(id);
        var message = new ApiMessage("Xoá brand thành công", "Brand deleted", DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok(new ApiResponse<>(message, null));
    }

    /**
     * API lấy danh sách tất cả thương hiệu trong hệ thống.
     * 
     * @return ResponseEntity chứa danh sách BrandResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands() {
        var result = brandService.getBrands();
        var message = new ApiMessage("Lấy danh sách brand thành công", "Fetched successfully",
                DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok(new ApiResponse<>(message, result));
    }

    /**
     * API tìm kiếm thương hiệu theo tên.
     * 
     * @param keyword Từ khoá tìm kiếm.
     * @return ResponseEntity chứa danh sách BrandResponse phù hợp
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getBrandsByBrandName(@RequestParam String keyword) {
        var result = brandService.getBrandsByBrandName(keyword);
        var message = new ApiMessage("Tìm kiếm brand thành công", "Search success", DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok(new ApiResponse<>(message, result));
    }

    /**
     * API phân trang danh sách thương hiệu.
     * 
     * @param pageNo   Số trang cần lấy dữ liệu.
     * @param pageSize Số lượng thương hiệu trên mỗi trang.
     * @return ResponseEntity chứa trang BrandResponse
     */
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<BrandResponse>>> pagination(@RequestParam int pageNo,
            @RequestParam int pageSize) {
        var result = brandService.pagination(pageNo, pageSize);
        var message = new ApiMessage("Phân trang brand thành công", "Pagination success",
                DefinitionCode.SUCCESS.getCode());
        return ResponseEntity.ok(new ApiResponse<>(message, result));
    }
}
