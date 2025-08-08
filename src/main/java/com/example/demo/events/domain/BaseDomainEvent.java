package com.example.demo.events.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * 通用领域事件，可携带任意业务负载与扩展属性
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@Data
@Builder
@NoArgsConstructor
public class BaseDomainEvent<T> implements Serializable, ResolvableTypeProvider {
    private String id;
    private String type;
    private Instant occurredAt;
    private String correlationId;
    private Map<String, Object> attributes; // 任意扩展键值
    private T payload; // 业务数据

    @JsonCreator
    public BaseDomainEvent(
            @JsonProperty("id") String id,
            @JsonProperty("type") String type,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("attributes") Map<String, Object> attributes,
            @JsonProperty("payload") T payload) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.type = type;
        this.occurredAt = occurredAt != null ? occurredAt : Instant.now();
        this.correlationId = correlationId;
        this.attributes = attributes;
        this.payload = payload;
    }

    /**
     * 向 Spring 暴露 BaseDomainEvent<T> 的具体泛型实参，便于按参数化类型匹配监听器
     */
    @Override
    @JsonIgnore
    public ResolvableType getResolvableType() {
        ResolvableType payloadType = (payload != null)
                ? ResolvableType.forInstance(payload)
                : ResolvableType.forClass(Object.class);
        return ResolvableType.forClassWithGenerics(BaseDomainEvent.class, payloadType);
    }
}
