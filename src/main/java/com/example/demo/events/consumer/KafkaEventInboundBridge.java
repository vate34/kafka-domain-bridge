package com.example.demo.events.consumer;

import com.example.demo.events.domain.DomainEventMessage;
import com.example.demo.events.domain.EventHeaders;
import com.example.demo.events.publisher.LocalEventPublisher;
import com.example.demo.events.transport.EventMappers;
import com.example.demo.events.versioning.EventVersionManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 消息接收桥接器，用于将Kafka的事件消息重新发布为本地事件
 */
@Component
public class KafkaEventInboundBridge {

    private final LocalEventPublisher localEventPublisher;
    private final EventVersionManager eventVersionManager;

    public KafkaEventInboundBridge(LocalEventPublisher localEventPublisher,
                                   EventVersionManager eventVersionManager) {
        this.localEventPublisher = localEventPublisher;
        this.eventVersionManager = eventVersionManager;
    }

    @KafkaListener(topics = "${app.topics.order-events}")
    public void onMessage(@Payload DomainEventMessage message) {
        // 使用事件版本管理器转换事件
        var domainEvent = EventMappers.toDomainEvent(message, eventVersionManager);
        Object wrapped = EventHeaders.markFromKafka(domainEvent);
        localEventPublisher.publishEvent(wrapped);
    }
}
