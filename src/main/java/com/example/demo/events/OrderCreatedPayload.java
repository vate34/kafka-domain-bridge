// Business DTO 示例：OrderCreatedPayload.java
package com.example.demo.events;

import java.math.BigDecimal;

/**
 * 这是一个业务类
 *
 * @param orderId
 * @param userId
 * @param amount
 */
public record OrderCreatedPayload(
        String orderId,
        String userId,
        BigDecimal amount
) {
}
