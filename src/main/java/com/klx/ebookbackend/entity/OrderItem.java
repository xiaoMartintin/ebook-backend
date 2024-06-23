package com.klx.ebookbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
        }
    }

    @PreRemove
    private void revertBookInventoryAndSales() {
        if (this.book != null && this.quantity != null) {
            // 恢复库存和销售数量
            book.setInventory(book.getInventory() + this.quantity);
            book.setSales(book.getSales() - this.quantity);
        }
    }
}


//重要！！！
//在 OrderItem 类中的 Book 字段上使用了 @JsonBackReference，这会导致在序列化时忽略 Book 字段。因此，前端看不到 Book 对象中的数据。
//要解决这个问题，你可以将 OrderItem 类中的 Book 字段的注解修改为 @JsonManagedReference，并确保 Book 类中的 OrderItem 字段保持 @JsonBackReference 注解。