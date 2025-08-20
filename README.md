# Kafka Domain Bridge

Kafka Domain Bridge是一个基于Spring Boot和Apache Kafka的事件驱动微服务应用，用于在微服务之间传递领域事件。

## 架构优化

本项目已经进行了以下架构优化：

### 1. 事件处理架构优化

- **事件发布器分离**: 将事件发布功能分离为本地发布器和Kafka发布器
- **统一事件发布服务**: 提供协调本地和Kafka事件发布的统一服务
- **事件版本管理**: 支持事件版本兼容性处理和版本转换

### 2. 配置管理优化

- **多环境配置**: 支持开发、测试、生产环境的独立配置
- **动态配置**: 支持通过环境变量覆盖配置参数

### 3. 可观测性增强

- **结构化日志**: 使用统一的日志格式
- **日志文件输出**: 支持将日志输出到文件

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/example/demo/
│   │       ├── ApplicationStarter.java
│   │       ├── config/
│   │       ├── events/
│   │       │   ├── consumer/
│   │       │   ├── domain/
│   │       │   ├── publisher/    # 事件发布器
│   │       │   ├── transport/
│   │       │   └── versioning/   # 事件版本管理
│   │       └── web/
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       └── application-prod.yml
└── test/
    ├── http/
    └── java/
```

## 核心组件

### 事件发布器 (publisher)

- `LocalEventPublisher`: 本地事件发布器
- `KafkaEventPublisher`: Kafka事件发布器
- `EventPublishingService`: 统一事件发布服务

### 事件版本管理 (versioning)

- `EventVersionManager`: 事件版本管理器
- `EventVersionConverter`: 事件版本转换器接口
- `OrderCreatedEventVersionConverter`: OrderCreated事件版本转换器

### 事件处理 (consumer)

- `KafkaEventInboundBridge`: Kafka事件入站桥接器
- `EnhancedEventListeners`: 增强的事件监听器示例

## 配置说明

### 环境变量

- `KAFKA_BOOTSTRAP_SERVERS`: Kafka服务器地址
- `APP_CONSUMER_GROUP`: Kafka消费者组ID

### 多环境配置

- `application.yml`: 主配置文件
- `application-dev.yml`: 开发环境配置
- `application-prod.yml`: 生产环境配置

## 使用方法

### 启动应用

```bash
# 使用默认配置（开发环境）
mvn spring-boot:run

# 使用生产环境配置
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### 测试API

创建订单：
```bash
curl -X POST "http://localhost:8080/orders?userId=user123&amount=99.99"
```

查询服务信息：
```bash
curl -X GET "http://localhost:8080/who"
```

## 架构设计原则

1. **单一职责**: 每个组件只负责一个特定的功能
2. **开闭原则**: 对扩展开放，对修改关闭
3. **依赖倒置**: 依赖抽象而不是具体实现
4. **接口隔离**: 使用接口隔离不同的功能模块
5. **无环依赖**: 避免组件之间的循环依赖

## 未来优化方向

1. **集成分布式追踪**: 集成OpenTelemetry或Spring Cloud Sleuth
2. **添加指标监控**: 集成Micrometer和Prometheus
3. **增强安全机制**: 添加API密钥验证和JWT支持
4. **完善测试套件**: 添加集成测试和端到端测试
5. **支持动态配置**: 集成Spring Cloud Config