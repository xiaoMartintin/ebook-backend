package com.klx.ebookbackend.repository;

import com.klx.ebookbackend.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(Integer userId);

    @Query("SELECT o FROM Order o WHERE "
            + "(:userId IS NULL OR o.user.id = :userId) "
            + "AND (:keyword IS NULL OR o.receiver LIKE %:keyword% OR o.tel LIKE %:keyword% OR o.address LIKE %:keyword% OR EXISTS (SELECT 1 FROM o.orderItems oi WHERE oi.book.title LIKE %:keyword%)) "
            + "AND (:startInstant IS NULL OR o.time >= :startInstant) "
            + "AND (:endInstant IS NULL OR o.time <= :endInstant)")
    List<Order> findOrders(@Param("userId") Integer userId, @Param("keyword") String keyword,
                           @Param("startInstant") Instant startInstant, @Param("endInstant") Instant endInstant);

    List<Order> findByUserIdAndTimeBetween(int userId, Instant startInstant, Instant endInstant, Pageable pageable);

    List<Order> findByTimeBetween(Instant startInstant, Instant endInstant, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE "
            + "((:keyword IS NULL OR :keyword = '') AND (:startInstant IS NULL) AND (:endInstant IS NULL)) "
            + "OR (:keyword IS NOT NULL AND (:keyword = '' OR LOWER(o.receiver) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(o.tel) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(o.address) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR EXISTS (SELECT 1 FROM o.orderItems oi WHERE LOWER(oi.book.title) LIKE LOWER(CONCAT('%', :keyword, '%'))))) "
            + "AND (:startInstant IS NULL OR o.time >= :startInstant) "
            + "AND (:endInstant IS NULL OR o.time <= :endInstant)")
    List<Order> findAllOrders(@Param("keyword") String keyword,
                              @Param("startInstant") Instant startInstant,
                              @Param("endInstant") Instant endInstant);





}
