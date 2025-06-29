package com.example.quanlybandienthoai.repository;

import com.example.quanlybandienthoai.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, Long> {
    boolean existsByUUID(String id);
}
