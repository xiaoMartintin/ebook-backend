package com.klx.ebookbackend.dao;

import com.klx.ebookbackend.entity.Book;

import java.util.List;

public interface BookDao {
    List<Book> getAllBooks();
    Book getBookById(int id);
    Book saveBook(Book book);
    void deleteBook(int id);
    List<Book> searchBooks(String keyword, int pageIndex, int pageSize);
    int getTotalBooksCount(String keyword);
    List<Book> getTopSellingBooks();
}
