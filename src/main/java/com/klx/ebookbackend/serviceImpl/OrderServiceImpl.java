package com.klx.ebookbackend.serviceImpl;

import com.klx.ebookbackend.dao.OrderDao;
import com.klx.ebookbackend.dao.BookDao;
import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private BookDao bookDao;

    @Override
    public Order placeOrder(Order order) {
        return orderDao.saveOrder(order);
    }

    @Override
    public Page<Order> getOrders(Integer userId, String keyword, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {
        Instant startInstant = startDate != null ? startDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
        Instant endInstant = endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : null;

        Page<Order> fetchedOrders = orderDao.findOrders(userId, keyword, startInstant, endInstant, pageRequest);
        System.out.println("fetchedOrders: " + fetchedOrders);

        fetchedOrders.forEach(order -> {
            if (order.getOrderItems() != null) {
                order.getOrderItems().forEach(orderItem -> {
                    if (orderItem.getBook() != null) {
                        Book book = bookDao.getBookById(orderItem.getBook().getId());
                        orderItem.setBook(book);
                    }
                });
            }
        });
        return fetchedOrders;
    }

    @Override
    public Page<Order> getAllOrders(String keyword, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {
        Instant startInstant = startDate != null ? startDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
        Instant endInstant = endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : null;

        Page<Order> fetchedOrders = orderDao.findAllOrders(keyword, startInstant, endInstant, pageRequest);

        System.out.println("fetchedOrders: " + fetchedOrders);
        fetchedOrders.forEach(order -> {
            if (order.getOrderItems() != null) {
                order.getOrderItems().forEach(orderItem -> {
                    if (orderItem.getBook() != null) {
                        Book book = bookDao.getBookById(orderItem.getBook().getId());
                        orderItem.setBook(book);
                    }
                });
            }
        });
        return fetchedOrders;
    }
}
