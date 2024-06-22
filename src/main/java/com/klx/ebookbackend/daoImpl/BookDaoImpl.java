package com.klx.ebookbackend.daoImpl;

import com.klx.ebookbackend.dao.BookDao;
import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BookDaoImpl implements BookDao {
    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(int id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(int id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<Book> searchBooks(String keyword, int pageIndex, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<Book> bookPage = bookRepository.findByKeyword(keyword, pageRequest);
        return bookPage.getContent();
    }

    @Override
    public List<Book> getTopSellingBooks() {
        List<Object[]> results = bookRepository.findTopSellingBooks();
        return results.stream().map(result -> {
            Book book = new Book();
            book.setId((Integer) result[0]);
            book.setTitle((String) result[1]);
            book.setAuthor((String) result[2]);
            book.setDescription((String) result[3]);
            book.setPrice((Double) result[4]);
            book.setCover((String) result[5]);
            book.setSales(((Number) result[6]).intValue());
            return book;
        }).collect(Collectors.toList());
    }

    @Override
    public int getTotalBooksCount(String keyword) {
        return (int) bookRepository.countByKeyword(keyword);
    }
}
