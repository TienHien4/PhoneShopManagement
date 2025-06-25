package com.example.quanlybandienthoai.service;

import com.example.quanlybandienthoai.dto.Request.ProductRequest;
import com.example.quanlybandienthoai.dto.Response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(long id, ProductRequest request);
    void deleteProduct(long id);
    ProductResponse getProductById(long id);
    List<ProductResponse> getProductsByKeyword(String keyword);
    Page<ProductResponse> Pagination(int pageNo, int pageSize);

}
