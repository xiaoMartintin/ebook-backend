package com.klx.ebookbackend.serviceImpl;

import com.klx.ebookbackend.dao.OrderDao;
import com.klx.ebookbackend.dao.OrderItemDao;
import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;

    @Override
    public Map<String, Object> getUserStatistics(Integer userId, Instant startDate, Instant endDate) {
        List<Order> orders = orderDao.findOrders(userId, null, startDate, endDate);

        Map<String, Object> statistics = new HashMap<>();
        int totalBooks = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                Instant purchaseTime = item.getOrder().getTime();
                if ((startDate == null || !purchaseTime.isBefore(startDate)) && (endDate == null || !purchaseTime.isAfter(endDate))) {
                    String bookTitle = item.getBook().getTitle();
                    Double bookPrice = item.getBook().getPrice();
                    int quantity = item.getQuantity();
                    BigDecimal price = BigDecimal.valueOf(bookPrice).multiply(BigDecimal.valueOf(quantity));

                    totalBooks += quantity;
                    totalAmount = totalAmount.add(price);

                    Map<String, Object> bookStats = new HashMap<>();
                    bookStats.put("title", bookTitle);
                    bookStats.put("quantity", quantity);
                    bookStats.put("price", bookPrice);
                    bookStats.put("totalPrice", price.setScale(2, RoundingMode.HALF_UP).doubleValue());
                    bookStats.put("purchaseDate", purchaseTime);

                    statistics.put(bookTitle + "_" + purchaseTime, bookStats);
                }
            }
        }

        statistics.put("totalBooks", totalBooks);
        statistics.put("totalAmount", totalAmount.setScale(2, RoundingMode.HALF_UP).doubleValue());

        return statistics;
    }
}
