package com.zing.springcloudalibaba.controller;

import com.zing.springcloudalibaba.domain.Order;
import com.zing.springcloudalibaba.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Zing
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/create")
    public Order create(@RequestParam Long productId) {
        return orderService.createOrder(productId, 10);
    }

}
