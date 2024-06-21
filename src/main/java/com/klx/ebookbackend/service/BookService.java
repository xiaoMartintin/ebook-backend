package com.klx.ebookbackend.service;

import com.klx.ebookbackend.entity.Book;

import java.util.List;

public interface BookService {
    List<Book> searchBooks(String keyword, int pageIndex, int pageSize);
    Book getBookById(int id);
    List<Book> getTop10BestSellingBooks();
    int getTotalBooksCount(String keyword); // 新增方法
}
