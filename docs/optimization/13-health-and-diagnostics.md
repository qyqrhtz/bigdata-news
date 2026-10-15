# Step 13 - Health and Diagnostics

## Problem

项目包含后端、RAG 服务、Coze 工作流、前端和 Docker 基础设施。答辩或换环境运行时，若只能通过页面是否打开来判断服务状态，排查效率较低，也不利于说明工程化部署能力。

## Change

新增轻量健康检查接口：

```text
GET /api/health
```

返回内容包括：

- `status`：应用存活状态。
- `service`：应用名称。
- `timestamp`：服务端当前时间。
- `dependencies`：非敏感依赖配置摘要，例如 RAG 服务地址、Coze workflow 是否已配置。

新增文件：

- `src/main/java/com/example/bigdata_news/controller/HealthController.java`
- `src/main/java/com/example/bigdata_news/dto/HealthResponse.java`

同时在 MockMvc 测试中覆盖 `/api/health`，保证健康检查接口随自动化验证一起运行。

## Value

- 答辩时可以快速证明后端服务已启动。
- 部署排查时能先确认应用本身和关键依赖配置。
- 简历上可以描述为“增加服务健康检查和依赖诊断接口，提升环境迁移与演示排障效率”。

## Verification

```powershell
.\mvnw.cmd test
```

预期结果：

- `/api/health` 返回 HTTP 200。
- 返回体包含 `status=UP`、`service=bigdata-news` 和依赖配置摘要。
