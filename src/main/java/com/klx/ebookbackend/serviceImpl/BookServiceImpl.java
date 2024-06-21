package com.klx.ebookbackend.serviceImpl;

import com.klx.ebookbackend.dao.BookDao;
import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;

    @Autowired
    public BookServiceImpl(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @Override
    public List<Book> searchBooks(String keyword, int pageIndex, int pageSize) {
        return bookDao.searchBooks(keyword, pageIndex, pageSize);
    }

    @Override
    public Book getBookById(int id) {
        return bookDao.getBookById(id);
    }

    @Override
    public List<Book> getTop10BestSellingBooks() {
        List<Book> topSellingBooks = bookDao.getTopSellingBooks();
        return topSellingBooks.size() > 10 ? topSellingBooks.subList(0, 10) : topSellingBooks;
    }

    @Override
    public int getTotalBooksCount(String keyword) {
        return bookDao.getTotalBooksCount(keyword);
    }
}
