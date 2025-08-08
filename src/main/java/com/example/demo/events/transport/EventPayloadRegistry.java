package com.example.demo.events.transport;

import com.example.demo.events.OrderCreatedPayload;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 本地注册表：维护业务级 schemaId 与 Java Payload 类型之间的双向映射。
 * Kafka 消息只携带 schemaId，应用内用它来恢复强类型。
 */
public final class EventPayloadRegistry {

    private static final Map<String, Class<?>> SCHEMA_TO_CLASS;
    private static final Map<Class<?>, String> CLASS_TO_SCHEMA;

    static {
        Map<String, Class<?>> s2c = new HashMap<>();
        Map<Class<?>, String> c2s = new HashMap<>();

        // 在此登记所有受支持的负载契约
        register(s2c, c2s, "order.created.v1", OrderCreatedPayload.class);

        SCHEMA_TO_CLASS = Collections.unmodifiableMap(s2c);
        CLASS_TO_SCHEMA = Collections.unmodifiableMap(c2s);
    }

    private EventPayloadRegistry() {
    }

    private static void register(Map<String, Class<?>> s2c, Map<Class<?>, String> c2s,
                                 String schemaId, Class<?> clazz) {
        s2c.put(schemaId, clazz);
        c2s.put(clazz, schemaId);
    }

    public static Class<?> resolveClass(String schemaId) {
        return SCHEMA_TO_CLASS.get(schemaId);
    }

    public static String resolveSchemaId(Class<?> clazz) {
        return CLASS_TO_SCHEMA.get(clazz);
    }
}
