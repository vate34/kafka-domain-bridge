package com.example.demo.events.producer;

import com.example.demo.events.domain.EventHeaders;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 这个发布器强制为本地事件发布器
 */
@Component
public class NativeApplicationEventPublisher implements ApplicationEventPublisher {

    private final ApplicationContext applicationContext;

    public NativeApplicationEventPublisher(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void publishEvent(Object event) {
        // 解包来自 Kafka 的包装器，确保发布的是真正的领域事件类型，避免被 Spring 包成 PayloadApplicationEvent
        Object toPublish = EventHeaders.unwrap(event);
        applicationContext.publishEvent(toPublish);
    }
}
