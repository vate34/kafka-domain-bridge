package com.example.demo.events.domain;

import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

/**
 * 事件包装器，用于区分是否来自Kafka
 */
public final class EventHeaders {
    private EventHeaders() {
    }

    public static Object markFromKafka(Object event) {
        if (event instanceof WithKafkaSource marker) return marker; // 已标记
        return new WithKafkaSource(event);
    }

    public static boolean isFromKafka(Object event) {
        return event instanceof WithKafkaSource;
    }

    public static Object unwrap(Object event) {
        return (event instanceof WithKafkaSource(Object delegate)) ? delegate : event;
    }

    /**
     * 让包装器实现 ResolvableTypeProvider，把内部真实事件类型暴露给 Spring 事件匹配器
     */
    public record WithKafkaSource(Object delegate) implements ResolvableTypeProvider {
        @Override
        public ResolvableType getResolvableType() {
            if (delegate == null) {
                return ResolvableType.forClass(Object.class);
            }
            // 若内部本身支持 ResolvableTypeProvider（能携带泛型信息），优先使用
            if (delegate instanceof ResolvableTypeProvider rtp) {
                ResolvableType rt = rtp.getResolvableType();
                if (rt != null) return rt;
            }
            // 否则根据实例推断类型（能尽可能保留泛型实参信息）
            return ResolvableType.forInstance(delegate);
        }
    }
}
