package com.klx.ebookbackend.daoImpl;

import com.klx.ebookbackend.dao.OrderDao;
import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.Instant;
import java.util.*;

@Repository
public class OrderDaoImpl implements OrderDao {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order saveOrder(Order order){
        return orderRepository.save(order);
    }
//
//    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
//    public Order saveOrder(Order order) {
//        System.out.println("开始保存订单: " + order);
//        try {
//            if (order.getTotalPrice() > 100) {
//                throw new RuntimeException("订单金额超过限制，抛出异常！");
//            }
//            Order savedOrder = orderRepository.save(order);
//            System.out.println("订单保存成功: " + savedOrder);
//            return savedOrder;
//        } catch (Exception e) {
//            System.out.println("订单保存失败，发生异常: " + e.getMessage());
//            throw e; // 重新抛出异常以触发事务回滚
//        }
//    }

    @Override
    public void deleteOrder(int id){
        orderRepository.deleteById(id);
    }

    @Override
    public Optional<Order> getOrderById(int id){
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Page<Order> findOrders(Integer userId, String keyword, Instant startInstant, Instant endInstant, Pageable pageable) {
        return orderRepository.findOrders(userId, keyword, startInstant, endInstant, pageable);
    }

    @Override
    public List<Order> getOrdersByUserIdAndTimeBetween(int userId, Instant startInstant, Instant endInstant) {
        return orderRepository.findByUserIdAndTimeBetween(userId, startInstant, endInstant);
    }

    @Override
    public List<Order> getOrdersByTimeBetween(Instant startInstant, Instant endInstant) {
        return orderRepository.findByTimeBetween(startInstant, endInstant);
    }

    @Override
    public Page<Order> findAllOrders(String keyword, Instant startInstant, Instant endInstant, Pageable pageable) {
        return orderRepository.findAllOrders(keyword, startInstant, endInstant, pageable);
    }
}
