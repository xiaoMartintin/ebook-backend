package com.klx.microservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "books")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "isbn", nullable = false)
    private String isbn;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "price")
    private Double price = 0.00;

    @Column(name = "description", length = 2000)
    private String description = "暂无描述";

    @Column(name = "inventory", nullable = false)
    private Integer inventory = 0;

    @Column(name = "image", nullable = false)
    private String cover;

    @Column(name = "sales", nullable = false, columnDefinition = "int default 0")
    private Integer sales = 0;

    @PrePersist
    @PreUpdate
    public void formatPrice() {
        if (this.price != null) {
            BigDecimal bd = BigDecimal.valueOf(this.price).setScale(2, RoundingMode.HALF_UP);
            this.price = bd.doubleValue();
        }
    }
}

//在这个实现中，我们使用了@PrePersist和@PreUpdate注解，确保在保存和更新实体时调用formatPrice()方法。formatPrice()方法会将价格四舍五入保留两位小数
    //这样做可以确保在数据库中存储的价格始终是两位小数的格式，避免了在前端或其他层次进行格式化处理。

