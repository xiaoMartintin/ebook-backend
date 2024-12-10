package com.klx.gateway;

import reactor.core.publisher.Mono;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("microservice_route", r -> r.path("/api/microservice/**")
                        .filters(f -> f.rewritePath("/api/microservice/(?<remaining>.*)", "/${remaining}"))
                        .uri("lb://MicroService"))
                .route("ebookbackend_route", r -> r.path("/**")
                        .filters(f -> f)
                        .uri("lb://EbookBackend"))
                .route("websocket_route", r -> r.path("/ws/**")
                        .uri("lb:ws://localhost:8082"))
                .build();
    }


    @RequestMapping("/fallback")
    public Mono<String> fallback() {
        return Mono.just("Service is unavailable, please try again later.");
    }
}
