package com.klx.microservice.repository;

import com.klx.microservice.entity.MongoBook;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoBookRepository extends MongoRepository<MongoBook, String> {
    // 自定义查询方法
    MongoBook findByBookId(Integer bookId);
}