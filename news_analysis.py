# -*- coding: utf-8 -*-
import argparse
import os

from pyspark.sql import SparkSession
from pyspark.sql.functions import col, to_date, explode, split, lit
from pyspark.sql.types import StructType, StructField, StringType


def parse_args():
    parser = argparse.ArgumentParser(description="Run Spark statistics over processed news data and write results to MySQL.")
    parser.add_argument("--input-path", default=os.getenv("NEWS_ANALYSIS_INPUT", "hdfs://namenode:8020/news/processed"))
    parser.add_argument("--input-format", default=os.getenv("NEWS_ANALYSIS_FORMAT", "parquet"), choices=["parquet", "json"])
    parser.add_argument("--mysql-url", default=os.getenv("SPARK_MYSQL_URL", "jdbc:mysql://mysql8:3306/mydb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"))
    parser.add_argument("--mysql-user", default=os.getenv("SPARK_MYSQL_USER", "root"))
    parser.add_argument("--mysql-password", default=os.getenv("SPARK_MYSQL_PASSWORD", "123456"))
    parser.add_argument("--mysql-driver", default=os.getenv("SPARK_MYSQL_DRIVER", "com.mysql.cj.jdbc.Driver"))
    parser.add_argument("--jdbc-jar", default=os.getenv("SPARK_MYSQL_JAR", "/opt/spark/jars/mysql-connector-java-8.0.29.jar"))
    parser.add_argument("--shuffle-partitions", default=os.getenv("SPARK_SHUFFLE_PARTITIONS", "4"))
    parser.add_argument("--write-mode", default=os.getenv("SPARK_WRITE_MODE", "overwrite"))
    return parser.parse_args()


def main():
    args = parse_args()

    spark_builder = (
        SparkSession.builder
        .appName("News Analysis")
        .config("spark.sql.shuffle.partitions", args.shuffle_partitions)
    )
    if args.jdbc_jar:
        spark_builder = spark_builder.config("spark.jars", args.jdbc_jar)

    spark = spark_builder.getOrCreate()

    schema = StructType([
        StructField("source", StringType(), True),
        StructField("date", StringType(), True),
        StructField("category", StringType(), True),
        StructField("title", StringType(), True)
    ])

    if args.input_format == "json":
        df_clean = spark.read.schema(schema).json(args.input_path)
    else:
        df_clean = spark.read.parquet(args.input_path)

    if "source" not in df_clean.columns:
        df_clean = df_clean.withColumn("source", lit("unknown"))
    df_clean.printSchema()
    df_clean.show(5, truncate=False)

    source_stats = df_clean.groupBy("source").count()
    df_clean = df_clean.withColumn("date_only", to_date(col("date")))
    date_stats = df_clean.groupBy("date_only").count()
    category_stats = df_clean.groupBy("category").count()
    words = df_clean.select(explode(split(col("title"), " ")).alias("word"))
    word_count = words.groupBy("word").count().orderBy(col("count").desc())

    mysql_props = {
        "user": args.mysql_user,
        "password": args.mysql_password,
        "driver": args.mysql_driver
    }

    source_stats.write.mode(args.write_mode).jdbc(args.mysql_url, "source_stats", properties=mysql_props)
    date_stats.write.mode(args.write_mode).jdbc(args.mysql_url, "date_stats", properties=mysql_props)
    category_stats.write.mode(args.write_mode).jdbc(args.mysql_url, "category_stats", properties=mysql_props)
    word_count.write.mode(args.write_mode).jdbc(args.mysql_url, "word_count", properties=mysql_props)

    print("News analysis completed and results were written to MySQL.")
    spark.stop()


if __name__ == "__main__":
    main()
