// DemoEventListeners.java
package com.example.demo.events.consumer;

import com.example.demo.events.OrderCreatedPayload;
import com.example.demo.events.domain.BaseDomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DemoEventListeners {

    private static final Logger log = LoggerFactory.getLogger(DemoEventListeners.class);

    // 示例1：监听所有 BaseDomainEvent（泛型擦除场景），在代码中判断类型字段
    @Async
    @EventListener
    public void onAnyDomainEvent(BaseDomainEvent<?> event) {
        log.info("[AnyListener] received: {}", event);
    }

    // 示例2：强类型监听某种 payload（Spring 会按参数类型匹配）
    @Async
    @EventListener
    public void onOrderCreated(BaseDomainEvent<OrderCreatedPayload> event) {
        var payload = event.getPayload();
        log.info("[OrderCreated] orderId={}, userId={}, amount={}",
                payload.orderId(), payload.userId(), payload.amount());
    }
}
