package com.example.quanlybandienthoai.repository;

import com.example.quanlybandienthoai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
      /**
       * Kiểm tra email đã tồn tại hay chưa.
       *
       * @param email của người dùng
       * @return true nếu email đã tồn tại, ngược lại false
       */
      boolean existsByEmail(String email);
      /**
       * Tìm kiếm người dùng theo email.
       *
       * @param email của người dùng
       * @return thông tin người dùng nếu tìm thấy
       */
      User findByEmail(String email);
}
