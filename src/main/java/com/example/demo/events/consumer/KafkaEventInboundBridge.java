package com.example.demo.events.consumer;

import com.example.demo.events.domain.DomainEventMessage;
import com.example.demo.events.domain.EventHeaders;
import com.example.demo.events.producer.NativeApplicationEventPublisher;
import com.example.demo.events.transport.EventMappers;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 消息接收桥接器，用于将Kafka的事件消息重新发布为本地事件
 */
@Component
public class KafkaEventInboundBridge {

    // 注意这个为native
    private final NativeApplicationEventPublisher nativePublisher;

    public KafkaEventInboundBridge(NativeApplicationEventPublisher nativePublisher) {
        this.nativePublisher = nativePublisher;
    }

    @KafkaListener(topics = "${app.topics.order-events}")
    public void onMessage(@Payload DomainEventMessage message) {
        var domainEvent = EventMappers.toDomainEvent(message);
        Object wrapped = EventHeaders.markFromKafka(domainEvent);
        nativePublisher.publishEvent(wrapped);
    }
}
