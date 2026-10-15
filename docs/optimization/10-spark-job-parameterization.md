# Step 10 - Spark Job Parameterization

## Before

- `main.py` contained a hard-coded Juhe API key.
- Spark scripts had hard-coded HDFS paths, MySQL URLs, table names and passwords.
- `news_analysis.py` read the processed directory as JSON even though `news_to_parquet.py` writes Parquet.
- The analytics job assumed a `source` column that may not exist in processed data.

## Changes

- Added `argparse` and environment-variable defaults to:
  - `main.py`
  - `news_to_parquet.py`
  - `news_analysis.py`
  - `test.py`
- Removed the hard-coded Juhe key from source code.
- Added Spark and data-job variables to `.env.example`.
- Updated `news_analysis.py` to support `--input-format parquet|json`.
- Made Parquet the default analysis input format.
- Added a fallback `source = "unknown"` column when processed data does not contain source information.
- Added README commands for the end-to-end data pipeline.

## Why It Matters

This turns one-off local scripts into reusable data jobs:

> 将新闻采集与 Spark 清洗分析脚本参数化，移除硬编码密钥、HDFS 路径和 MySQL 配置，并修复 Parquet/JSON 读写不一致问题，提升大数据处理链路的可复现性。

## Example Commands

```bash
python main.py --appkey "$JUHE_APPKEY" --news-type guonei --output-dir news_data
```

```bash
spark-submit news_to_parquet.py \
  --raw-dir hdfs://namenode:8020/news/raw/*.json \
  --processed-dir hdfs://namenode:8020/news/processed
```

```bash
spark-submit news_analysis.py \
  --input-path hdfs://namenode:8020/news/processed \
  --input-format parquet \
  --mysql-url "jdbc:mysql://mysql8:3306/mydb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
```

## Verification

```bash
python3 -m py_compile main.py news_to_parquet.py news_analysis.py test.py
```

Expected:

- All scripts pass syntax compilation.
- Runtime parameters can be supplied through CLI args or environment variables.
