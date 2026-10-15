# Step 02 - RAG Service Modularization

## Before

- RAG 代码位于 `D:\pythonRag\main.py`，不在主项目结构中。
- 模型路径、数据库地址、索引文件名全部硬编码。
- 缺少依赖清单和健康检查接口。

## Changes

- 新增 `rag-service/` 模块：
  - `app.py`
  - `requirements.txt`
  - `README.md`
- 使用环境变量管理：
  - `RAG_MODEL_PATH`
  - `RAG_DB_URL`
  - `RAG_INDEX_FILE`
  - `RAG_DATA_FILE`
  - `RAG_BATCH_SIZE`
- 增加 `/health` 接口，展示索引数量、元数据数量和模型路径。
- `/rag` 接口限制 `top_k` 范围为 1 到 20，避免异常请求拖垮服务。

## Why It Matters

这一步把 RAG 从“个人脚本”升级成“可独立运行的微服务”。简历中可以表达为：

> 将 FAISS 语义检索脚本重构为独立 Flask 服务，支持索引缓存、健康检查和环境变量配置，并通过 Spring Boot 网关接口对外提供 RAG 检索能力。

## Verification

启动服务后访问：

```text
GET http://localhost:5000/health
GET http://localhost:5000/rag?query=climate&top_k=5
```

后端通过 `RAG_SERVICE_URL` 调用该服务。
