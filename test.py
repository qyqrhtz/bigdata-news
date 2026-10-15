import argparse
import os

from pyspark.sql import SparkSession
from pyspark.sql.functions import col, md5, to_timestamp, lit, substring


def parse_args():
    parser = argparse.ArgumentParser(description="Load cleaned news JSON from HDFS into MySQL.")
    parser.add_argument("--hdfs-default-fs", default=os.getenv("HDFS_DEFAULT_FS", "hdfs://namenode:8020"))
    parser.add_argument("--input-path", default=os.getenv("NEWS_MYSQL_INPUT", "hdfs://namenode:8020/news/raw/news_data.json"))
    parser.add_argument("--mysql-url", default=os.getenv("SPARK_MYSQL_URL", "jdbc:mysql://mysql8:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true"))
    parser.add_argument("--mysql-table", default=os.getenv("SPARK_MYSQL_TABLE", "news"))
    parser.add_argument("--mysql-user", default=os.getenv("SPARK_MYSQL_USER", "root"))
    parser.add_argument("--mysql-password", default=os.getenv("SPARK_MYSQL_PASSWORD", "123456"))
    parser.add_argument("--mysql-driver", default=os.getenv("SPARK_MYSQL_DRIVER", "com.mysql.cj.jdbc.Driver"))
    parser.add_argument("--write-mode", default=os.getenv("SPARK_WRITE_MODE", "overwrite"))
    return parser.parse_args()


def main():
    args = parse_args()
    spark = SparkSession.builder \
        .appName("NewsDataToMySQL") \
        .config("spark.hadoop.fs.defaultFS", args.hdfs_default_fs) \
        .getOrCreate()

    try:
        print("Reading news JSON from HDFS...")
        df = spark.read.json(args.input_path)

        cleaned_df = df.select(
            md5(col("link")).alias("uniquekey"),
            substring(col("headline"), 0, 500).alias("title"),
            to_timestamp(col("date"), "yyyy-MM-dd").alias("date"),
            substring(col("category"), 0, 50).alias("category"),
            substring(col("authors"), 0, 100).alias("author_name"),
            substring(col("link"), 0, 500).alias("url"),
            lit(None).cast("string").alias("thumbnail_pic_s"),
            lit(None).cast("string").alias("thumbnail_pic_s02"),
            lit(None).cast("string").alias("thumbnail_pic_s03"),
            lit(0).cast("int").alias("is_content")
        ).dropDuplicates(["uniquekey"])

        row_count = cleaned_df.count()
        print(f"Rows after de-duplication: {row_count}")
        cleaned_df.show(5)

        cleaned_df.write.format("jdbc").options(
            url=args.mysql_url,
            driver=args.mysql_driver,
            dbtable=args.mysql_table,
            user=args.mysql_user,
            password=args.mysql_password
        ).mode(args.write_mode).save()

        print("News data was written to MySQL successfully.")

    except Exception as e:
        print(f"Spark job failed: {e}")
        raise

    finally:
        spark.stop()


if __name__ == "__main__":
    main()
