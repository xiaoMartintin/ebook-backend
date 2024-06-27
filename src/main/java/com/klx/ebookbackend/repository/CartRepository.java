package com.klx.ebookbackend.repository;

import com.klx.ebookbackend.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
//    List<Cart> findByUserId(Integer userId);
    Page<Cart> findByUserId(Integer userId, Pageable pageable);

    @Override
    List<Cart> findAll();

    @Override
    Optional<Cart> findById(Integer integer);

    @Override
    <S extends Cart> S save(S entity);

    @Override
    void deleteById(Integer integer);

    @Override
    void deleteAllById(Iterable<? extends Integer> integers);
}
