package com.example.demo.events.publisher;

import org.springframework.stereotype.Service;

/**
 * 统一事件发布服务，协调本地事件发布和Kafka事件发布
 */
@Service
public class EventPublishingService {

    private final LocalEventPublisher localEventPublisher;
    private final KafkaEventPublisher kafkaEventPublisher;

    public EventPublishingService(LocalEventPublisher localEventPublisher,
                                  KafkaEventPublisher kafkaEventPublisher) {
        this.localEventPublisher = localEventPublisher;
        this.kafkaEventPublisher = kafkaEventPublisher;
    }

    /**
     * 发布事件到本地和Kafka
     *
     * @param event 要发布的事件
     */
    public void publishEvent(Object event) {
        // 首先发布到本地，确保本地监听器能接收到事件
        localEventPublisher.publishEvent(event);

        // 然后发布到Kafka，供其他服务消费
        kafkaEventPublisher.publishEvent(event);
    }

    /**
     * 仅发布事件到本地
     *
     * @param event 要发布的事件
     */
    public void publishLocalEvent(Object event) {
        localEventPublisher.publishEvent(event);
    }

    /**
     * 仅发布事件到Kafka
     *
     * @param event 要发布的事件
     */
    public void publishKafkaEvent(Object event) {
        kafkaEventPublisher.publishEvent(event);
    }
}