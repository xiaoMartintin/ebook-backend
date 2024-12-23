package com.klx.ebookbackend.controller;

import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.entity.Cart;
import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.entity.User;
import com.klx.ebookbackend.dto.OrderInfo;
import com.klx.ebookbackend.service.OrderService;
import com.klx.ebookbackend.service.CartService;
import com.klx.ebookbackend.service.UserService;
import com.klx.ebookbackend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private CartService cartService;

    @PostMapping("/order")
    public ResponseEntity<?> placeOrder(@RequestBody OrderInfo orderInfo, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse("User not logged in", false, null));
        }

        // Debugging output
        System.out.println("OrderInfo received: " + orderInfo);

        String address = orderInfo.getAddress();
        String receiver = orderInfo.getReceiver();
        String tel = orderInfo.getTel();
        List<Integer> itemIds = orderInfo.getItemIds();

        if (address == null || receiver == null || tel == null || itemIds == null || itemIds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponse("Invalid order info", false, null));
        }

        Order order = new Order();
        order.setAddress(address);
        order.setReceiver(receiver);
        order.setTel(tel);
        order.setTime(Instant.now());
        User user = userService.getUserById(userId).orElse(null);
        order.setUser(user);

        Set<OrderItem> orderItems = new LinkedHashSet<>();
        double totalOrderPrice = 0.0;

        for (Integer itemId : itemIds) {
            try {
                OrderItem orderItem = new OrderItem();
                Optional<Cart> cartItem = cartService.getCartById(itemId);

                //orderInfo里面的itemId实际上是Cart的id所以必须从Cart里面拿数据，把一个cart映射到一个order_item
                if (cartItem.isPresent()) {
                    orderItem.setOrder(order);
                    orderItem.setBook(cartItem.get().getBook());
                    orderItem.setQuantity(cartItem.get().getQuantity());
                    totalOrderPrice += cartItem.get().getQuantity() * cartItem.get().getBook().getPrice();
                    orderItems.add(orderItem);
                } else {
                    System.out.println("Cart item not found for id: " + itemId);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponse("Invalid cart item ID: " + itemId, false, null));
                }
            } catch (Exception e) {
                System.out.println("Error processing cart item with id: " + itemId);
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createResponse("Error processing cart item with id: " + itemId, false, null));
            }
        }

        // Check if user has enough balance
        if (user.getBalance() < totalOrderPrice) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponse("Insufficient balance", false, null));
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalOrderPrice);

        try {
            orderService.placeOrder(order);
        } catch (Exception e) {
            System.out.println("Error placing order");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createResponse("Error placing order", false, null));
        }

        return ResponseEntity.ok(createResponse("Order successfully placed", true, null));
    }



    @GetMapping("/order")
    public ResponseEntity<?> getOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {

        System.out.println("keyword: " + keyword);
        System.out.println("startDate: " + startDate);
        System.out.println("endDate: " + endDate);

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse("User not logged in", false, null));
        }

        Page<Order> orderPage = orderService.getOrders(userId, keyword, startDate, endDate, PageRequest.of(pageIndex, pageSize));
        System.out.println(orderPage.getContent());

        if (orderPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(createResponse("No orders found", true, orderPage.getContent()));
        }

        return ResponseEntity.ok(createResponse("Orders retrieved", true, orderPage));
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {

        System.out.println("Keyword: " + keyword);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null || !userService.isUserAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createResponse("Access denied", false, null));
        }

        Page<Order> orderPage = orderService.getAllOrders(keyword, startDate, endDate, PageRequest.of(pageIndex, pageSize));

        System.out.println("Orders: " + orderPage.getContent());

        if (orderPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(createResponse("No orders found", true, orderPage.getContent()));
        }

        return ResponseEntity.ok(createResponse("Orders retrieved", true, orderPage));
    }






    private Map<String, Object> createResponse(String message, boolean ok, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("ok", ok);
        response.put("data", data);
        return response;
    }


}
