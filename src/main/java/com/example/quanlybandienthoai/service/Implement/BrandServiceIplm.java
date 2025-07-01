package com.example.quanlybandienthoai.service.Implement;

import com.example.quanlybandienthoai.dto.Request.BrandRequest;
import com.example.quanlybandienthoai.dto.Response.BrandResponse;
import com.example.quanlybandienthoai.entity.Brand;
import com.example.quanlybandienthoai.enums.DefinitionCode;
import com.example.quanlybandienthoai.exception.AppException;
import com.example.quanlybandienthoai.repository.BrandRepository;
import com.example.quanlybandienthoai.service.BrandService;
import com.example.quanlybandienthoai.service.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation cho chức năng quản lý thương hiệu
 *
 * @author Nguyễn Tiến Hiền
 * @since 22/06/2025
 */
@Service
public class BrandServiceIplm implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private RedisService redisService;

    private static final Logger logger = LogManager.getLogger(BrandServiceIplm.class);

    /**
     * Tạo một thương hiệu mới
     * 
     * @param request dữ liệu thương hiệu từ client
     * @return đối tượng BrandResponse chứa thông tin thương hiệu vừa tạo
     */
    @Override
    public BrandResponse createBrand(BrandRequest request) {
        logger.info("Bắt đầu tạo brand với tên: {}", request.getBrand_name());

        // Kiểm tra xem tên thương hiệu đã tồn tại chưa
        if (brandRepository.existsByBrandName(request.getBrand_name())) {
            logger.warn("Brand '{}' đã tồn tại", request.getBrand_name());
            throw new AppException(
                    DefinitionCode.EXISTS,
                    "Tên thương hiệu đã tồn tại",
                    "Brand already exists: " + request.getBrand_name());
        }

        // Tạo đối tượng Brand mới
        Brand brand = new Brand();
        brand.setBrandName(request.getBrand_name());
        brand.setCountry(request.getCountry());

        // Lưu vào CSDL
        brand = brandRepository.save(brand);
        logger.info("Tạo brand thành công, ID: {}", brand.getBrand_id());

        // Xóa cache danh sách brand và brand theo id
        redisService.delete("brandList");
        redisService.delete("brand:" + brand.getBrand_id());

        return new BrandResponse(
                brand.getBrand_id(),
                brand.getBrandName(),
                brand.getCountry());
    };

    /**
     * Cập nhật thông tin thương hiệu theo ID
     * 
     * @param id      ID thương hiệu cần cập nhật
     * @param request dữ liệu mới từ client
     * @return BrandResponse chứa thông tin đã cập nhật
     */
    @Override
    public BrandResponse updateBrand(long id, BrandRequest request) {
        logger.info("Bắt đầu cập nhật brand, ID: {}", id);

        // Lấy brand từ DB, nếu không có thì ném ngoại lệ
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Không tìm thấy brand với ID: {}", id);
                    return new AppException(
                            DefinitionCode.NOT_FOUND,
                            "Không tìm thấy thương hiệu",
                            "Brand not found: ID=" + id);
                });

        // Cập nhật thông tin
        brand.setBrandName(request.getBrand_name());
        brand.setCountry(request.getCountry());

        // Lưu lại
        brand = brandRepository.save(brand);
        logger.info("Cập nhật brand thành công, ID: {}", id);

        // Xóa cache danh sách brand và brand theo id
        redisService.delete("brandList");
        redisService.delete("brand:" + id);

        return new BrandResponse(
                brand.getBrand_id(),
                brand.getBrandName(),
                brand.getCountry());
    };

    /**
     * Xoá thương hiệu theo ID
     * 
     * @param id ID thương hiệu cần xoá
     */
    @Override
    public void deleteBrand(long id) {
        logger.info("Yêu cầu xóa brand với ID: {}", id);

        // Kiểm tra tồn tại
        if (!brandRepository.existsById(id)) {
            logger.warn("Không tìm thấy brand để xoá, ID: {}", id);
            throw new AppException(
                    DefinitionCode.NOT_FOUND,
                    "Không tìm thấy thương hiệu để xoá",
                    "Brand not found: ID=" + id);
        }

        // Thực hiện xoá
        brandRepository.deleteById(id);
        logger.info("Xoá brand thành công, ID: {}", id);

        // Xóa cache danh sách brand và brand theo id
        redisService.delete("brandList");
        redisService.delete("brand:" + id);
    }

    /**
     * Lấy danh sách tất cả thương hiệu
     * 
     * @return danh sách các BrandResponse
     */
    @Override
    public List<BrandResponse> getBrands() {
        String cacheKey = "brandList";
        Object cached = redisService.getValue(cacheKey);
        if (cached != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<BrandResponse> cachedList = mapper.convertValue(cached, new TypeReference<List<BrandResponse>>() {
                });
                logger.info("Trả về danh sách brand từ cache");
                return cachedList;
            } catch (Exception e) {
                logger.warn("Lỗi khi ép kiểu dữ liệu cache brandList: {}", e.getMessage());
            }
        }

        List<Brand> brands = brandRepository.findAll();
        List<BrandResponse> result = brands.stream()
                .map(brand -> {
                    BrandResponse brandResponse = new BrandResponse(
                            brand.getBrand_id(),
                            brand.getBrandName(),
                            brand.getCountry());
                    return brandResponse;
                }).collect(Collectors.toList());
        redisService.setValue(cacheKey, result);
        return result;
    }

    @Override
    public List<BrandResponse> getBrandsByBrandName(String keyword) {
        String cacheKey = "brandKeyword:" + keyword;
        Object cached = redisService.getValue(cacheKey);
        if (cached != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<BrandResponse> cachedList = mapper.convertValue(cached, new TypeReference<List<BrandResponse>>() {
                });
                logger.info("Trả về danh sách brand theo keyword từ cache");
                return cachedList;
            } catch (Exception e) {
                logger.warn("Lỗi khi ép kiểu dữ liệu cache brandKeyword: {}", e.getMessage());
            }
        }
        List<Brand> brands = brandRepository.findByByKeyword(keyword);
        List<BrandResponse> result = brands.stream()
                .map(brand -> new BrandResponse(
                        brand.getBrand_id(),
                        brand.getBrandName(),
                        brand.getCountry()))
                .collect(Collectors.toList());
        // Lưu cache với TTL 5 phút
        redisService.setValue(cacheKey, result, 300);
        return result;
    }

    @Override
    public Page<BrandResponse> pagination(int pageNo, int pageSize) {
        String cacheKey = "brandPage::" + pageNo + "_" + pageSize;
        Object cached = redisService.getValue(cacheKey);
        if (cached != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<BrandResponse> cachedList = mapper.convertValue(cached, new TypeReference<List<BrandResponse>>() {
                });
                org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest
                        .of(pageNo - 1, pageSize);
                return new org.springframework.data.domain.PageImpl<>(cachedList, pageable, cachedList.size());
            } catch (Exception e) {
                logger.warn("Lỗi khi ép kiểu dữ liệu cache brandPage: {}", e.getMessage());
            }
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<BrandResponse> result = brandRepository.findAll(pageable).map(brand -> {
            BrandResponse brandResponse = new BrandResponse(
                    brand.getBrand_id(),
                    brand.getBrandName(),
                    brand.getCountry());
            return brandResponse;
        });

        redisService.setValue(cacheKey, result.getContent(), 300);
        return result;
    }
}
