package com.example.quanlybandienthoai.service.Implement;

import com.example.quanlybandienthoai.dto.Request.ProductRequest;
import com.example.quanlybandienthoai.dto.Response.ProductResponse;
import com.example.quanlybandienthoai.entity.Brand;
import com.example.quanlybandienthoai.entity.Product;
import com.example.quanlybandienthoai.enums.DefinitionCode;
import com.example.quanlybandienthoai.exception.AppException;
import com.example.quanlybandienthoai.repository.BrandRepository;
import com.example.quanlybandienthoai.repository.ProductRepository;
import com.example.quanlybandienthoai.service.ProductService;
import com.example.quanlybandienthoai.service.RedisService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation cho chức năng quản lý sản phẩm
 *
 * @author Nguyễn Tiến Hiền
 * @since 22/06/2025
 */
@Service
public class ProductServiceIplm implements ProductService {
        @Autowired
        private ProductRepository productRepository;
        @Autowired
        private BrandRepository brandRepository;
        @Autowired
        private RedisService redisService;

        private static final Logger logger = LogManager.getLogger(ProductServiceIplm.class);

        /**
         * Tạo mới sản phẩm
         */
        @Override
        public ProductResponse createProduct(ProductRequest request) {
                logger.info("ProductRequest: {}", request);
                Brand brand = brandRepository.findById(request.getBrand_id())
                        .orElseThrow(() -> new AppException(
                                DefinitionCode.NOT_FOUND,
                                "Không tìm thấy thương hiệu",
                                "Brand not found with id: " + request.getBrand_id()
                        ));
                Product product = new Product();
                product.setProduct_name(request.getProduct_name());
                product.setSpecification(request.getSpecification());
                product.setPrice(request.getPrice());
                product.setBrand(brand);
                productRepository.save(product);
                logger.info("Create product successfully with id: {}", product.getProduct_id());
                return new ProductResponse(
                        product.getProduct_id(),
                        product.getProduct_name(),
                        product.getSpecification(),
                        product.getPrice(),
                        product.getImage(),
                        product.getRelease_date(),
                        product.getBrand()
                );
        }

        /**
         * Cập nhật thông tin sản phẩm
         */
        @Override
        public ProductResponse updateProduct(long id, ProductRequest request) {
                Product product = productRepository.findById(id)
                        .orElseThrow(() -> {
                                logger.warn("Không tìm thấy sản phẩm với id: {}", id);
                                return new AppException(
                                        DefinitionCode.NOT_FOUND,
                                        "Không tìm thấy sản phẩm",
                                        "Product not found with id: " + id
                                );
                        });
                product.setProduct_name(request.getProduct_name());
                product.setSpecification(request.getSpecification());
                product.setPrice(request.getPrice());
                Brand brand = brandRepository.findById(request.getBrand_id())
                        .orElseThrow(() -> new AppException(
                                DefinitionCode.NOT_FOUND,
                                "Không tìm thấy thương hiệu",
                                "Brand not found with id: " + request.getBrand_id()
                        ));
                product.setBrand(brand);
                productRepository.save(product);
                redisService.delete("product:" + id);
                logger.info("Update product successfully with id: {}", product.getProduct_id());
                return new ProductResponse(
                        product.getProduct_id(),
                        product.getProduct_name(),
                        product.getSpecification(),
                        product.getPrice(),
                        product.getImage(),
                        product.getRelease_date(),
                        product.getBrand()
                );
        }

        /**
         * Xóa sản phẩm theo ID
         */
        @Override
        public void deleteProduct(long id) {
                Product product = productRepository.findById(id)
                        .orElseThrow(() -> {
                                logger.warn("Không tìm thấy sản phẩm với id: {}", id);
                                return new AppException(
                                        DefinitionCode.NOT_FOUND,
                                        "Không tìm thấy sản phẩm",
                                        "Product not found with id: " + id
                                );
                        });
                productRepository.delete(product);
                redisService.delete("product:" + id);
        }

        /**
         * Lấy thông tin sản phẩm theo ID (có cache)
         */
        @Override
        public ProductResponse getProductById(long id) {
                long start = System.currentTimeMillis();
                String key = "product:" + id;
                ProductResponse cached = (ProductResponse) redisService.getValue(key);
                if (cached != null) {
                        logger.info("Get product from Redis cache with id: {}", id);
                        long end = System.currentTimeMillis();
                        logger.info("getProductByIdCache time: {} ms", (end - start));
                        return cached;
                }
                Product product = productRepository.findById(id)
                        .orElseThrow(() -> {
                                logger.warn("Không tìm thấy sản phẩm với id: {}", id);
                                return new AppException(
                                        DefinitionCode.NOT_FOUND,
                                        "Không tìm thấy sản phẩm",
                                        "Product not found with id: " + id
                                );
                        });
                ProductResponse response = new ProductResponse(
                        product.getProduct_id(),
                        product.getProduct_name(),
                        product.getSpecification(),
                        product.getPrice(),
                        product.getImage(),
                        product.getRelease_date(),
                        product.getBrand()
                );
                redisService.setValue(key, response);
                long end = System.currentTimeMillis();
                logger.info("getProductByIdCache time: {} ms", (end - start));
                return response;
        }

        /**
         * Lấy thông tin sản phẩm theo ID (không dùng Redis)
         */
        public ProductResponse getProductByIdNoCache(long id) {
                long start = System.currentTimeMillis();
                Product product = productRepository.findById(id)
                        .orElseThrow(() -> {
                                logger.warn("Không tìm thấy sản phẩm với id: {}", id);
                                return new AppException(
                                        DefinitionCode.NOT_FOUND,
                                        "Không tìm thấy sản phẩm",
                                        "Product not found with id: " + id
                                );
                        });
                ProductResponse response = new ProductResponse(
                        product.getProduct_id(),
                        product.getProduct_name(),
                        product.getSpecification(),
                        product.getPrice(),
                        product.getImage(),
                        product.getRelease_date(),
                        product.getBrand()
                );
                long end = System.currentTimeMillis();
                logger.info("getProductByIdNoCache time: {} ms", (end - start));
                return response;
        }

        /**
         * Tìm sản phẩm theo từ khoá
         */
        @Override
        public List<ProductResponse> getProductsByKeyword(String keyword) {
                var listProducts = productRepository.getProductsByKeyword(keyword);
                return listProducts.stream().map(product -> new ProductResponse(
                        product.getProduct_id(),
                        product.getProduct_name(),
                        product.getSpecification(),
                        product.getPrice(),
                        product.getImage(),
                        product.getRelease_date(),
                        product.getBrand()
                )).toList();
        }

        /**
         * Phân trang danh sách sản phẩm
         */
        @Override
        public Page<ProductResponse> Pagination(int pageNo, int pageSize) {
                Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
                return productRepository.findAll(pageable)
                        .map(product -> new ProductResponse(
                                product.getProduct_id(),
                                product.getProduct_name(),
                                product.getSpecification(),
                                product.getPrice(),
                                product.getImage(),
                                product.getRelease_date(),
                                product.getBrand()
                        ));
        }
}
