package com.klx.ebookbackend.daoImpl;

import com.klx.ebookbackend.dao.OrderItemDao;
import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class OrderItemDaoImpl implements OrderItemDao {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public List<OrderItem> getOrderItemsByOrder(Order order){//一定是非空的
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        return orderItems.isEmpty() ? null : orderItems;
    }

    @Override
    public OrderItem saveOrderItem(OrderItem orderItem){
        return orderItemRepository.save(orderItem);
    }

    @Override
    public void deleteOrderItem(int id){
        orderItemRepository.deleteById(id);
    }
}
