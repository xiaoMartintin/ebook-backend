package com.klx.ebookbackend.repository;

import com.klx.ebookbackend.entity.MongoBook;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoBookRepository extends MongoRepository<MongoBook, String> {
    // 自定义查询方法
    MongoBook findByBookId(Integer bookId);
}