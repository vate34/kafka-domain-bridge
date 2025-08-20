package com.example.demo.events.versioning;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 事件版本管理初始化配置
 */
@Component
public class EventVersioningInitializer {

    private final EventVersionManager eventVersionManager;
    private final OrderCreatedEventVersionConverter orderCreatedEventVersionConverter;

    public EventVersioningInitializer(EventVersionManager eventVersionManager,
                                     OrderCreatedEventVersionConverter orderCreatedEventVersionConverter) {
        this.eventVersionManager = eventVersionManager;
        this.orderCreatedEventVersionConverter = orderCreatedEventVersionConverter;
    }

    /**
     * 在应用上下文刷新时注册事件版本转换器
     */
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 注册OrderCreated事件版本转换器
        eventVersionManager.registerVersionConverter("OrderCreated", orderCreatedEventVersionConverter);
    }
}