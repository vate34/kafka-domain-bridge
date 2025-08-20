package com.example.demo.events.publisher;

import com.example.demo.events.domain.BaseDomainEvent;
import com.example.demo.events.domain.EventHeaders;
import com.example.demo.events.transport.EventMappers;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 本地事件发布器，用于在应用内部发布事件
 */
@Component
public class LocalEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public LocalEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 发布本地事件
     *
     * @param event 要发布的事件
     */
    public void publishEvent(Object event) {
        // 解包来自 Kafka 的包装器，确保发布的是真正的领域事件类型
        Object toPublish = EventHeaders.unwrap(event);
        applicationEventPublisher.publishEvent(toPublish);
    }
}