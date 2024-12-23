package com.klx.ebookbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "user_id", columnList = "user_id")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(name = "created_at")
    private Instant time;

    @Column(name = "receiver_name", nullable = false)
    private String receiver;

    @Column(name = "receiver_address", nullable = false)
    private String address;

    @Column(name = "tel", nullable = false)
    private String tel;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<OrderItem> orderItems = new LinkedHashSet<>();

    public void updateTotalPrice() {
        this.totalPrice = this.orderItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getBook().getPrice())
                .sum();
    }
}
