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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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


    private final WebClient webClient = WebClient.create("http://localhost:8084");


    @Override
    public Order placeOrder(Order order) {
        return orderDao.saveOrder(order);
    }

    /**
     * 处理订单的创建、校验、保存逻辑。
     */
    @Override
    @Transactional
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
        List<Map<String, Object>> bookOrders = new ArrayList<>();

        // 构建订单项并收集请求数据
        for (Integer itemId : itemIds) {
            Cart cartItem = cartService.getCartById(itemId).orElseThrow(() ->
                    new RuntimeException("Cart item not found for ID: " + itemId));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);

            // 构建 FaaS 请求的 Map 并添加到请求列表
            Map<String, Object> bookOrder = new HashMap<>();
            bookOrder.put("price", cartItem.getBook().getPrice());
            bookOrder.put("quantity", cartItem.getQuantity());
            bookOrders.add(bookOrder);
        }

        // 发送批量请求到 FaaS 服务并累加总价
        Double totalOrderPrice = webClient.post()
                .uri("/calculateTotalPrice")
                .bodyValue(bookOrders)
                .retrieve()
                .bodyToFlux(Double.class)
                .reduce(0.0, Double::sum)
                .block();

        // 检查用户余额是否足够
        if (user.getBalance() < totalOrderPrice) {
            throw new RuntimeException("余额不足。当前余额：" + user.getBalance() + "，订单总价：" + totalOrderPrice);
        }

        // 扣除用户余额并设置订单信息
        user.setBalance(user.getBalance() - totalOrderPrice);
        order.setOrderItems(orderItems);
        order.setTotalPrice(totalOrderPrice);

        // 保存订单
        try {
            orderDao.saveOrder(order);
        } catch (RuntimeException e) {
            throw e;
        }

        // 保存订单项
        for (OrderItem orderItem : orderItems) {
            try {
                orderItemDao.saveOrderItem(orderItem);
            } catch (RuntimeException e) {
                System.out.println("保存订单项时发生异常：" + e.getMessage());
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
