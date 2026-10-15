# Bigdata News Analytics Platform

大数据新闻分析平台，整合 Spring Boot 后端、Spark/Hadoop 数据处理、Vue 前端和 FAISS RAG 检索服务，用于新闻数据清洗、统计分析、可视化和语义检索。

## Modules

- `src/`: Spring Boot backend and analytics APIs.
- `rag-service/`: Flask + FAISS semantic retrieval service.
- `news-frontend/`: Vue 3 + Vite frontend.
- `news_to_parquet.py`, `news_analysis.py`: Spark jobs for HDFS processing and MySQL analytics output.
- `docker-compose.yaml`: MySQL, Hadoop, Spark, Mongo and Redis local development stack.
- `docs/optimization/`: Step-by-step optimization notes for resume and defense material.
- `docs/API_REFERENCE.md`: Backend API catalog for demo, frontend integration and defense.
- `docs/ARCHITECTURE_AND_DEFENSE.md`: Architecture, data flow, demo order and defense talking points.
- `docs/FRONTEND_GUIDE.md`: Frontend pages, API mapping, environment variables and build commands.
- `docs/DATABASE_SCHEMA.md`: MySQL schema, data write path and RAG read path.
- `docs/CI.md`: GitHub Actions checks and local equivalent verification commands.
- `docs/SUBMISSION_CHECKLIST.md`: Submission scope, ignored files, verification commands and teacher review path.

## Quick Start

For prerequisites, portable model setup, configuration loading, validation scope,
and offline recovery, see [Reproduction and recovery](docs/REPRODUCTION.md).
The Compose stack is for local development, not an Internet-facing deployment.
Change example passwords before using real data. Existing containers with the same
names or services using ports 3306/8080/5000 must not be overwritten.

1. Start infrastructure:

```bash
docker compose up -d
```

Start infrastructure with the optional RAG container:

```bash
docker compose --profile rag up -d
```

2. Start RAG service:

```bash
cd rag-service
pip install -r requirements.txt
python app.py
```

Or build the container image:

```bash
docker compose --profile rag build rag-service
```

3. Start backend:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
mvnw.cmd spring-boot:run
```

Open API documentation:

```text
http://localhost:8081/swagger-ui.html
http://localhost:8081/v3/api-docs
```

Health check:

```text
http://localhost:8081/api/health
```

4. Start frontend:

```bash
cd news-frontend
npm install
npm run dev
```

## Data Jobs

Fetch Juhe news JSON:

```bash
python main.py --appkey "$JUHE_APPKEY" --news-type guonei --output-dir news_data
```

Merge raw JSON into HDFS Parquet:

```bash
spark-submit news_to_parquet.py \
  --raw-dir hdfs://namenode:8020/news/raw/*.json \
  --processed-dir hdfs://namenode:8020/news/processed
```

Write raw cleaned news into MySQL:

```bash
spark-submit test.py \
  --input-path hdfs://namenode:8020/news/raw/news_data.json \
  --mysql-url "jdbc:mysql://mysql8:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true" \
  --mysql-table news
```

Run analytics over processed Parquet:

```bash
spark-submit news_analysis.py \
  --input-path hdfs://namenode:8020/news/processed \
  --input-format parquet \
  --mysql-url "jdbc:mysql://mysql8:3306/mydb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
```

## Verification

Run the submission check before demo or delivery:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/verify.ps1
```

The script verifies:

- Spring Boot clean tests and OpenAPI/validation smoke tests.
- Python data jobs and RAG service syntax in WSL2 when WSL can be called from PowerShell script mode.
- Docker Compose configuration with and without the optional RAG profile.
- Vue frontend production build.

If the frontend dependencies are missing, the script runs `npm ci` before `npm run build`.
If WSL reports a distro error from script mode, copy the command printed by the script and run it directly in PowerShell.
Use `-StrictWsl` when you want the script to fail on WSL syntax-check errors.

## Documentation

- [API Reference](docs/API_REFERENCE.md)
- [Architecture And Defense Guide](docs/ARCHITECTURE_AND_DEFENSE.md)
- [Frontend Guide](docs/FRONTEND_GUIDE.md)
- [Database Schema](docs/DATABASE_SCHEMA.md)
- [CI Verification](docs/CI.md)
- [Submission Checklist](docs/SUBMISSION_CHECKLIST.md)
- [Optimization Roadmap](docs/optimization/00-roadmap.md)
- [Resume Bullets](docs/optimization/resume-bullets.md)

## Resume Highlights

- Built a multi-service news analytics platform with Spring Boot, Spark, Hadoop, MySQL and Vue.
- Polished project metadata and startup output so build artifacts, IDE display and runtime logs match the platform positioning.
- Implemented FAISS-based semantic retrieval and connected it to backend RAG endpoints.
- Externalized runtime configuration and removed hard-coded secrets for safer deployment.
- Added health and dependency configuration checks for easier demo and deployment diagnostics.
- Removed debug-only test APIs from the default backend surface and kept OpenAPI documentation focused on business endpoints.
- Added an API reference and tests for OpenAPI tags, validation errors and 404 responses.
- Added an architecture and defense guide covering module responsibilities, data flow, demo order and interview talking points.
- Added frontend integration documentation covering routes, API mapping, environment variables and build verification.
- Added MySQL initialization schema and database documentation for reproducible local setup.
- Added GitHub Actions CI for backend tests, Python syntax checks, Docker Compose validation and frontend build.
- Added a submission checklist covering deliverables, ignored large files and teacher review order.
- Added Docker Compose orchestration and optimization documentation for reproducible delivery.
