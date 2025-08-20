package com.example.demo.events.consumer;

import com.example.demo.events.OrderCreatedPayload;
import com.example.demo.events.domain.BaseDomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 增强的事件监听器示例，展示新架构的功能
 */
@Component
public class EnhancedEventListeners {

    private static final Logger log = LoggerFactory.getLogger(EnhancedEventListeners.class);

    /**
     * 监听所有BaseDomainEvent事件
     *
     * @param event 事件对象
     */
    @Async
    @EventListener
    public void onAnyDomainEvent(BaseDomainEvent<?> event) {
        log.info("[AnyListener] received event: type={}, id={}", event.getType(), event.getId());
    }

    /**
     * 强类型监听OrderCreated事件
     *
     * @param event OrderCreated事件
     */
    @Async
    @EventListener
    public void onOrderCreated(BaseDomainEvent<OrderCreatedPayload> event) {
        var payload = event.getPayload();
        log.info("[OrderCreated] orderId={}, userId={}, amount={}",
                payload.orderId(), payload.userId(), payload.amount());
    }

    /**
     * 监听特定属性的事件
     *
     * @param event 事件对象
     */
    @Async
    @EventListener
    public void onHighValueOrder(BaseDomainEvent<OrderCreatedPayload> event) {
        var payload = event.getPayload();
        // 监听高价值订单（金额大于1000）
        if (payload.amount().compareTo(java.math.BigDecimal.valueOf(1000)) > 0) {
            log.info("[HighValueOrder] High value order detected: orderId={}, amount={}",
                    payload.orderId(), payload.amount());
        }
    }
}