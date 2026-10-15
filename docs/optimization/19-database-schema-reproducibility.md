# Step 19 - Database Schema Reproducibility

## Problem

后端配置中 `spring.jpa.hibernate.ddl-auto=none`，这是更接近正式项目的选择，但也意味着 Spring Boot 不会自动创建 MySQL 表。Docker Compose 原本只创建数据库 `mydb`，没有初始化 `news` 和 `analytics_result` 表，老师或新环境首次运行时可能遇到表不存在的问题。

## Change

新增 MySQL 初始化脚本：

```text
mysql/init/01-schema.sql
```

并在 `docker-compose.yaml` 中挂载：

```yaml
./mysql/init:/docker-entrypoint-initdb.d:ro
```

新增数据库说明文档：

```text
docs/DATABASE_SCHEMA.md
```

文档说明：

- `news` 表字段、来源和索引。
- `analytics_result` 表字段和用途。
- Spark 写入 MySQL 的命令。
- 后端 API 与表的读取关系。
- RAG 服务如何读取 `news` 表构建 FAISS 索引。
- MySQL 初始化脚本只在首次初始化数据目录时执行。

## Value

- 解决首次启动 MySQL 后缺表的问题。
- 明确 Spark、后端、RAG 对数据库的读写关系。
- 让数据库结构不再只隐含在 Java Entity 和 Python 脚本里。
- 提升老师复现、答辩讲解和后续维护的清晰度。

## Verification

```powershell
docker compose config --quiet
```

预期结果：

- Compose 配置可解析。
- MySQL 服务挂载 `./mysql/init:/docker-entrypoint-initdb.d:ro`。

同时检查：

- `mysql/init/01-schema.sql` 存在。
- `docs/DATABASE_SCHEMA.md` 存在。
- README 已链接数据库说明文档。
