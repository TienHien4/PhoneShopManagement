package com.example.quanlybandienthoai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long product_id;
    private String product_name;
    private String specification;
    private double price;
    private String image;
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;
    private LocalDate release_date;
}
