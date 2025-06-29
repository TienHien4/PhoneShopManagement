package com.example.quanlybandienthoai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String address;
    @ManyToMany
    private Set<Role> roles;
    private LocalDate registrated_date = LocalDate.now();
    @OneToOne(mappedBy = "user")
    private ShoppingCart shopping_cart;
}
