# Interview Assistant (Spring Boot + LangChain4j)

一个可直接用于面试展示的后端项目模板，聚焦于“AI 面试助手”场景：

- 生成岗位定制化面试题
- 评估候选人回答质量
- JWT 登录鉴权，保护业务接口
- PostgreSQL 持久化面试记录（Flyway 管理表结构）
- 支持 `LangChain4j(OpenAI)` 与 `Rule-Based` 双 Provider 切换

## 1. 技术栈

- Java 17
- Spring Boot 3
- LangChain4j
- Maven
- JUnit 5 + MockMvc

## 2. 项目亮点（面试可讲）

- 分层架构清晰：`controller -> service -> ai-client`
- 生产可用的健壮性：参数校验、统一异常处理、AI 调用失败自动回退
- 安全基线：JWT 鉴权、受保护 API、401 访问控制测试
- 工程化能力：可配置 Provider、数据库迁移、单元测试、Docker 化
- 可扩展：后续可挂接 RAG、向量数据库、审计日志、鉴权体系

## 3. 快速启动

### 本地启动

```bash
mvn spring-boot:run
```

默认使用规则引擎（无需 Key）。  
如果要启用 OpenAI：

1. 设置环境变量 `OPENAI_API_KEY`
2. 修改 `src/main/resources/application.yml`：

```yaml
app:
  ai:
    provider: openai
```

### 登录获取 Token

`POST /api/v1/auth/login`

```json
{
  "username": "admin",
  "password": "admin123"
}
```

后续请求在 Header 中携带：

`Authorization: Bearer <accessToken>`

默认使用内存 H2（便于本地零配置启动）。  
如需使用 PostgreSQL，可启用 profile：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

并配置环境变量：`DB_URL`、`DB_USERNAME`、`DB_PASSWORD`。

### 运行测试

```bash
mvn test
```

## 4. API 示例

### 4.1 生成面试题

`POST /api/v1/interview/questions`

```json
{
  "role": "Java后端工程师",
  "seniority": "Senior",
  "topics": ["缓存", "并发", "可观测性"],
  "questionCount": 3
}
```

### 4.2 评估回答

`POST /api/v1/interview/evaluate`

```json
{
  "question": "你如何优化一个高并发接口？",
  "candidateAnswer": "我会先压测定位瓶颈，再做缓存和限流，并补齐监控告警。",
  "expectedSignals": ["压测", "缓存", "限流", "监控"]
}
```

## 5. 数据落库说明

- 表名：`interview_records`
- 迁移脚本：`src/main/resources/db/migration/V1__create_interview_records.sql`
- 在以下场景自动记录请求与响应：
  - 生成面试题
  - 评估候选人回答

## 6. 目录结构

```text
src/main/java/com/example/interviewassistant
├─ config
├─ controller
├─ dto
├─ exception
├─ service
│  └─ ai
└─ InterviewAssistantApplication.java
```

## 7. 后续可扩展方向

- 接入 PostgreSQL 存储面试记录与评分历史
- 加入 Spring Security + JWT 鉴权
- 增加面试官工作台（React/Vue）
- 使用向量检索做企业题库增强（RAG）
