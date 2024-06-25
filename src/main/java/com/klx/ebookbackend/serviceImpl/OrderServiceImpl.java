package com.klx.ebookbackend.serviceImpl;

import com.klx.ebookbackend.dao.OrderDao;
import com.klx.ebookbackend.dao.OrderItemDao;
import com.klx.ebookbackend.dao.BookDao;
import com.klx.ebookbackend.daoImpl.OrderItemDaoImpl;
import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.util.*;
import java.time.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDaoImpl orderItemDao;
    @Autowired
    private BookDao bookDao;

    @Override
    public Order placeOrder(Order order) {
        return orderDao.saveOrder(order);
    }

    @Override
    public List<Order> getOrders(Integer userId, String keyword, LocalDate startDate, LocalDate endDate) {
        List<Order> orders = new ArrayList<>();
        try {
            //虽然前端和controller里面是LocalDate但是必须要换成Instant，精确的时间戳！默认为零点
            Instant startInstant = startDate != null ? startDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
            Instant endInstant = endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : null;

            // 打印转换后的时间戳
            System.out.println("Start Instant: " + startInstant);
            System.out.println("End Instant: " + endInstant);


            List<Order> fetchedOrders = orderDao.findOrders(userId, keyword, startInstant, endInstant);
            // 打印查询结果
            System.out.println("Fetched Orders: " + fetchedOrders);

            for (Order order : fetchedOrders) {
                List<OrderItem> orderItems = orderItemDao.getOrderItemsByOrder(order);
                for (OrderItem orderItem : orderItems) {
                    //给每一个OrderItem都设置book
                    Book book = bookDao.getBookById(orderItem.getBook().getId());
                    orderItem.setBook(book);
                }
                order.setOrderItems(new LinkedHashSet<>(orderItems));
                orders.add(order);
            }
        } catch (Exception e) {
            System.err.println("Error fetching orders for user ID: " + userId + " - " + e.getMessage());
        }
        return orders;
    }
    @Override
    public List<Order> getAllOrders(String keyword, LocalDate startDate, LocalDate endDate) {
        List<Order> orders = new ArrayList<>();
        try {
//            System.out.println("Keyword: " + keyword);
//            System.out.println("Start Date: " + startDate);
//            System.out.println("End Date: " + endDate);

            //虽然前端和controller里面是LocalDate但是必须要换成Instant，精确的时间戳！默认为零点
            Instant startInstant = startDate != null ? startDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
            Instant endInstant = endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : null;

//            System.out.println("Start Instant: " + startInstant);
//            System.out.println("End Instant: " + endInstant);

            List<Order> fetchedOrders = orderDao.findAllOrders(keyword, startInstant, endInstant);

//            System.out.println("Fetched Orders: " + fetchedOrders);

            for (Order order : fetchedOrders) {
                List<OrderItem> orderItems = orderItemDao.getOrderItemsByOrder(order);
                for (OrderItem orderItem : orderItems) {
                    Book book = bookDao.getBookById(orderItem.getBook().getId());
                    orderItem.setBook(book);
                }
                order.setOrderItems(new LinkedHashSet<>(orderItems));
                orders.add(order);
            }
        } catch (Exception e) {
            System.err.println("Error fetching all orders: " + e.getMessage());
        }
        return orders;
    }



}
