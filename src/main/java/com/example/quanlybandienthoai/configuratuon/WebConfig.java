package com.example.quanlybandienthoai.configuratuon;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cấu hình CORS (Cross-Origin Resource Sharing) cho toàn bộ ứng dụng Spring Boot.
 *
 * Cho phép frontend (React, chạy ở cổng 3000) gọi các API của backend (Spring Boot)
 * thông qua các phương thức như GET, POST, PUT, DELETE.
 *
 * Đây là cấu hình cần thiết để frontend và backend hoạt động với nhau khi phát triển ứng dụng web fullstack.
 * @author Nguyễn Tiến Hiền
 * @since 25/06/2025
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Ghi đè phương thức addCorsMappings để cấu hình chính sách CORS cho toàn bộ ứng dụng.
     * @param registry đối tượng cấu hình CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Áp dụng cho tất cả các endpoint
                .allowedOrigins("http://localhost:3000") // Cho phép truy cập từ frontend chạy trên localhost:3000
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Các HTTP methods được phép
                .allowCredentials(true); // Cho phép gửi cookie và thông tin xác thực kèm request
    }
}

