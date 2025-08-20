package com.example.demo.events.versioning;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * OrderCreated事件版本转换器
 */
public class OrderCreatedEventVersionConverter implements EventVersionConverter {
    
    @Override
    public JsonNode convert(String fromVersion, String toVersion, JsonNode eventData) {
        // 如果是从v1转换到v2
        if ("v1".equals(fromVersion) && "v2".equals(toVersion)) {
            return convertV1ToV2(eventData);
        }
        // 如果是从v2转换到v1
        else if ("v2".equals(fromVersion) && "v1".equals(toVersion)) {
            return convertV2ToV1(eventData);
        }
        // 其他情况返回原始数据
        return eventData;
    }
    
    @Override
    public boolean isCompatible(String version) {
        // v1和v2都是兼容的版本
        return "v1".equals(version) || "v2".equals(version);
    }
    
    /**
     * 将v1版本的OrderCreated事件转换为v2版本
     *
     * @param eventData v1版本的事件数据
     * @return v2版本的事件数据
     */
    private JsonNode convertV1ToV2(JsonNode eventData) {
        if (eventData instanceof ObjectNode objectNode) {
            // 在v2中添加currency字段，默认为CNY
            objectNode.put("currency", "CNY");
            return objectNode;
        }
        return eventData;
    }
    
    /**
     * 将v2版本的OrderCreated事件转换为v1版本
     *
     * @param eventData v2版本的事件数据
     * @return v1版本的事件数据
     */
    private JsonNode convertV2ToV1(JsonNode eventData) {
        if (eventData instanceof ObjectNode objectNode) {
            // 移除v2中新增的currency字段
            objectNode.remove("currency");
            return objectNode;
        }
        return eventData;
    }
}