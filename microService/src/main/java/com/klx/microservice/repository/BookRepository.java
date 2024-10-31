package com.klx.microservice.repository;

import com.klx.microservice.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    // 模糊查找书名包含指定关键字的书籍
    List<Book> findByTitleContaining(String title);
}
