from pyspark.sql import SparkSession
from pyspark.sql import Row

# 创建 SparkSession
spark = SparkSession.builder \
    .appName("Spark MySQL Write Test") \
    .config("spark.jars", "/path/to/mysql-connector-java-8.0.23.jar") \  # 需要将 MySQL JDBC 驱动路径指定到这里
    .getOrCreate()

# 创建一些测试数据
data = [
    Row(id=1, name="Alice", age=30),
    Row(id=2, name="Bob", age=25),
    Row(id=3, name="Charlie", age=35)
]

# 创建 DataFrame
df = spark.createDataFrame(data)

# MySQL 数据库连接配置
url = "jdbc:mysql://localhost:3306/test_db"  # 替换为你的 MySQL 数据库 URL
properties = {
    "user": "root",  # 替换为你的 MySQL 用户
    "password": "123456",  # 替换为你的密码
    "driver": "com.mysql.cj.jdbc.Driver"
}

# 将 DataFrame 写入 MySQL
df.write.jdbc(url=url, table="people", mode="overwrite", properties=properties)

# 读取写入的数据，验证是否成功
result_df = spark.read.jdbc(url=url, table="people", properties=properties)
result_df.show()

# 停止 Spark 会话
spark.stop()