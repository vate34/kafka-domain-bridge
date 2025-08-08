package com.example.demo.events.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * 仅用于跨进程传输（Kafka）的事件模型。
 * 完全不暴露内部类型信息，通过 schemaId 指示业务负载的业务级契约。
 *
 * @param type     业务事件名，如 "OrderCreated"
 * @param schemaId 业务级负载契约标识，如 "order.created.v1"
 * @param payload  JSON 结构体（Map/Record 等）
 */
public record DomainEventMessage(String id, String type, String schemaId, Instant occurredAt, String correlationId,
                                 Map<String, Object> attributes, Object payload) implements Serializable {

    @JsonCreator
    public DomainEventMessage(
            @JsonProperty("id") String id,
            @JsonProperty("type") String type,
            @JsonProperty("schemaId") String schemaId,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("attributes") Map<String, Object> attributes,
            @JsonProperty("payload") Object payload) {
        this.id = (id != null ? id : UUID.randomUUID().toString());
        this.type = type;
        this.schemaId = schemaId;
        this.occurredAt = (occurredAt != null ? occurredAt : Instant.now());
        this.correlationId = correlationId;
        this.attributes = attributes;
        this.payload = payload;
    }
}
