# Submission Checklist

这份清单用于最终上交、给老师验收或迁移到另一台电脑时检查项目是否完整。当前要求是不再生成新的 zip。

## Submit These Files

建议提交源码仓库或项目文件夹，重点包含：

| Path | Purpose |
| --- | --- |
| `README.md` | 项目总入口、启动方式、验证方式 |
| `.env.example` | 环境变量示例，不包含真实密钥 |
| `docker-compose.yaml` | MySQL、Hadoop、Spark、Redis、Mongo、可选 RAG 服务编排 |
| `mysql/init/01-schema.sql` | MySQL 首次启动建表脚本 |
| `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/` | Spring Boot 后端构建入口 |
| `src/` | Spring Boot 后端源码、DTO、测试 |
| `main.py` | 新闻采集脚本 |
| `news_to_parquet.py` | Spark JSON 到 Parquet 清洗脚本 |
| `news_analysis.py` | Spark 分析脚本 |
| `test.py` | HDFS 到 MySQL 加载脚本 |
| `rag-service/` | Flask/FAISS RAG 服务源码和 Dockerfile |
| `news-frontend/` | Vue 前端源码 |
| `scripts/verify.ps1` | 一键验证脚本 |
| `.github/workflows/ci.yml` | GitHub Actions CI 配置 |
| `docs/` | API、架构、前端、优化记录、简历材料 |

## Do Not Submit These Files

以下内容不建议提交，体积大、可生成、或包含机器本地状态：

| Path / Pattern | Reason |
| --- | --- |
| `all-MiniLM-L6-v2/` | 大模型目录，体积大，应单独说明下载或挂载 |
| `*.faiss`, `*.pkl` | 向量索引和中间数据，可由 RAG 服务重建或单独提供 |
| `news-frontend/node_modules/` | 前端依赖，可通过 `npm ci` 安装 |
| `news-frontend/dist/` | 前端构建产物，可通过 `npm run build` 生成 |
| `target/` | Maven 构建产物，可通过 `mvnw.cmd clean test` 重建 |
| `mysql/data/`, `rag-data/`, `data/` | 本地运行数据，不应进入源码提交 |
| `.env` | 本地真实配置，可能包含密钥 |
| `teacher-submit*.zip`, `teacher-submit-*/`, `submit/` | 旧提交包或临时打包目录 |
| `*.log`, `__pycache__/`, `.venv/` | 运行缓存和日志 |

这些规则已经写入 `.gitignore`。

## Before Submission

1. 检查配置示例：

```powershell
Test-Path .env.example
```

2. 运行后端 clean 测试：

```powershell
.\mvnw.cmd clean test
```

3. 校验 Docker Compose：

```powershell
docker compose config --quiet
docker compose --profile rag config --quiet
```

4. 构建前端：

```powershell
cd news-frontend
npm run build
```

5. 检查 Python 脚本语法：

```powershell
wsl.exe -d Ubuntu-22.04 -- sh -lc "python3 -m py_compile /mnt/d/bigdata-news/main.py /mnt/d/bigdata-news/news_to_parquet.py /mnt/d/bigdata-news/news_analysis.py /mnt/d/bigdata-news/test.py /mnt/d/bigdata-news/rag-service/app.py /mnt/d/bigdata-news/rag-service/wsgi.py"
```

6. 或运行提交前验证脚本：

```powershell
powershell -ExecutionPolicy Bypass -File scripts/verify.ps1
```

如果 WSL 在脚本模式下报 `WSL_E_DISTRO_NOT_FOUND`，复制脚本打印出的 WSL 命令手动执行。

## Teacher Review Path

老师可以按这个顺序查看：

1. `README.md`：了解项目是什么、怎么运行。
2. `docs/ARCHITECTURE_AND_DEFENSE.md`：看系统架构、数据流、答辩讲解。
3. `docs/API_REFERENCE.md`：看正式后端接口。
4. `docs/FRONTEND_GUIDE.md`：看前端页面和接口对应关系。
5. `docs/DATABASE_SCHEMA.md`：看 MySQL 表结构和数据流转。
6. `docs/CI.md`：看自动化验证配置。
7. `docs/optimization/`：看每一步优化记录。
8. `docs/optimization/resume-bullets.md`：看可写进简历的亮点。

## Model And Data Note

RAG 模型和索引不建议直接放进源码提交。可以在答辩说明中写：

```text
模型目录 all-MiniLM-L6-v2 与 FAISS 索引文件因体积较大，不纳入源码仓库。运行 RAG 时通过 RAG_MODEL_PATH、RAG_INDEX_FILE、RAG_DATA_FILE 指向本地模型和索引路径，或由服务根据 MySQL 新闻数据重建索引。
```

## Final Check

最终提交前确认：

- 没有新生成 zip。
- 没有提交真实 `.env`。
- 没有提交模型、索引、node_modules、dist、target。
- `README.md` 和 `docs/` 能说明项目价值和运行方式。
- `scripts/verify.ps1` 或等价手工命令已通过。
