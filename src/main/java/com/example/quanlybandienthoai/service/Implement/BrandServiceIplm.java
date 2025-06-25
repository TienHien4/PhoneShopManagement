package com.example.quanlybandienthoai.service.Implement;

import com.example.quanlybandienthoai.dto.Request.BrandRequest;
import com.example.quanlybandienthoai.dto.Response.BrandResponse;
import com.example.quanlybandienthoai.entity.Brand;
import com.example.quanlybandienthoai.enums.DefinitionCode;
import com.example.quanlybandienthoai.exception.AppException;
import com.example.quanlybandienthoai.repository.BrandRepository;
import com.example.quanlybandienthoai.service.BrandService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private static final Logger logger = LogManager.getLogger(BrandServiceIplm.class);

    /**
     * Tạo một thương hiệu mới
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
                    "Brand already exists: " + request.getBrand_name()
            );
        }

        // Tạo đối tượng Brand mới
        Brand brand = new Brand();
        brand.setBrandName(request.getBrand_name());
        brand.setCountry(request.getCountry());

        // Lưu vào CSDL
        brand = brandRepository.save(brand);
        logger.info("Tạo brand thành công, ID: {}", brand.getBrand_id());

        return toResponse(brand);
    }

    /**
     * Cập nhật thông tin thương hiệu theo ID
     * @param id ID thương hiệu cần cập nhật
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
                            "Brand not found: ID=" + id
                    );
                });

        // Cập nhật thông tin
        brand.setBrandName(request.getBrand_name());
        brand.setCountry(request.getCountry());

        // Lưu lại
        brand = brandRepository.save(brand);
        logger.info("Cập nhật brand thành công, ID: {}", id);

        return toResponse(brand);
    }

    /**
     * Xoá thương hiệu theo ID
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
                    "Brand not found: ID=" + id
            );
        }

        // Thực hiện xoá
        brandRepository.deleteById(id);
        logger.info("Xoá brand thành công, ID: {}", id);
    }

    /**
     * Lấy danh sách tất cả thương hiệu
     * @return danh sách các BrandResponse
     */
    @Override
    public List<BrandResponse> getBrands() {
        logger.info("Lấy danh sách tất cả thương hiệu");

        List<Brand> brands = brandRepository.findAll();
        return brands.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đối tượng Brand thành DTO BrandResponse
     * @param brand đối tượng Entity từ DB
     * @return BrandResponse
     */
    private BrandResponse toResponse(Brand brand) {
        return new BrandResponse(
                brand.getBrand_id(),
                brand.getBrandName(),
                brand.getCountry()
        );
    }
}
