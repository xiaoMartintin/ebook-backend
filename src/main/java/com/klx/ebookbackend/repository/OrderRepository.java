package com.klx.ebookbackend.repository;

import com.klx.ebookbackend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(Integer userId);

    @Query("SELECT o FROM Order o WHERE "
            + "(:userId IS NULL OR o.user.id = :userId) "
            + "AND (:keyword IS NULL OR o.receiver LIKE %:keyword% OR o.tel LIKE %:keyword% OR o.address LIKE %:keyword% OR o.address LIKE %:keyword% OR EXISTS (SELECT 1 FROM o.orderItems oi WHERE oi.book.title LIKE %:keyword%)) "
            + "AND (:startDate IS NULL OR o.time >= :startDate) "
            + "AND (:endDate IS NULL OR o.time <= :endDate)")
    List<Order> findOrders(@Param("userId") Integer userId, @Param("keyword") String keyword,
                           @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);//给order用的

    List<Order> findByUserIdAndTimeBetween(Integer userId, Instant startDate, Instant endDate);//给statistics用的

    @Override
    void deleteById(Integer integer);

    @Override
    <S extends Order> S save(S entity);
}
