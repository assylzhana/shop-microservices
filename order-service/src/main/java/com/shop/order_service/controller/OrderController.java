package com.shop.order_service.controller;

import com.shop.order_service.dto.OrderRequest;
import com.shop.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
            orderService.placeOrder(orderRequest);
            return "Order placed successfully";
    }

    public String fallbackMethod(OrderRequest orderRequest, Throwable throwable) {
            if (throwable instanceof IllegalArgumentException) {
                return "Product is not in stock, please try again later";
            } else if (throwable instanceof IllegalStateException) {
                return "Inventory service is unavailable, please try again later";
            } else {
                return "Oops! Something went wrong, please try again later";
            }
    }
}
