package com.klx.ebookbackend.daoImpl;

import com.klx.ebookbackend.dao.OrderItemDao;
import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

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

//    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
//    public OrderItem saveOrderItem(OrderItem orderItem) {
//        System.out.println("开始保存订单项: " + orderItem);
//        try {
//            if (orderItem.getQuantity() > 10) {
//                throw new RuntimeException("订单项数量超过限制，抛出异常！");
//            }
//            OrderItem savedItem = orderItemRepository.save(orderItem);
//            System.out.println("订单项保存成功: " + savedItem);
//            return savedItem;
//        } catch (Exception e) {
//            System.out.println("订单项保存失败，发生异常: " + e.getMessage());
//            throw e; // 重新抛出异常以触发事务回滚
//        }
//    }


    @Override
    public void deleteOrderItem(int id){
        orderItemRepository.deleteById(id);
    }
}
