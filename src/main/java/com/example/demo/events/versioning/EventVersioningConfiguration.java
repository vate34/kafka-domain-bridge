package com.example.demo.events.versioning;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 事件版本管理配置类
 */
@Configuration
public class EventVersioningConfiguration {
    
    /**
     * OrderCreated事件版本转换器Bean
     *
     * @return OrderCreatedEventVersionConverter实例
     */
    @Bean
    public OrderCreatedEventVersionConverter orderCreatedEventVersionConverter() {
        return new OrderCreatedEventVersionConverter();
    }
    
    /**
     * 注册OrderCreated事件版本转换器
     *
     * @param eventVersionManager 事件版本管理器
     * @param converter           OrderCreated事件版本转换器
     */
    public void registerOrderCreatedConverter(EventVersionManager eventVersionManager,
                                             OrderCreatedEventVersionConverter converter) {
        eventVersionManager.registerVersionConverter("OrderCreated", converter);
    }
}