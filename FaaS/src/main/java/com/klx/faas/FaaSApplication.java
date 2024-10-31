package com.klx.faas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.Map;
import java.util.function.Function;

@SpringBootApplication
public class FaaSApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaaSApplication.class, args);
    }

    // 单次请求计算总价的函数
    @Bean
    public Function<Map<String, Object>, Double> calculateTotalPrice() {
        return order -> {
            double price = (double) order.get("price");
            int quantity = (int) order.get("quantity");
            return price * quantity;
        };
    }
}
