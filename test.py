from pyspark.sql import SparkSession
from pyspark.sql.functions import col, md5, to_timestamp, lit, substring

# 初始化 SparkSession
spark = SparkSession.builder \
    .appName("NewsDataToMySQL") \
    .config("spark.hadoop.fs.defaultFS", "hdfs://namenode:8020") \
    .getOrCreate()

try:
    # 1. 读取 HDFS 原始 JSON 数据
    print(">>> 正在从 HDFS 读取数据...")
    df = spark.read.json("hdfs://namenode:8020/news/raw/news_data.json")

    # 2. 字段清洗与转换
    # 严格匹配你的 MySQL 表结构字段名
    cleaned_df = df.select(
        md5(col("link")).alias("uniquekey"),  # 生成32位MD5主键
        substring(col("headline"), 0, 500).alias("title"),
        to_timestamp(col("date"), "yyyy-MM-dd").alias("date"),
        substring(col("category"), 0, 50).alias("category"),
        substring(col("authors"), 0, 100).alias("author_name"),  # 对齐数据库字段
        substring(col("link"), 0, 500).alias("url"),
        lit(None).cast("string").alias("thumbnail_pic_s"),
        lit(None).cast("string").alias("thumbnail_pic_s02"),
        lit(None).cast("string").alias("thumbnail_pic_s03"),
        lit(0).cast("int").alias("is_content")
    ).dropDuplicates(["uniquekey"])  # 【新增】根据主键去重，解决 Duplicate entry 问题

    # 3. 触发 Action 操作，查看数据状态
    row_count = cleaned_df.count()
    print(f">>> 成功抓取并去重后的数据条数: {row_count}")
    cleaned_df.show(5)

    # 4. 写入 MySQL (使用容器名 mysql8)
    print(">>> 正在尝试连接 mysql8 容器并执行覆盖写入...")
    cleaned_df.write.format("jdbc").options(
        url="jdbc:mysql://mysql8:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true",
        driver="com.mysql.cj.jdbc.Driver",
        dbtable="news",
        user="root",
        password="123456"
    ).mode("overwrite").save()  # 【建议】使用 overwrite 模式清空并重新导入，确保数据纯净

    print(">>> [SUCCESS] 20万条数据已成功入库！")

except Exception as e:
    print(f">>> [ERROR] 运行出错: \n{e}")

finally:
    spark.stop()