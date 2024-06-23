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

import java.util.*;

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
    public List<Order> getOrders(Integer userId) {
        List<Order> orders = new ArrayList<>();
        try {
            List<Order> fetchedOrders = orderDao.getOrdersByUserId(userId);
            for (Order order : fetchedOrders) {
                List<OrderItem> orderItems = orderItemDao.getOrderItemsByOrder(order);
                for (OrderItem orderItem : orderItems) {
                    //给每一个OrderItem都设置book
                    Book book = bookDao.getBookById(orderItem.getBook().getId());
                    //打印一下book
                    orderItem.setBook(book);
                }
                order.setOrderItems(new LinkedHashSet<>(orderItems));
                orders.add(order);
            }

            //debug
            for (Order order : fetchedOrders) {
                Set<OrderItem> orderItems = order.getOrderItems();
                for (OrderItem orderItem : orderItems) {
                    System.out.println("book："+orderItem.getBook().getTitle());
                }
            }


        } catch (Exception e) {
            System.err.println("Error fetching orders for user ID: " + userId + " - " + e.getMessage());
        }
        return orders;
    }

}
