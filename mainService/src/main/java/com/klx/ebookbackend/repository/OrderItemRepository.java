package com.klx.ebookbackend.repository;

import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer>{

    List<OrderItem> findByOrder(Order order);

    <S extends OrderItem> S save(S entity);

    @Override
    void deleteById(Integer integer);
}
