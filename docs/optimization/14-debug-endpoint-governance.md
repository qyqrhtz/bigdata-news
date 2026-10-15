# Step 14 - Debug Endpoint Governance

## Problem

课程项目中常见的临时接口如果直接保留在默认环境，会让项目显得不够正式：

- 根路径 `/` 只返回 `Spring Boot is running!`，与正式 API 契约无关。
- `/api/test/coze/*` 默认暴露测试 Coze 调用入口，容易和正式 RAG 问答接口混淆。

这些接口虽然开发阶段方便，但上交、答辩或写简历时会降低工程完整度。

## Change

- 删除临时根路径测试控制器 `TestController`。
- 删除临时 Coze 测试控制器 `CozeTestController`。
- 新增 MockMvc 测试，验证默认环境下 `/api/test/coze/simple` 返回 404。

保留正式入口：

- 服务健康检查：`GET /api/health`
- RAG 问答：`POST /api/news/rag/chat`
- 语义检索：`GET /api/news/search`

## Value

- 默认环境只暴露正式 API，项目观感更像可交付服务。
- OpenAPI 文档更干净，答辩展示时不混入临时测试接口。
- Coze 调用统一通过正式 RAG 问答链路验证，减少双入口带来的维护成本。

## Verification

```powershell
.\mvnw.cmd test
```

预期结果：

- 默认 profile 下 `/api/test/coze/simple` 返回 HTTP 404。
- 正式接口测试和健康检查测试继续通过。
