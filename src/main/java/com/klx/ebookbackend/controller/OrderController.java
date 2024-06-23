package com.klx.ebookbackend.controller;

import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.entity.Cart;
import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.service.OrderService;
import com.klx.ebookbackend.service.CartService;
import com.klx.ebookbackend.service.UserService;
import com.klx.ebookbackend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private CartService cartService;

    @PostMapping
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
        order.setUser(userService.getUserById(userId).orElse(null));

        Set<OrderItem> orderItems = new LinkedHashSet<>();
        for (Integer itemId : itemIds) {
            try {
                OrderItem orderItem = new OrderItem();
                Optional<Cart> cartItem = cartService.getCartById(itemId);

                //orderInfo里面的itemId实际上是Cart的id所以必须从Cart里面拿数据，把一个cart映射到一个order_item
                if (cartItem.isPresent()) {
                    orderItem.setOrder(order);
                    orderItem.setBook(cartItem.get().getBook());
                    orderItem.setQuantity(cartItem.get().getQuantity());
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

        order.setOrderItems(orderItems);

        try {
            orderService.placeOrder(order);
        } catch (Exception e) {
            System.out.println("Error placing order");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createResponse("Error placing order", false, null));
        }

        return ResponseEntity.ok(createResponse("Order successfully placed", true, null));
    }


    @GetMapping
    public ResponseEntity<?> getOrders(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse("User not logged in", false, null));
        }

        List<Order> orders = orderService.getOrders(userId);

        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(createResponse("No orders found", true, orders));
        }

        return ResponseEntity.ok(createResponse("Orders retrieved", true, orders));
    }


    private Map<String, Object> createResponse(String message, boolean ok, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("ok", ok);
        response.put("data", data);
        return response;
    }

    public static class OrderInfo {
        private String address;
        private String receiver;
        private String tel;
        private List<Integer> itemIds;

        // Getters and Setters

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public List<Integer> getItemIds() {
            return itemIds;
        }

        public void setItemIds(List<Integer> itemIds) {
            this.itemIds = itemIds;
        }
    }
}
