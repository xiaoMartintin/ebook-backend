package com.klx.ebookbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@Entity
@Table(name = "order_items", indexes = {
        @Index(name = "order_id", columnList = "order_id"),
        @Index(name = "book_id", columnList = "book_id")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    @JsonManagedReference
    private Book book;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;



    @PrePersist
    @PreUpdate
    private void updateBookInventoryAndSales() {
        if (this.book != null && this.quantity != null) {
            // 更新库存和销售数量
            book.setInventory(book.getInventory() - this.quantity);
            book.setSales(book.getSales() + this.quantity);

            // 计算订单项总价，保留两位小数
            double orderItemTotalPrice = roundToTwoDecimalPlaces(this.quantity * this.book.getPrice());

            // 更新用户余额
            User user = this.order.getUser();
            user.setBalance(user.getBalance() - orderItemTotalPrice);

            // 更新订单总价
            this.order.updateTotalPrice();
        }
    }

    private double roundToTwoDecimalPlaces(double value) {
        return Math.round(value * 100) / 100.0;
    }


//    贴合实际的话，是不能恢复的，因为不管你后来书怎么变，订单只要完成了，订单里的书的价格等信息就是买下时的状态

//    @PreRemove
//    private void revertBookInventoryAndSales() {
//        if (this.book != null && this.quantity != null) {
//            // 恢复库存和销售数量
//            book.setInventory(book.getInventory() + this.quantity);
//            book.setSales(book.getSales() - this.quantity);
//
//            // 恢复用户余额
//            User user = this.order.getUser();
//            double orderItemTotalPrice = this.quantity * this.book.getPrice();
//            user.setBalance(user.getBalance() + orderItemTotalPrice);
//
//            // 更新订单总价
//            this.order.updateTotalPrice();
//        }
//    }
}
