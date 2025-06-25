package com.example.quanlybandienthoai.service.Implement;

import com.example.quanlybandienthoai.dto.Request.LoginRequest;
import com.example.quanlybandienthoai.dto.Request.LogoutRequest;
import com.example.quanlybandienthoai.dto.Request.UserRequest;
import com.example.quanlybandienthoai.dto.Request.UserUpdateRequest;
import com.example.quanlybandienthoai.dto.Response.LoginResponse;
import com.example.quanlybandienthoai.dto.Response.UserResponse;
import com.example.quanlybandienthoai.entity.User;
import com.example.quanlybandienthoai.enums.DefinitionCode;
import com.example.quanlybandienthoai.exception.AppException;
import com.example.quanlybandienthoai.repository.UserRepository;
import com.example.quanlybandienthoai.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: Nguyễn Tiến Hiền
 * @since: 2025-06-22
 * Lớp xử lý nghiệp vụ liên quan đến người dùng.
 */
@Service
public class UserServiceIplm implements UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LogManager.getLogger(UserServiceIplm.class);

    /**
     * Tạo mới một người dùng (customer).
     * @param request Dữ liệu đầu vào từ client
     * @return Thông tin khách hàng sau khi tạo
     */
    @Override
    public UserResponse createCustomer(UserRequest request) {
        logger.info("Bắt đầu tạo khách hàng với email: {}", request.getEmail());

        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Email '{}' đã tồn tại", request.getEmail());
            throw new AppException(
                    DefinitionCode.EXISTS,
                    "Email đã tồn tại",
                    "Email already exists: " + request.getEmail()
            );
        }

        // Tạo mới user và lưu vào database
        User user = new User();
        user.setFull_name(request.getFull_name());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        userRepository.save(user);
        logger.info("Tạo khách hàng thành công, ID: {}", user.getUserId());

        // Trả về response
        return new UserResponse(
                user.getUserId(),
                user.getFull_name(),
                user.getEmail(),
                user.getPassword(),
                user.getPhone(),
                user.getAddress(),
                user.getRegistrated_date()
        );
    }

    /**
     * Cập nhật thông tin người dùng theo ID.
     */
    @Override
    public UserResponse updateCustomer(long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Không tìm thấy khách hàng với ID: {}", id);
                    return new AppException(
                            DefinitionCode.NOT_FOUND,
                            "Không tìm thấy khách hàng",
                            "No customer with id: " + id
                    );
                });

        user.setFull_name(request.getFull_name());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        userRepository.save(user);
        logger.info("Sửa khách hàng thành công, ID: {}", user.getUserId());

        return new UserResponse(
                user.getUserId(),
                user.getFull_name(),
                user.getEmail(),
                user.getPassword(),
                user.getPhone(),
                user.getAddress(),
                user.getRegistrated_date()
        );
    }

    /**
     * Lấy thông tin người dùng theo ID.
     */
    @Override
    public UserResponse getCustomerById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        DefinitionCode.NOT_FOUND,
                        "Không tìm thấy người dùng",
                        "User not found with id: " + id
                ));

        return new UserResponse(
                user.getUserId(),
                user.getFull_name(),
                user.getEmail(),
                user.getPassword(),
                user.getPhone(),
                user.getAddress(),
                user.getRegistrated_date()
        );
    }

    /**
     * Lấy danh sách tất cả người dùng.
     */
    @Override
    public List<UserResponse> getCustomers() {
        List<User> listUsers = userRepository.findAll();

        // Chuyển đổi danh sách User -> UserResponse
        return listUsers.stream()
                .map(user -> new UserResponse(
                        user.getUserId(),
                        user.getFull_name(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getPhone(),
                        user.getAddress(),
                        user.getRegistrated_date()
                )).toList();
    }

    /**
     * Đăng nhập người dùng.
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(
                    DefinitionCode.NOT_FOUND,
                    "Email chưa được đăng ký",
                    "Email not found"
            );
        }

        User user = userRepository.findByEmail(request.getEmail());
        if (!user.getPassword().equals(request.getPassword())) {
            throw new AppException(
                    DefinitionCode.NOT_FOUND,
                    "Mật khẩu sai",
                    "Password is invalid"
            );
        }

        return new LoginResponse(user.getUserId());
    }

    /**
     * Đăng xuất người dùng.
     */
    @Override
    public void logout(LogoutRequest request) {
        User user = userRepository.findById(request.getUser_id())
                .orElseThrow(() -> new AppException(
                        DefinitionCode.NOT_FOUND,
                        "Không tìm thấy người dùng",
                        "User not found"
                ));
        logger.info("Người dùng với id {} đã đăng xuất", request.getUser_id());
    }
}
