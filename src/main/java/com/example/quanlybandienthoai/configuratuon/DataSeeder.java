package com.example.quanlybandienthoai.configuratuon;

import com.example.quanlybandienthoai.entity.Role;
import com.example.quanlybandienthoai.entity.User;
import com.example.quanlybandienthoai.repository.RoleRepository;
import com.example.quanlybandienthoai.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataSeeder {

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            if (!roleRepository.existsByName("ADMIN")) {
                Role role = new Role("ADMIN", "ADMIN");
                roleRepository.save(role);
            }
            if (!roleRepository.existsByName("USER")) {
                Role role = new Role("USER", "USER");
                roleRepository.save(role);
            }

            if (userRepository.findByUsername("admin") == null) {
                // Tạo và lưu Role
                var role1 = roleRepository.findByName("ADMIN");
                // Tạo danh sách Role
                Set<Role> roles = new HashSet<>();
                roles.add(role1);

                // Tạo và lưu User
                User user = new User();
                user.setUsername("admin");
                user.setPassword("123456");
                user.setEmail("admin@gmail.com");
                user.setRoles(roles);
                // Mã hóa mật khẩu
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                // Lưu User
                userRepository.save(user);
            }
            if (userRepository.findByUsername("user") == null) {
                // Tạo và lưu Role
                var role1 = roleRepository.findByName("USER");
                // Tạo danh sách Role
                Set<Role> roles = new HashSet<>();
                roles.add(role1);

                // Tạo và lưu User
                User user = new User();
                user.setUsername("user");
                user.setPassword("123456");
                user.setEmail("user@gmail.com");
                user.setRoles(roles);
                // Mã hóa mật khẩu
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                // Lưu User
                userRepository.save(user);
            }
        };
    }
}
