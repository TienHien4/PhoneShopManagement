package com.example.quanlybandienthoai.service;

import com.example.quanlybandienthoai.dto.Request.BrandRequest;
import com.example.quanlybandienthoai.dto.Response.BrandResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BrandService {
    BrandResponse createBrand(BrandRequest request);
    BrandResponse updateBrand(long id, BrandRequest request);
    void deleteBrand(long id);
    List<BrandResponse> getBrands();
    List<BrandResponse> getBrandsByBrandName(String keyword);
    Page<BrandResponse> pagination(int pageNo, int pageSize);
}
