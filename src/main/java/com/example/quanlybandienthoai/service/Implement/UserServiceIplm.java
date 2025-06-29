package com.example.quanlybandienthoai.service.Implement;

import com.example.quanlybandienthoai.dto.Request.LoginRequest;
import com.example.quanlybandienthoai.dto.Request.LogoutRequest;
import com.example.quanlybandienthoai.dto.Request.UserRequest;
import com.example.quanlybandienthoai.dto.Request.UserUpdateRequest;
import com.example.quanlybandienthoai.dto.Response.LoginResponse;
import com.example.quanlybandienthoai.dto.Response.UserResponse;
import com.example.quanlybandienthoai.entity.Role;
import com.example.quanlybandienthoai.entity.User;
import com.example.quanlybandienthoai.enums.DefinitionCode;
import com.example.quanlybandienthoai.exception.AppException;
import com.example.quanlybandienthoai.repository.RoleRepository;
import com.example.quanlybandienthoai.repository.UserRepository;
import com.example.quanlybandienthoai.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: Nguyễn Tiến Hiền
 * @since: 2025-06-22
 *         Lớp xử lý nghiệp vụ liên quan đến người dùng.
 */
@Service
public class UserServiceIplm implements UserService {

        @Autowired
        private UserRepository userRepository;
        @Autowired
        private RoleRepository roleRepository;

        private static final Logger logger = LogManager.getLogger(UserServiceIplm.class);

        /**
         * Tạo mới một người dùng (customer).
         * 
         * @param request Dữ liệu đầu vào từ client
         * @return Thông tin khách hàng sau khi tạo
         */
        @Override
        @CacheEvict(value = { "user", "userList" }, allEntries = true)
        public UserResponse createCustomer(UserRequest request) {
                logger.info("Bắt đầu tạo khách hàng với email: {}", request.getEmail());

                // Kiểm tra email đã tồn tại chưa
                if (userRepository.existsByEmail(request.getEmail())) {
                        logger.warn("Email '{}' đã tồn tại", request.getEmail());
                        throw new AppException(
                                        DefinitionCode.EXISTS,
                                        "Email đã tồn tại",
                                        "Email already exists: " + request.getEmail());
                }
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

                // Tạo mới user và lưu vào database
                User user = new User();
                user.setUsername(request.getUsername());
                user.setEmail(request.getEmail());
                user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
                user.setPhone(request.getPhone());
                user.setAddress(request.getAddress());
                Role role = roleRepository.findByName("USER");
                Set<Role> roles = new HashSet<>();
                roles.add(role);
                user.setRoles(roles);
                userRepository.save(user);
                logger.info("Tạo khách hàng thành công, ID: {}", user.getUserId());

                // Trả về response
                return new UserResponse(
                                user.getUserId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getPassword(),
                                user.getPhone(),
                                user.getAddress(),
                                user.getRegistrated_date(),
                                user.getRoles().stream().map(s -> s.getName()).collect(Collectors.toSet()));
        }

        /**
         * Cập nhật thông tin người dùng theo ID.
         */
        @Override
        @CacheEvict(value = { "userList", "user" }, allEntries = true)
        public UserResponse updateCustomer(long id, UserUpdateRequest request) {
                User user = userRepository.findById(id)
                                .orElseThrow(() -> {
                                        logger.warn("Không tìm thấy khách hàng với ID: {}", id);
                                        return new AppException(
                                                        DefinitionCode.NOT_FOUND,
                                                        "Không tìm thấy khách hàng",
                                                        "No customer with id: " + id);
                                });

                user.setUsername(request.getUsername());
                user.setPhone(request.getPhone());
                user.setAddress(request.getAddress());
                userRepository.save(user);
                logger.info("Sửa khách hàng thành công, ID: {}", user.getUserId());

                return new UserResponse(
                                user.getUserId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getPassword(),
                                user.getPhone(),
                                user.getAddress(),
                                user.getRegistrated_date(),
                                user.getRoles().stream().map(s -> s.getName()).collect(Collectors.toSet()));
        }

        /**
         * Lấy thông tin người dùng theo ID (có cache)
         */
        @Override
        @Cacheable(value = "user", key = "#id")
        public UserResponse getCustomerById(long id) {
                long start = System.currentTimeMillis();
                User user = userRepository.findById(id)
                                .orElseThrow(() -> new AppException(
                                                DefinitionCode.NOT_FOUND,
                                                "Không tìm thấy người dùng",
                                                "User not found with id: " + id));
                UserResponse response = new UserResponse(
                                user.getUserId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getPassword(),
                                user.getPhone(),
                                user.getAddress(),
                                user.getRegistrated_date(),
                                user.getRoles().stream().map(s -> s.getName()).collect(Collectors.toSet()));
                long end = System.currentTimeMillis();
                logger.info("getCustomerById time: {} ms", (end - start));
                return response;
        }

        /**
         * Lấy danh sách tất cả người dùng.
         */
        @Override
        @Cacheable(value = "userList")
        public List<UserResponse> getCustomers() {
                long start = System.currentTimeMillis();
                List<User> listUsers = userRepository.findAll();
                List<UserResponse> result = listUsers.stream()
                                .map(user -> new UserResponse(
                                                user.getUserId(),
                                                user.getUsername(),
                                                user.getEmail(),
                                                user.getPassword(),
                                                user.getPhone(),
                                                user.getAddress(),
                                                user.getRegistrated_date(),
                                                user.getRoles().stream().map(s -> s.getName())
                                                                .collect(Collectors.toSet())))
                                .toList();
                long end = System.currentTimeMillis();
                logger.info("getCustomers time: {} ms", (end - start));
                return result;
        }
}
