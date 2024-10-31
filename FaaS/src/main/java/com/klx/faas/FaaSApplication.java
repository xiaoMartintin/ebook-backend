package com.klx.faas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SpringBootApplication
public class FaaSApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaaSApplication.class, args);
    }

    // 批量计算总价函数
    @Bean
    public Function<Flux<List<Map<String, Object>>>, Flux<List<Double>>> calculateTotalPrice() {
        return ordersFlux -> ordersFlux.map(orders ->
                orders.stream()
                        .map(order -> {
                            double price = (double) order.get("price");
                            int quantity = (int) order.get("quantity");
                            return price * quantity;
                        })
                        .toList()
        );
    }
}
