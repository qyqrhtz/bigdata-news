# Step 15 - API Reference and Contract Tests

## Problem

项目接口数量增加后，如果只依赖代码或 Swagger 页面，答辩时不容易快速讲清楚：

- 哪些接口是正式业务入口。
- RAG、统计分析、健康检查分别怎么调用。
- 错误响应格式是否统一。
- 删除调试接口后默认环境到底暴露了哪些 API。

## Change

新增接口参考文档：

```text
docs/API_REFERENCE.md
```

文档内容包括：

- Health 接口。
- News/RAG 接口。
- Statistics/Analytics 接口。
- 统一错误响应格式。
- 调试接口治理策略。

同时补强后端契约测试：

- 验证 OpenAPI 中包含 `Health` 和 `Statistics APIs` 标签。
- 验证 `/api/test/coze/simple` 默认返回 HTTP 404。
- 验证 404 错误体仍使用统一 `ApiErrorResponse` 结构。

## Value

- 前端联调和答辩讲解时有稳定接口清单。
- 错误响应和 OpenAPI 标签由测试兜底，不只停留在文档描述。
- 项目材料更接近真实工程交付，能体现 API 契约意识。

## Verification

```powershell
.\mvnw.cmd clean test
```

预期结果：

- OpenAPI 文档端点返回 200。
- OpenAPI 中包含核心接口分组。
- 默认环境不暴露 Coze 调试接口。
- 404 响应结构和状态码正确。
