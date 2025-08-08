// DemoController.java
package com.example.demo.web;

import com.example.demo.events.OrderCreatedPayload;
import com.example.demo.events.domain.BaseDomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class DemoController {

    private final ApplicationEventPublisher eventPublisher;

    public DemoController(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostMapping
    public String createOrder(@RequestParam String userId,
                              @RequestParam BigDecimal amount) {
        String orderId = UUID.randomUUID().toString();

        // 构建自定义业务负载
        OrderCreatedPayload payload = new OrderCreatedPayload(orderId, userId, amount);

        // attributes 可装入任意扩展信息（如渠道、IP、灰度标识等）
        var event = BaseDomainEvent.builder().type("OrderCreated")
                .correlationId(UUID.randomUUID().toString())
                .attributes(Map.of(
                        "channel", "APP",
                        "ip", "192.168.0.1",
                        "featureFlag", true
                ))
                .payload(payload)
                .build();

        eventPublisher.publishEvent(event);
        return "Order created: " + orderId;
    }
}
