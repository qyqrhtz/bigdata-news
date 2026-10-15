# Frontend Guide

前端项目位于：

```text
news-frontend/
```

技术栈：

- Vue 3
- Vite
- Element Plus
- ECharts
- Axios

## Environment

前端通过环境变量配置后端地址：

```text
VITE_API_BASE_URL=http://localhost:8081/api
```

示例文件：

```text
news-frontend/.env.example
```

本地开发时可以复制为 `.env`：

```powershell
Copy-Item news-frontend\.env.example news-frontend\.env
```

## Commands

安装依赖：

```powershell
cd news-frontend
npm ci
```

启动开发服务：

```powershell
npm run dev
```

生产构建：

```powershell
npm run build
```

预览生产构建：

```powershell
npm run preview
```

## Pages

| Route | View | Purpose | Main APIs |
| --- | --- | --- | --- |
| `/` | `Menu.vue` | 入口菜单 | None |
| `/dashboard` | `DataScreen.vue` | 新闻数据大屏 | `GET /news/page`, `GET /news/stats/dashboard` |
| `/topics` | `Topics.vue` | 主题分析 | `GET /analytics/topics`, `POST /analytics/recompute` |
| `/authors` | `Authors.vue` | 作者异常分析 | `GET /analytics/authors/anomaly`, `POST /analytics/recompute` |
| `/duplicates` | `Duplicates.vue` | 重复新闻分析 | `GET /analytics/duplicates`, `POST /analytics/recompute` |
| `/bursts` | `Bursts.vue` | 突发主题分析 | `GET /analytics/bursts`, `POST /analytics/recompute` |
| `/chat` | `Chat.vue` | RAG 智能问答 | `POST /news/rag/chat` |

## API Client

统一 Axios 实例：

```text
news-frontend/src/api/request.js
```

业务 API 封装：

```text
news-frontend/src/api/news.js
news-frontend/src/api/analytics.js
```

这样做的好处：

- 后端地址集中由 `VITE_API_BASE_URL` 控制。
- 页面不再硬编码 `localhost`。
- 前后端联调时只需要改环境变量。

## Backend Requirements

前端开发前建议确认后端可用：

```text
http://localhost:8081/api/health
```

接口文档：

```text
http://localhost:8081/swagger-ui.html
```

后端正式接口清单：

```text
docs/API_REFERENCE.md
```

## Build Verification

当前项目已通过生产构建：

```powershell
cd news-frontend
npm run build
```

构建产物在：

```text
news-frontend/dist/
```

`dist/` 已在 `.gitignore` 中忽略，不作为源码提交。

## Resume Value

前端部分可以这样描述：

```text
使用 Vue 3 + Vite + Element Plus + ECharts 构建新闻数据可视化前端，封装统一 Axios API 客户端，通过环境变量管理后端地址，支持统计大屏、主题分析、异常作者、重复新闻、突发主题和 RAG 问答页面。
```
