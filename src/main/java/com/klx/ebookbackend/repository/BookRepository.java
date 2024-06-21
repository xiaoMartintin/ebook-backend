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
    Book save(Book book);

    @Override
    void deleteById(Integer integer);

    @Override
    Optional<Book> findById(Integer integer);

    @Query("SELECT b.id, b.title, b.author, b.description, b.price, b.image, SUM(oi.quantity) as sales " +
            "FROM OrderItem oi JOIN oi.book b " +
            "GROUP BY b.id, b.title, b.author, b.description, b.price, b.image " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Book> findTopSellingBooks();


}