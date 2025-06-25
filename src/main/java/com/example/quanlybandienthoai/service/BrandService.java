package com.example.quanlybandienthoai.service;

import com.example.quanlybandienthoai.dto.Request.BrandRequest;
import com.example.quanlybandienthoai.dto.Response.BrandResponse;
import java.util.List;

public interface BrandService {
    BrandResponse createBrand(BrandRequest request);
    BrandResponse updateBrand(long id, BrandRequest request);
    void deleteBrand(long id);
    List<BrandResponse> getBrands();
}
