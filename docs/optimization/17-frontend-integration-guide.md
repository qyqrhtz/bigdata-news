# Step 17 - Frontend Integration Guide

## Problem

前端已经迁入项目并完成构建优化，但如果只保留源码，老师或后续维护者需要自己读路由和 API 封装才能知道：

- 每个页面对应什么功能。
- 页面调用哪些后端接口。
- 前端如何配置后端地址。
- 如何启动、构建和验证。

这会降低项目的可复现性，也不利于答辩时展示完整前后端联调链路。

## Change

新增前端联调说明：

```text
docs/FRONTEND_GUIDE.md
```

文档内容包括：

- 前端技术栈。
- `VITE_API_BASE_URL` 环境变量。
- 本地开发、生产构建、预览命令。
- 页面路由与后端 API 映射。
- 统一 Axios 客户端说明。
- 后端健康检查和 Swagger 入口。
- 前端部分可写进简历的描述。

同时在 README 中加入文档入口。

## Value

- 前后端联调关系更清楚。
- 答辩展示时可以按页面说明快速讲业务功能。
- 简历上可以更自然地描述“前后端一体化项目”，而不是只写后端或 Spark。

## Verification

```powershell
cd news-frontend
npm run build
```

预期结果：

- Vite 生产构建通过。
- 页面路由和 API 封装仍能正常编译。
