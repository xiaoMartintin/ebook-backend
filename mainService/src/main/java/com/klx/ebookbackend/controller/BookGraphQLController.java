package com.klx.ebookbackend.controller;

import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.dto.PagedBooks;
import com.klx.ebookbackend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class BookGraphQLController {

    private final BookService bookService;

    @Autowired
    public BookGraphQLController(BookService bookService) {
        this.bookService = bookService;
        System.out.println("BookGraphQLController loaded!");
    }

    /**
     * GraphQL 查询：通过书名关键字搜索书籍
     *
     * @param keyword   书名关键字
     * @param pageIndex 当前页码
     * @param pageSize  每页大小
     * @return 分页书籍信息
     */
    @QueryMapping
    public PagedBooks searchBooksByName(
            @Argument("keyword") String keyword,
            @Argument int pageIndex,
            @Argument int pageSize) {
//        System.out.println("调用了GraphQL");

        // 调用服务层方法获取书籍列表和总记录数
        List<Book> books = bookService.searchBooks(keyword, pageIndex, pageSize);
        int total = bookService.getTotalBooksCount(keyword);

        // 封装为分页结果
        return new PagedBooks(books, total);
    }
}