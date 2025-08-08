package com.example.demo.events.producer;

import com.example.demo.events.domain.BaseDomainEvent;
import com.example.demo.events.domain.EventHeaders;
import com.example.demo.events.transport.EventMappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * KafkaBackedApplicationEventPublisher 是一个基于 Kafka 的 Spring 应用事件发布器。
 * <p>
 * 此类实现了 ApplicationEventPublisher 接口，通过 Kafka 发送事件。事件可以是简单的对象，也可以是
 * BaseDomainEvent 的实例。对于 BaseDomainEvent 类型的事件，将使用 EventMappers 进行转换。
 * 如果事件已经标记为来自 Kafka（由 EventHeaders.isFromKafka 方法判断），则不会重复发布。
 * <p>
 * 构造函数接收 KafkaTemplate 和目标 Topic 的配置参数，用于发送事件至指定的 Kafka Topic。
 * <p>
 * 常量 HEADER_SOURCE 定义了事件头中表示来源的键名；值 SOURCE_APP 表示事件来自应用自身，而 SOURCE_KAFKA
 * 表示事件来自 Kafka。
 * <p>
 * 方法：
 * - publishEvent(Object event): 发布应用事件。检查事件来源，如果非 Kafka 来源则封装并发送到 Kafka 目标 Topic。
 */
@Component
@Primary
public class KafkaBackedApplicationEventPublisher implements ApplicationEventPublisher {

    public static final String HEADER_SOURCE = "x-source";
    public static final String SOURCE_KAFKA = "kafka";
    public static final String SOURCE_APP = "app";
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public KafkaBackedApplicationEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.topics.order-events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publishEvent(Object event) {
        if (EventHeaders.isFromKafka(event)) {
            return;
        }

        Object payloadToSend = event;
        if (event instanceof BaseDomainEvent<?> domainEvent) {
            payloadToSend = EventMappers.toMessage(domainEvent);
        }

        var msg = MessageBuilder.withPayload(payloadToSend)
                .setHeader(HEADER_SOURCE, SOURCE_APP)
                .setHeader(org.springframework.kafka.support.KafkaHeaders.TOPIC, topic)
                .build();

        kafkaTemplate.send(msg);
    }
}
