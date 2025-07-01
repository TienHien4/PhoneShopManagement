package com.example.quanlybandienthoai.repository;

import com.example.quanlybandienthoai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
       * @return người dùng
       */
      User findByEmail(String email);
      @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = :username")
      User findByUsername(@Param("username") String username);

      /**
       * Tìm kiếm người dùng theo từ khóa.
       *
       * @param keyword từ khóa mà người dùng nhập
       * @return danh sách người dùng
       */
      @Query("SELECT u from User u where u.username like concat('%', ?1, '%') " +
              "or u.address like concat('%', ?1, '%')" +
              "or u.phone like concat('%', ?1, '%') ")
      List<User> findUsersByKeyword(String keyword);


}
