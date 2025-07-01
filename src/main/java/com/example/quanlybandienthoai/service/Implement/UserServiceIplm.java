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
import com.example.quanlybandienthoai.service.RedisService;
import com.example.quanlybandienthoai.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        @Autowired
        private RedisService redisService;

        private static final Logger logger = LogManager.getLogger(UserServiceIplm.class);

        /**
         * Tạo mới một người dùng (customer).
         * 
         * @param request Dữ liệu đầu vào từ client
         * @return Thông tin khách hàng sau khi tạo
         */
        @Override
        public UserResponse createUser(UserRequest request) {
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

                // Xóa cache danh sách user và user theo id
                redisService.delete("userList");
                redisService.delete("userKeyword");
                redisService.delete("user:" + user.getUserId());

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
        public UserResponse updateUser(long id, UserUpdateRequest request) {
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

                // Xóa cache danh sách user và user theo id
                redisService.delete("userList");
                redisService.delete("userKeyword");
                redisService.delete("user:" + id);

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
        public UserResponse getUserById(long id) {
                long start = System.currentTimeMillis();
                // Kiểm tra cache trước
                Object cached = redisService.getValue("user:" + id);
                if (cached != null) {
                        logger.info("Trả về từ cache Redis");
                        long end = System.currentTimeMillis();
                        logger.info("getCustomer DB query time: {} ms", (end - start));
                        return (UserResponse) cached;
                }
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
                redisService.setValue("user:" + id, response);
                long end = System.currentTimeMillis();
                logger.info("getCustomerById time: {} ms", (end - start));
                return response;
        }

        /**
         * Lấy danh sách tất cả người dùng.
         */
        @Override
        public List<UserResponse> getUsers() {
                long start = System.currentTimeMillis();
                Object cached = redisService.getValue("userList");
                // Kiểm tra cache để get dữ liệu
                if (cached != null) {
                        try {
                                ObjectMapper mapper = new ObjectMapper();
                                mapper.registerModule(new JavaTimeModule());
                                // Chuyển object cache thành List<UserResponse>
                                List<UserResponse> cachedList = mapper.convertValue(cached, new TypeReference<List<UserResponse>>() {});
                                logger.info("Trả về từ cache Redis");
                                long end = System.currentTimeMillis();
                                logger.info("getCustomers DB query time: {} ms", (end - start));
                                return cachedList;
                        } catch (Exception e) {
                                logger.warn("Lỗi khi chuyển dữ liệu từ cache: {}", e.getMessage());
                        }
                }
                // Nếu không có cache → truy DB
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
                                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())))
                        .toList();

                // Lưu vào cache
                redisService.setValue("userList", result);
                long end = System.currentTimeMillis();
                logger.info("getCustomers DB query time: {} ms", (end - start));
                return result;
        }
        /**
         * Lấy danh sách tất cả người dùng theo từ khóa.
         */
        @Override
        public List<UserResponse> getUserByKeyword(String keyword) {
                String cacheKey = "userKeyword";
                Object cached = redisService.getValue(cacheKey);
                // Kiểm tra cache để get dữ liệu
                if(cached != null){
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        var cachedList = mapper.convertValue(cached, new TypeReference<List<UserResponse>>() {});
                        logger.info("Get data from redis");
                        return cachedList;
                }
                // Lấy dữ liệu từ db
                var listUser = userRepository.findUsersByKeyword(keyword);
                var result = listUser.stream().map(user -> {
                      UserResponse userResponse = new UserResponse(
                              user.getUserId(),
                              user.getUsername(),
                              user.getEmail(),
                              user.getPassword(),
                              user.getPhone(),
                              user.getAddress(),
                              user.getRegistrated_date(),
                              user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet())
                      );
                      return  userResponse;
                }).toList();
                // Lưu vào cache
                redisService.setValue(cacheKey, result);
                return result;
        }
        /**
         * Lấy danh sách tất cả người dùng phân trang.
         */
        @Override
        public Page<UserResponse> pagination(int pageNo, int pageSize) {
                String cacheKey = "userPage::" + pageNo + "-" + pageSize;
                Object cached = redisService.getValue(cacheKey);
                // Kiểm tra cache để get dữ liệu
                if(cached != null){
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        var cachedList = mapper.convertValue(cached, new TypeReference<List<UserResponse>>() {});
                        Pageable pageable = PageRequest.of(pageNo-1, pageSize);
                        return new PageImpl<>(cachedList, pageable, cachedList.size());
                }
                // Lấy dữ liệu từ db
                Pageable pageable = PageRequest.of(pageNo-1, pageSize);
                var pageUser = userRepository.findAll(pageable);
                var result = pageUser.map(user -> {
                        UserResponse userResponse = new UserResponse(
                                user.getUserId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getPassword(),
                                user.getPhone(),
                                user.getAddress(),
                                user.getRegistrated_date(),
                                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
                        );
                        return  userResponse;
                });
                // Lưu vào cache
                redisService.setValue(cacheKey, result.getContent());
                return result;
        }


}
