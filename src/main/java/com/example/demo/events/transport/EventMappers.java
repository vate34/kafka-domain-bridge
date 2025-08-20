package com.example.demo.events.transport;

import com.example.demo.events.domain.BaseDomainEvent;
import com.example.demo.events.domain.DomainEventMessage;
import com.example.demo.events.versioning.EventVersionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * 负责在领域事件与传输事件之间转换。
 * 通过 schemaId ↔ Class 的注册表隔离内部类型信息。
 */
public final class EventMappers {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    // 对未知/未注册负载的保底 schemaId
    private static final String GENERIC_SCHEMA = "generic.v1";

    private EventMappers() {
    }

    /**
     * 将通用领域事件对象转换为跨进程传输使用的事件消息对象。
     *
     * @param event 要转换的通用领域事件，包含事件标识、类型、时间戳、关联 ID、扩展属性及负载信息。
     * @return 转换后的跨进程传输事件消息，包含 schemaId 表示的业务负载契约。
     */
    public static DomainEventMessage toMessage(BaseDomainEvent<?> event) {
        Object payload = event.getPayload();
        String schemaId = null;

        if (payload != null) {
            // 只在本地查注册表，不把类名写出
            schemaId = EventPayloadRegistry.resolveSchemaId(payload.getClass());
        }
        if (schemaId == null) {
            schemaId = GENERIC_SCHEMA;
        }

        return new DomainEventMessage(
                event.getId(),
                event.getType(),
                schemaId,
                event.getOccurredAt(),
                event.getCorrelationId(),
                event.getAttributes(),
                payload // 作为普通 JSON 结构序列化
        );
    }

    /**
     * 将传输事件消息对象转换为通用领域事件对象。
     *
     * @param msg 跨进程传输的事件消息对象，包含事件标识、类型、时间戳、关联 ID、扩展属性及业务负载信息。
     * @param eventVersionManager 事件版本管理器（可选）
     * @return 转换后的通用领域事件对象，包含类型化的业务负载及其他领域事件信息。
     */
    public static BaseDomainEvent<?> toDomainEvent(DomainEventMessage msg, EventVersionManager eventVersionManager) {
        // 如果提供了版本管理器，先进行版本转换
        Object payload = msg.payload();
        if (eventVersionManager != null) {
            // 检查版本兼容性
            if (!eventVersionManager.isVersionCompatible(msg.type(), extractVersionFromSchemaId(msg.schemaId()))) {
                // 如果不兼容，尝试转换到兼容版本
                JsonNode payloadNode = OBJECT_MAPPER.valueToTree(payload);
                JsonNode convertedPayload = eventVersionManager.convertEventVersion(
                        msg.type(), 
                        extractVersionFromSchemaId(msg.schemaId()), 
                        "v1", // 目标版本
                        payloadNode
                );
                payload = OBJECT_MAPPER.convertValue(convertedPayload, Map.class);
            }
        }
        
        Class<?> payloadClass = resolvePayloadClassBySchema(msg.schemaId());
        Object typedPayload = convertPayload(payload, payloadClass);

        return BaseDomainEvent.builder()
                .id(msg.id())
                .type(msg.type())
                .occurredAt(msg.occurredAt())
                .correlationId(msg.correlationId())
                .attributes(msg.attributes())
                .payload(typedPayload)
                .build();
    }

    /**
     * 将传输事件消息对象转换为通用领域事件对象（无版本管理）。
     *
     * @param msg 跨进程传输的事件消息对象，包含事件标识、类型、时间戳、关联 ID、扩展属性及业务负载信息。
     * @return 转换后的通用领域事件对象，包含类型化的业务负载及其他领域事件信息。
     */
    public static BaseDomainEvent<?> toDomainEvent(DomainEventMessage msg) {
        return toDomainEvent(msg, null);
    }

    /**
     * 根据给定的 schemaId 确定对应的负载 Java 类型。
     * 如果 schemaId 为空或未能找到匹配的类型，默认返回 Map.class。
     *
     * @param schemaId 业务级负载契约标识符，用于定位对应的 Java 类型
     * @return schemaId 对应的 Java 类型；如果未找到，返回 Map.class
     */
    private static Class<?> resolvePayloadClassBySchema(String schemaId) {
        if (schemaId == null) return Map.class;
        Class<?> clazz = EventPayloadRegistry.resolveClass(schemaId);
        return (clazz != null ? clazz : Map.class);
    }

    /**
     * 将给定的负载对象转换为目标类型的实例。
     * 如果负载为 null 或已是目标类型的实例，则直接返回输入负载。
     * 如果类型不匹配，使用 ObjectMapper 执行类型转换。
     *
     * @param payload     要转换的负载对象，可以为任意类型
     * @param targetClass 目标类型的 Class 对象，用于指定转换后的类型
     * @return 转换后的目标类型实例；如果负载为 null 或已是目标类型，则直接返回输入负载
     */
    private static Object convertPayload(Object payload, Class<?> targetClass) {
        if (payload == null || targetClass.isInstance(payload)) {
            return payload;
        }
        return OBJECT_MAPPER.convertValue(payload, targetClass);
    }
    
    /**
     * 从schemaId中提取版本信息
     * 
     * @param schemaId schemaId
     * @return 版本信息
     */
    private static String extractVersionFromSchemaId(String schemaId) {
        if (schemaId == null) return "v1";
        int lastDotIndex = schemaId.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < schemaId.length() - 1) {
            return schemaId.substring(lastDotIndex + 1);
        }
        return "v1";
    }
}
