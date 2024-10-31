package com.klx.ebookbackend.dao;

import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.entity.Order;

import java.util.List;

public interface OrderItemDao {
    List<OrderItem> getOrderItemsByOrder(Order order);//一定是非空的
    OrderItem saveOrderItem(OrderItem orderItem);
    void deleteOrderItem(int id);
}