# Step 11 - RAG Dockerization

## Before

- RAG service could only be started with local Python.
- Docker Compose only started infrastructure services such as MySQL, Hadoop and Spark.
- Model files and FAISS cache handling were not documented for container deployment.

## Changes

- Added `rag-service/Dockerfile`.
- Added `rag-service/.dockerignore` to keep model files, FAISS indexes and cache data out of the image.
- Added `rag-service/wsgi.py` for Gunicorn startup.
- Updated `rag-service/app.py` with locked lazy initialization so WSGI/container startup loads runtime safely.
- Added `rag-service` to `docker-compose.yaml` behind the optional `rag` profile.
- Added volume mounts for:
  - SentenceTransformer model directory.
  - FAISS index and metadata cache directory.

## Why It Matters

This makes the RAG module a deployable microservice instead of a local-only script:

> 将 FAISS RAG 检索服务容器化，使用 Gunicorn 提供生产式启动入口，并通过 Docker Compose profile 与 MySQL 基础设施联动，模型和索引通过 volume 挂载避免大文件进入镜像。

## Commands

Build only the RAG image:

```bash
docker compose --profile rag build rag-service
```

Start infrastructure only:

```bash
docker compose up -d
```

Start infrastructure and RAG service:

```bash
docker compose --profile rag up -d
```

## Runtime Notes

- `RAG_MODEL_DIR` should point to the host model directory.
- `RAG_DATA_DIR` should contain or receive `news_index.faiss` and `news_data.pkl`.
- If FAISS cache files do not exist, the service will try to build them from MySQL.

## Verification

```bash
docker compose config --quiet
docker compose --profile rag config --quiet
```

Expected:

- Compose config passes with and without the `rag` profile.
