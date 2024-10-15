package com.klx.ebookbackend.serviceImpl;
import com.klx.ebookbackend.dao.OrderDao;
import com.klx.ebookbackend.dao.OrderItemDao;
import com.klx.ebookbackend.dao.BookDao;
import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.entity.Cart;
import com.klx.ebookbackend.entity.User;
import com.klx.ebookbackend.service.OrderService;
import com.klx.ebookbackend.service.CartService;
import com.klx.ebookbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private BookDao bookDao;
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;

    @Override
    public Order placeOrder(Order order) {
        return orderDao.saveOrder(order);
    }

    /**
     * 处理订单的创建、校验、保存逻辑。
     */
    @Override
    @Transactional // 保持整个订单处理的事务性
    public Order processOrder(Integer userId, String address, String receiver, String tel, List<Integer> itemIds) {
        User user = userService.getUserById(userId).orElseThrow(() ->
                new RuntimeException("User not found with ID: " + userId));

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setReceiver(receiver);
        order.setTel(tel);
        order.setTime(Instant.now());

        Set<OrderItem> orderItems = new LinkedHashSet<>();
        double totalOrderPrice = 0.0;

        // 首先计算总价，创建订单项（不保存）
        for (Integer itemId : itemIds) {
            Cart cartItem = cartService.getCartById(itemId).orElseThrow(() ->
                    new RuntimeException("Cart item not found for ID: " + itemId));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order); // 此时 order 尚未持久化
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            totalOrderPrice += cartItem.getQuantity() * cartItem.getBook().getPrice();

            orderItems.add(orderItem);
        }

        // 检查用户余额是否足够
        if (user.getBalance() < totalOrderPrice) {
            throw new RuntimeException("余额不足。当前余额：" + user.getBalance() + "，订单总价：" + totalOrderPrice);
        }

        // 扣除用户余额并设置订单信息
        user.setBalance(user.getBalance() - totalOrderPrice);
        order.setOrderItems(orderItems);
        order.setTotalPrice(totalOrderPrice);

        // 先保存订单（此时订单尚未保存，order 为瞬态对象）
        try {
            orderDao.saveOrder(order);
        } catch (RuntimeException e) {
            System.out.println("保存订单时发生异常：" + e.getMessage());
            // 根据业务需求，决定是否需要重新抛出异常或设置事务回滚
            throw e; // 如果需要回滚事务，可以重新抛出异常
        }

        // 保存订单项（此时订单已经持久化，可以正确引用）
        for (OrderItem orderItem : orderItems) {
            try {
                orderItemDao.saveOrderItem(orderItem);
            } catch (RuntimeException e) {
                System.out.println("保存订单项时发生异常：" + e.getMessage());
                // 根据需要处理异常，可能需要记录日志或采取补偿措施
            }
        }

        return order;
    }



    @Override
    public Page<Order> getOrders(Integer userId, String keyword, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {
        Instant startInstant = startDate != null ? startDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
        Instant endInstant = endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : null;

        Page<Order> fetchedOrders = orderDao.findOrders(userId, keyword, startInstant, endInstant, pageRequest);
        System.out.println("fetchedOrders: " + fetchedOrders);

        fetchedOrders.forEach(order -> {
            List<OrderItem> orderItems = orderItemDao.getOrderItemsByOrder(order);
            if (orderItems != null) {
                orderItems.forEach(orderItem -> {
                    if (orderItem.getBook() != null) {
                        Book book = bookDao.getBookById(orderItem.getBook().getId());
                        orderItem.setBook(book);
                    }
                });
                order.setOrderItems(new LinkedHashSet<>(orderItems));
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
            List<OrderItem> orderItems = orderItemDao.getOrderItemsByOrder(order);
            if (orderItems != null) {
                orderItems.forEach(orderItem -> {
                    if (orderItem.getBook() != null) {
                        Book book = bookDao.getBookById(orderItem.getBook().getId());
                        orderItem.setBook(book);
                    }
                });
                order.setOrderItems(new LinkedHashSet<>(orderItems));
            }
        });
        return fetchedOrders;
    }

}
