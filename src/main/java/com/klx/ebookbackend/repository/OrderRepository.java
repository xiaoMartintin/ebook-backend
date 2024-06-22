package com.klx.ebookbackend.repository;

import com.klx.ebookbackend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(Integer userId);

    @Override
    void deleteById(Integer integer);

    @Override
    <S extends Order> S save(S entity);
}