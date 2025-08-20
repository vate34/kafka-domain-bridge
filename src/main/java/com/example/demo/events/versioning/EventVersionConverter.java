package com.example.demo.events.versioning;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 事件版本转换器接口
 */
public interface EventVersionConverter {
    
    /**
     * 转换事件数据从一个版本到另一个版本
     *
     * @param fromVersion 源版本
     * @param toVersion   目标版本
     * @param eventData   事件数据
     * @return 转换后的事件数据
     */
    JsonNode convert(String fromVersion, String toVersion, JsonNode eventData);
    
    /**
     * 检查指定版本是否兼容
     *
     * @param version 版本号
     * @return 是否兼容
     */
    boolean isCompatible(String version);
}