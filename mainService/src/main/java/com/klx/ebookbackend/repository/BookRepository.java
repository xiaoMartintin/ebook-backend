package com.klx.ebookbackend.repository;

import com.klx.ebookbackend.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.author LIKE %:keyword%")
    Page<Book> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.title LIKE %:keyword% OR b.author LIKE %:keyword%")
    long countByKeyword(@Param("keyword") String keyword);

    @Override
    List<Book> findAll();

    @Override
    <S extends Book> S save(S entity);
   // 在 JPA 中，save 方法可以用于保存新实体或更新现有实体。//
    // 如果传入的实体对象在数据库中已经存在（即其 id 字段对应于数据库中已有记录的主键），则 save 方法会更新该记录。
    // 如果传入的实体对象在数据库中不存在（即其 id 字段为 null 或者对应的记录在数据库中不存在），则 save 方法会插入一条新记录。

    @Override
    void deleteById(Integer integer);

    @Override
    Optional<Book> findById(Integer integer);

    @Query("SELECT b.id, b.title, b.author, b.description, b.price, b.cover, SUM(oi.quantity) as sales " +
            "FROM OrderItem oi JOIN oi.book b " +
            "GROUP BY b.id, b.title, b.author, b.description, b.price, b.cover " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingBooks();
}