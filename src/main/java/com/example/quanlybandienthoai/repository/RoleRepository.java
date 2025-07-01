package com.example.quanlybandienthoai.repository;

import com.example.quanlybandienthoai.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(String roleName);
    Role findByName(String name);
}
