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
        // 判断是否是标签搜索
        if (bookDao.isTag(keyword)) {
            return bookDao.findBooksByTagRelation(keyword);
        } else {
            return bookDao.searchBooks(keyword, pageIndex, pageSize);
        }
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

    @Override
    public Book saveBook(Book book) {
        return bookDao.saveBook(book);
    }

    @Override
    public void deleteBook(int id) {
        bookDao.deleteBook(id);
    }
}
