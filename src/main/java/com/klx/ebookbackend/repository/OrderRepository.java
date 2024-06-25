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
            + "AND (:keyword IS NULL OR o.receiver LIKE %:keyword% OR o.tel LIKE %:keyword% OR o.address LIKE %:keyword% OR EXISTS (SELECT 1 FROM o.orderItems oi WHERE oi.book.title LIKE %:keyword%)) "
            + "AND (:startInstant IS NULL OR o.time >= :startInstant) "
            + "AND (:endInstant IS NULL OR o.time <= :endInstant)")
    List<Order> findOrders(@Param("userId") Integer userId, @Param("keyword") String keyword,
                           @Param("startInstant") Instant startInstant, @Param("endInstant") Instant endInstant);//给order用的

    @Query("SELECT o FROM Order o WHERE "
            + "((:keyword IS NULL OR :keyword = '') AND (:startInstant IS NULL) AND (:endInstant IS NULL)) "  // 当所有参数为空时，确保返回true
            + "OR (:keyword IS NOT NULL AND (:keyword = '' OR LOWER(o.receiver) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(o.tel) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(o.address) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR EXISTS (SELECT 1 FROM o.orderItems oi WHERE LOWER(oi.book.title) LIKE LOWER(CONCAT('%', :keyword, '%'))))) "
            + "AND (:startInstant IS NULL OR o.time >= :startInstant) "
            + "AND (:endInstant IS NULL OR o.time <= :endInstant)")
    List<Order> findAllOrders(@Param("keyword") String keyword,
                              @Param("startInstant") Instant startInstant,
                              @Param("endInstant") Instant endInstant);//给admin order用的，确保在所有参数都为空时，能够返回所有订单。

//    @Query("SELECT o FROM Order o WHERE "
//            + "(:keyword IS NULL OR LOWER(o.receiver) LIKE LOWER(CONCAT('%', :keyword, '%')) "
//            + "OR LOWER(o.tel) LIKE LOWER(CONCAT('%', :keyword, '%')) "
//            + "OR LOWER(o.address) LIKE LOWER(CONCAT('%', :keyword, '%')) "
//            + "OR EXISTS (SELECT 1 FROM o.orderItems oi WHERE LOWER(oi.book.title) LIKE LOWER(CONCAT('%', :keyword, '%')))) "
//            + "AND (:startInstant IS NULL OR o.time >= :startInstant) "
//            + "AND (:endInstant IS NULL OR o.time <= :endInstant)")
//    List<Order> findAllOrders(@Param("keyword") String keyword,
//                              @Param("startInstant") Instant startInstant,
//                              @Param("endInstant") Instant endInstant);
//    ;


    List<Order> findByUserIdAndTimeBetween(Integer userId, Instant startInstant, Instant endInstant);//给statistics用的

    @Override
    void deleteById(Integer integer);

    @Override
    <S extends Order> S save(S entity);
}
