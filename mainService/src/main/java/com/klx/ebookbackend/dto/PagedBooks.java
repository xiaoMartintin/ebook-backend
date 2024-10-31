package com.klx.ebookbackend.dto;

import com.klx.ebookbackend.entity.Book;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagedBooks {
    private List<Book> items;
    private int total;

    public PagedBooks(List<Book> items, int total) {
        this.items = items;
        this.total = total;
    }
}