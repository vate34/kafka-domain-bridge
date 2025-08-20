package com.example.demo.events.versioning;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件版本管理器，用于处理事件的版本兼容性
 */
@Component
public class EventVersionManager {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 事件类型到版本转换器的映射
    private final Map<String, EventVersionConverter> versionConverters = new ConcurrentHashMap<>();

    /**
     * 注册事件版本转换器
     *
     * @param eventType 事件类型
     * @param converter 版本转换器
     */
    public void registerVersionConverter(String eventType, EventVersionConverter converter) {
        versionConverters.put(eventType, converter);
    }

    /**
     * 转换事件到指定版本
     *
     * @param eventType    事件类型
     * @param fromVersion  源版本
     * @param toVersion    目标版本
     * @param eventData    事件数据
     * @return 转换后的事件数据
     */
    public JsonNode convertEventVersion(String eventType, String fromVersion, String toVersion, JsonNode eventData) {
        EventVersionConverter converter = versionConverters.get(eventType);
        if (converter != null) {
            return converter.convert(fromVersion, toVersion, eventData);
        }
        // 如果没有找到转换器，返回原始数据
        return eventData;
    }

    /**
     * 检查事件版本是否兼容
     *
     * @param eventType   事件类型
     * @param eventVersion 事件版本
     * @return 是否兼容
     */
    public boolean isVersionCompatible(String eventType, String eventVersion) {
        EventVersionConverter converter = versionConverters.get(eventType);
        if (converter != null) {
            return converter.isCompatible(eventVersion);
        }
        // 如果没有找到转换器，默认认为兼容
        return true;
    }
}