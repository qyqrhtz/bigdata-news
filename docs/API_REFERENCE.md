# API Reference

本项目的后端默认运行在：

```text
http://localhost:8081
```

Swagger/OpenAPI 文档：

```text
http://localhost:8081/swagger-ui.html
http://localhost:8081/v3/api-docs
```

## Health

| Method | Path | Description |
| --- | --- | --- |
| GET | `/api/health` | 服务存活状态和非敏感依赖配置摘要 |

示例响应：

```json
{
  "status": "UP",
  "service": "bigdata-news",
  "timestamp": "2026-06-18T00:00:00Z",
  "dependencies": {
    "ragServiceUrl": "http://127.0.0.1:5000",
    "cozeWorkflowConfigured": "false"
  }
}
```

## News And RAG

| Method | Path | Description |
| --- | --- | --- |
| GET | `/api/news/page?page={page}&size={size}` | 分页查询新闻，`page >= 0`，`1 <= size <= 100` |
| GET | `/api/news/{id}` | 根据 ID 查询新闻 |
| DELETE | `/api/news/{id}` | 删除新闻 |
| GET | `/api/news/stats/dashboard` | 新闻可视化大屏统计 |
| GET | `/api/news/search?query={query}&topK={topK}` | 基于 RAG 服务的语义检索 |
| POST | `/api/news/rag/chat` | RAG 问答入口 |

RAG 问答请求：

```json
{
  "question": "最近有哪些科技新闻？"
}
```

RAG 问答响应：

```json
{
  "question": "最近有哪些科技新闻？",
  "answer": "根据当前检索结果...",
  "sources": [
    {
      "title": "新闻标题",
      "url": "https://example.com/news",
      "score": 0.83
    }
  ]
}
```

## Statistics And Analytics

| Method | Path | Description |
| --- | --- | --- |
| GET | `/api/stats/dashboard` | 前端大屏聚合统计数据 |
| GET | `/api/analytics/topics` | 主题分析结果 |
| GET | `/api/analytics/authors/anomaly` | 作者异常分析结果 |
| GET | `/api/analytics/duplicates` | 重复新闻分析结果 |
| GET | `/api/analytics/bursts` | 突发主题分析结果 |
| POST | `/api/analytics/recompute` | 重新计算缓存分析结果 |

## Error Response

后端使用统一错误响应格式：

```json
{
  "timestamp": "2026-06-18T00:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "question: question must not be blank",
  "path": "/api/news/rag/chat"
}
```

已覆盖的错误场景：

- RAG 问答问题为空：HTTP 400。
- 语义检索 `topK` 超出范围：HTTP 400。
- 新闻分页 `page` 为负数或 `size` 超出范围：HTTP 400。
- 调试接口或未知路径不存在：HTTP 404。

## Debug Endpoint Policy

默认环境不暴露临时调试 API：

```text
/api/test/coze/*
/
```

Coze 调用通过正式 RAG 问答接口验证：

```text
POST /api/news/rag/chat
```
