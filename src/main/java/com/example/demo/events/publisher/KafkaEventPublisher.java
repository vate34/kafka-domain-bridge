package com.example.demo.events.publisher;

import com.example.demo.events.domain.BaseDomainEvent;
import com.example.demo.events.domain.EventHeaders;
import com.example.demo.events.transport.EventMappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * Kafka事件发布器，用于将事件发布到Kafka
 */
@Component
public class KafkaEventPublisher {

    public static final String HEADER_SOURCE = "x-source";
    public static final String SOURCE_KAFKA = "kafka";
    public static final String SOURCE_APP = "app";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public KafkaEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.topics.order-events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    /**
     * 将事件发布到Kafka
     *
     * @param event 要发布的事件
     */
    public void publishEvent(Object event) {
        // 如果事件来自Kafka，则不重复发布
        if (EventHeaders.isFromKafka(event)) {
            return;
        }

        Object payloadToSend = event;
        // 如果是领域事件，转换为传输消息
        if (event instanceof BaseDomainEvent<?> domainEvent) {
            payloadToSend = EventMappers.toMessage(domainEvent);
        }

        // 构建消息并发送到Kafka
        var msg = MessageBuilder.withPayload(payloadToSend)
                .setHeader(HEADER_SOURCE, SOURCE_APP)
                .setHeader(org.springframework.kafka.support.KafkaHeaders.TOPIC, topic)
                .build();

        kafkaTemplate.send(msg);
    }
}