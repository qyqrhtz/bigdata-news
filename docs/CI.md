# CI Verification

持续集成配置位于：

```text
.github/workflows/ci.yml
```

CI 在 `push` 和 `pull_request` 时运行，用于验证项目是否仍可构建、可测试、可解析。

## Checks

| Step | Command | Purpose |
| --- | --- | --- |
| Backend clean test | `./mvnw clean test` | 编译后端、运行 MockMvc/API 契约测试，避免旧 class 残留 |
| Python syntax check | `python3 -m py_compile ...` | 检查采集、Spark、RAG 脚本语法 |
| Docker Compose config | `docker compose config --quiet` | 校验默认基础设施编排 |
| Docker Compose RAG config | `docker compose --profile rag config --quiet` | 校验可选 RAG 服务编排 |
| Frontend install | `npm ci` | 使用锁文件安装前端依赖 |
| Frontend build | `npm run build` | 验证 Vue/Vite 生产构建 |

## Runtime Versions

| Runtime | Version |
| --- | --- |
| Java | 17 |
| Node.js | 22 |
| Python | GitHub Ubuntu runner default `python3` |
| Docker Compose | GitHub Ubuntu runner bundled version |

## Local Equivalent

Windows 本地可以使用：

```powershell
powershell -ExecutionPolicy Bypass -File scripts/verify.ps1
```

也可以手工运行：

```powershell
.\mvnw.cmd clean test
docker compose config --quiet
docker compose --profile rag config --quiet
cd news-frontend
npm ci
npm run build
```

Python 脚本语法检查：

```powershell
wsl.exe -d Ubuntu-22.04 -- sh -lc "python3 -m py_compile /mnt/d/bigdata-news/main.py /mnt/d/bigdata-news/news_to_parquet.py /mnt/d/bigdata-news/news_analysis.py /mnt/d/bigdata-news/test.py /mnt/d/bigdata-news/rag-service/app.py /mnt/d/bigdata-news/rag-service/wsgi.py"
```

## Resume Value

可以这样描述：

```text
为项目补充 GitHub Actions CI，自动执行后端 clean test、Python 脚本语法检查、Docker Compose 配置校验和前端生产构建，提升多模块项目的回归验证能力。
```
