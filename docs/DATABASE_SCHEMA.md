# Database Schema

后端默认连接 MySQL：

```text
jdbc:mysql://localhost:3306/mydb
```

配置入口：

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

Docker Compose 中 MySQL 首次初始化会执行：

```text
mysql/init/01-schema.sql
```

该脚本只负责建表，不包含真实业务数据。

## Tables

### `news`

新闻主表，供后端分页、详情、统计和 RAG 索引使用。

| Column | Type | Source | Description |
| --- | --- | --- | --- |
| `uniquekey` | `VARCHAR(64)` | Juhe API / raw JSON | 新闻唯一 ID，主键 |
| `title` | `VARCHAR(500)` | Juhe API / raw JSON | 新闻标题 |
| `date` | `DATETIME` | Juhe API / Spark parse | 发布时间 |
| `category` | `VARCHAR(50)` | Juhe API / Spark transform | 新闻分类 |
| `author_name` | `VARCHAR(100)` | Juhe API / raw JSON | 作者或来源 |
| `url` | `VARCHAR(500)` | Juhe API / raw JSON | 原文链接 |
| `thumbnail_pic_s` | `VARCHAR(500)` | Juhe API / raw JSON | 缩略图 1 |
| `thumbnail_pic_s02` | `VARCHAR(500)` | Juhe API / raw JSON | 缩略图 2 |
| `thumbnail_pic_s03` | `VARCHAR(500)` | Juhe API / raw JSON | 缩略图 3 |
| `is_content` | `INT` | Juhe API / raw JSON | 是否有正文标记 |

索引：

- `PRIMARY KEY (uniquekey)`
- `idx_news_date (date)`
- `idx_news_category (category)`
- `idx_news_author_name (author_name)`

### `analytics_result`

分析结果缓存表，供 `/api/analytics/*` 接口读取。

| Column | Type | Source | Description |
| --- | --- | --- | --- |
| `result_key` | `VARCHAR(64)` | Backend analytics service | 分析结果类型，例如 topics、duplicates |
| `updated_at` | `DATETIME` | Backend analytics service | 更新时间 |
| `json` | `LONGTEXT` | Backend analytics service | 分析结果 JSON |

索引：

- `PRIMARY KEY (result_key)`

## Data Write Path

新闻数据写入：

```powershell
spark-submit test.py `
  --input-path hdfs://namenode:8020/news/raw/news_data.json `
  --mysql-url "jdbc:mysql://mysql8:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true" `
  --mysql-table news
```

分析结果写入：

```powershell
spark-submit news_analysis.py `
  --input-path hdfs://namenode:8020/news/processed `
  --input-format parquet `
  --mysql-url "jdbc:mysql://mysql8:3306/mydb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
```

## Backend Read Path

| API | Table |
| --- | --- |
| `GET /api/news/page` | `news` |
| `GET /api/news/{id}` | `news` |
| `GET /api/news/stats/dashboard` | `news` |
| `GET /api/stats/dashboard` | `news` |
| `GET /api/analytics/topics` | `analytics_result` |
| `GET /api/analytics/authors/anomaly` | `analytics_result` |
| `GET /api/analytics/duplicates` | `analytics_result` |
| `GET /api/analytics/bursts` | `analytics_result` |

## RAG Read Path

RAG 服务通过 `RAG_DB_URL` 连接 MySQL，读取 `news` 表中的标题、链接等字段构建 FAISS 索引。

示例：

```text
RAG_DB_URL=mysql+pymysql://root:123456@mysql8/mydb
```

索引文件和数据缓存不写入数据库：

```text
RAG_INDEX_FILE=/data/news_index.faiss
RAG_DATA_FILE=/data/news_data.pkl
```

## Reproducibility Notes

- `mysql/init/01-schema.sql` 只会在 MySQL 数据目录第一次初始化时自动执行。
- 如果 `mysql/data/` 已经存在，修改初始化 SQL 后不会自动重新执行。
- 需要重新初始化数据库时，先备份重要数据，再清理 `mysql/data/`。
- `mysql/data/` 已被 `.gitignore` 忽略，不应提交。
