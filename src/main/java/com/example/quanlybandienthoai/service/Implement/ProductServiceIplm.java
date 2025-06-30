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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
                                                "Brand not found with id: " + request.getBrand_id()));
                Product product = new Product();
                product.setProduct_name(request.getProduct_name());
                product.setSpecification(request.getSpecification());
                product.setPrice(request.getPrice());
                product.setBrand(brand);
                productRepository.save(product);
                logger.info("Create product successfully with id: {}", product.getProduct_id());
                ProductResponse response = new ProductResponse(
                                product.getProduct_id(),
                                product.getProduct_name(),
                                product.getSpecification(),
                                product.getPrice(),
                                product.getImage(),
                                product.getRelease_date(),
                                product.getBrand());
                redisService.deleteByPattern("productPage::*");
                redisService.deleteByPattern("productKeyword:*");
                return response;
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
                                                        "Product not found with id: " + id);
                                });
                product.setProduct_name(request.getProduct_name());
                product.setSpecification(request.getSpecification());
                product.setPrice(request.getPrice());
                Brand brand = brandRepository.findById(request.getBrand_id())
                                .orElseThrow(() -> new AppException(
                                                DefinitionCode.NOT_FOUND,
                                                "Không tìm thấy thương hiệu",
                                                "Brand not found with id: " + request.getBrand_id()));
                product.setBrand(brand);
                productRepository.save(product);
                logger.info("Update product successfully with id: {}", product.getProduct_id());
                ProductResponse response = new ProductResponse(
                                product.getProduct_id(),
                                product.getProduct_name(),
                                product.getSpecification(),
                                product.getPrice(),
                                product.getImage(),
                                product.getRelease_date(),
                                product.getBrand());
                // Xóa cache sản phẩm theo id và danh sách
                redisService.delete("product:" + id);
                redisService.deleteByPattern("productPage::*");
                redisService.deleteByPattern("productKeyword:*");
                return response;
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
                                                        "Product not found with id: " + id);
                                });
                productRepository.delete(product);
                // Xóa cache sản phẩm theo id và danh sách
                redisService.delete("product:" + id);
                redisService.deleteByPattern("productPage::*");
                redisService.deleteByPattern("productKeyword:*");
        }

        /**
         * Lấy thông tin sản phẩm theo ID (có cache)
         */
        @Override
        public ProductResponse getProductById(long id) {
                long start = System.currentTimeMillis();
                String key = "product:" + id;
                Object cached = redisService.getValue(key);
                if (cached != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        logger.info("Get product from Redis cache with id: {}", id);
                        long end = System.currentTimeMillis();
                        logger.info("getProductById time: {} ms", (end - start));
                        ProductResponse cacheConvert = mapper.convertValue(cached, ProductResponse.class);
                        return cacheConvert;
                }
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> {
                                        logger.warn("Không tìm thấy sản phẩm với id: {}", id);
                                        return new AppException(
                                                        DefinitionCode.NOT_FOUND,
                                                        "Không tìm thấy sản phẩm",
                                                        "Product not found with id: " + id);
                                });
                ProductResponse response = new ProductResponse(
                                product.getProduct_id(),
                                product.getProduct_name(),
                                product.getSpecification(),
                                product.getPrice(),
                                product.getImage(),
                                product.getRelease_date(),
                                product.getBrand());
                redisService.setValue(key, response);
                long end = System.currentTimeMillis();
                logger.info("getProductById time: {} ms", (end - start));
                return response;
        }

        /**
         * Lấy thông tin sản phẩm theo ID (không dùng Redis, benchmark)
         */
        public ProductResponse getProductByIdNoCache(long id) {
                long start = System.currentTimeMillis();
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> {
                                        logger.warn("Không tìm thấy sản phẩm với id: {}", id);
                                        return new AppException(
                                                        DefinitionCode.NOT_FOUND,
                                                        "Không tìm thấy sản phẩm",
                                                        "Product not found with id: " + id);
                                });
                ProductResponse response = new ProductResponse(
                                product.getProduct_id(),
                                product.getProduct_name(),
                                product.getSpecification(),
                                product.getPrice(),
                                product.getImage(),
                                product.getRelease_date(),
                                product.getBrand());
                long end = System.currentTimeMillis();
                logger.info("getProductByIdNoCache time: {} ms", (end - start));
                return response;
        }

        /**
         * Tìm sản phẩm theo từ khoá
         */
        @Override
        public List<ProductResponse> getProductsByKeyword(String keyword) {
                long start = System.currentTimeMillis();
                String cacheKey = "productKeyword: " + keyword;
                Object cached = redisService.getValue(cacheKey);
                if (cached != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        List<ProductResponse> cachedList = mapper.convertValue(cached,
                                        new TypeReference<List<ProductResponse>>() {
                                        });
                        logger.info("Trả về từ cache Redis");
                        logger.info("Get list Product By Keyword: " + keyword);
                        long end = System.currentTimeMillis();
                        logger.info("getProducts DB query time: {} ms", (end - start));
                        return cachedList;

                }
                var listProducts = productRepository.getProductsByKeyword(keyword);
                List<ProductResponse> result = listProducts.stream().map(product -> new ProductResponse(
                                product.getProduct_id(),
                                product.getProduct_name(),
                                product.getSpecification(),
                                product.getPrice(),
                                product.getImage(),
                                product.getRelease_date(),
                                product.getBrand())).toList();
                // Lưu cache với TTL 5 phút (300 giây)
                redisService.setValue(cacheKey, result, 300);
                long end = System.currentTimeMillis();
                logger.info("getProducts DB query time: {} ms", (end - start));
                return result;
        }

        /**
         * Phân trang danh sách sản phẩm
         */
        @Override
        public Page<ProductResponse> Pagination(int pageNo, int pageSize) {
                long start = System.currentTimeMillis();
                String cacheKey = "productPage::" + pageNo + "_" + pageSize;
                Object cached = redisService.getValue(cacheKey);

                if (cached != null) {
                        try {
                                ObjectMapper mapper = new ObjectMapper();
                                mapper.registerModule(new JavaTimeModule());
                                // Cache chỉ lưu List<ProductResponse>, không lưu PageImpl
                                List<ProductResponse> cachedList = mapper.convertValue(
                                                cached, new TypeReference<List<ProductResponse>>() {
                                                });
                                Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
                                long end = System.currentTimeMillis();
                                logger.info("Trả về từ cache Redis");
                                logger.info("getProducts DB query time: {} ms", (end - start));
                                return new PageImpl<>(cachedList, pageable, cachedList.size());
                        } catch (Exception e) {
                                logger.warn("Lỗi khi convert List<ProductResponse>: {}", e.getMessage());
                        }
                }

                Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
                Page<ProductResponse> page = productRepository.findAll(pageable)
                                .map(product -> new ProductResponse(
                                                product.getProduct_id(),
                                                product.getProduct_name(),
                                                product.getSpecification(),
                                                product.getPrice(),
                                                product.getImage(),
                                                product.getRelease_date(),
                                                product.getBrand()));
                // Lưu cache chỉ là List<ProductResponse>
                redisService.setValue(cacheKey, page.getContent(), 600);
                long end = System.currentTimeMillis();
                logger.info("getProducts DB query time: {} ms", (end - start));
                return page;
        }

}
