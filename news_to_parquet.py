import argparse
import os

from pyspark.sql import SparkSession
from pyspark.sql.functions import explode, col
from pyspark.sql.utils import AnalysisException


def parse_args():
    parser = argparse.ArgumentParser(description="Merge raw Juhe JSON news files into de-duplicated Parquet data.")
    parser.add_argument("--raw-dir", default=os.getenv("NEWS_RAW_DIR", "hdfs://namenode:8020/news/raw/*.json"))
    parser.add_argument("--processed-dir", default=os.getenv("NEWS_PROCESSED_DIR", "hdfs://namenode:8020/news/processed"))
    parser.add_argument("--app-name", default=os.getenv("SPARK_APP_NAME", "NewsJsonToParquet"))
    return parser.parse_args()


def main():
    args = parse_args()

    # 1. 创建 SparkSession
    spark = SparkSession.builder \
        .appName(args.app_name) \
        .getOrCreate()

    print("Reading raw JSON files...")
    try:
        # 2. 读取 JSON 文件，忽略格式错误
        df_raw = spark.read.option("multiLine", True).option("mode", "PERMISSIVE").json(args.raw_dir)
        if df_raw.rdd.isEmpty():
            print("No raw JSON files found. Exiting.")
            spark.stop()
            return
    except Exception as e:
        print(f"Error reading raw files: {e}")
        spark.stop()
        return

    print("Exploding 'result.data' array...")
    # 3. 展开 data 数组
    df_news = df_raw.select(explode(col("result.data")).alias("news"))

    print("Selecting required fields...")
    # 4. 选择需要的字段
    df_news = df_news.select(
        col("news.uniquekey"),
        col("news.title"),
        col("news.date"),
        col("news.category"),
        col("news.author_name"),
        col("news.url"),
        col("news.thumbnail_pic_s"),
        col("news.thumbnail_pic_s02"),
        col("news.thumbnail_pic_s03"),
        col("news.is_content")
    )

    # 5. 读取历史数据，如果不存在则创建空 DataFrame
    try:
        df_history = spark.read.parquet(args.processed_dir)
        print("Historical data loaded.")
    except AnalysisException:
        print("No historical data found. Creating empty DataFrame.")
        df_history = spark.createDataFrame([], df_news.schema)

    # 6. 合并去重
    df_combined = df_history.union(df_news).dropDuplicates(["uniquekey"])
    total_count = df_combined.count()
    print(f"Total news items after merge: {total_count}")

    # 7. 写入 Parquet（覆盖整个目录）
    df_combined.write.mode("overwrite").parquet(args.processed_dir)
    print(f"Data successfully written to {args.processed_dir}")

    spark.stop()
    print("Spark session stopped.")


if __name__ == "__main__":
    main()
