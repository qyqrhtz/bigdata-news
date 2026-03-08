# -*- coding: utf-8 -*-
from pyspark.sql import SparkSession
from pyspark.sql.functions import col, to_date, explode, split
from pyspark.sql.types import StructType, StructField, StringType, DateType, IntegerType

# ========================
# 1️⃣ 创建 SparkSession
# ========================
spark = (
    SparkSession.builder
    .appName("News Analysis")
    .config("spark.sql.shuffle.partitions", "4")
    .config("spark.jars", "/opt/spark/jars/mysql-connector-java-8.0.29.jar")  # 加入 JDBC 驱动路径
    .getOrCreate()
)

# ========================
# 2️⃣ 定义 schema 和读取 HDFS clean 数据
# ========================
# 定义数据的 schema
schema = StructType([
    StructField("source", StringType(), True),
    StructField("date", StringType(), True),
    StructField("category", StringType(), True),
    StructField("title", StringType(), True)
])

# 读取 HDFS clean 数据，使用 schema
df_clean = spark.read.schema(schema).json("hdfs://namenode:8020/news/processed")

# 查看数据的 schema 和前几行数据
df_clean.printSchema()
df_clean.show(5, truncate=False)

# ========================
# 3️⃣ 数据分析
# ========================

# 3.1 新闻来源统计
source_stats = df_clean.groupBy("source").count()
source_stats.show(20)

# 3.2 每日新闻数量
df_clean = df_clean.withColumn("date_only", to_date(col("date")))
date_stats = df_clean.groupBy("date_only").count()
date_stats.show(20)

# 3.3 新闻分类统计
category_stats = df_clean.groupBy("category").count()
category_stats.show(20)

# 3.4 标题关键词统计
words = df_clean.select(explode(split(col("title"), " ")).alias("word"))
word_count = words.groupBy("word").count().orderBy(col("count").desc())
word_count.show(20)

# ========================
# 4️⃣ 写入 MySQL
# ========================
mysql_url = "jdbc:mysql://mysql:3306/news?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
mysql_props = {
    "user": "root",
    "password": "123456",  # 修改为你自己的密码
    "driver": "com.mysql.cj.jdbc.Driver"
}

source_stats.write.mode("overwrite").jdbc(mysql_url, "source_stats", properties=mysql_props)
date_stats.write.mode("overwrite").jdbc(mysql_url, "date_stats", properties=mysql_props)
category_stats.write.mode("overwrite").jdbc(mysql_url, "category_stats", properties=mysql_props)
word_count.write.mode("overwrite").jdbc(mysql_url, "word_count", properties=mysql_props)

# ========================
# 5️⃣ 完成
# ========================
print("✅ 新闻分析完成，结果已写入 MySQL")
spark.stop()