import requests
import json
import os
from datetime import datetime

# ==========================
# 1️⃣ 在这里填写你的 APPKEY
# ==========================
APPKEY = "b29df05926434ac8096ec7777c6190ce"

# 新闻类型：
# top(头条) / guonei(国内) / guoji(国际) / keji(科技) / caijing(财经)
NEWS_TYPE = "guonei"

# 保存目录
OUTPUT_DIR = "news_data"

# ==========================


def fetch_news():
    url = "http://v.juhe.cn/toutiao/index"

    params = {
        "key": APPKEY,
        "type": NEWS_TYPE
    }

    try:
        print("正在请求新闻数据...")
        response = requests.get(url, params=params, timeout=10)
        response.raise_for_status()

        data = response.json()

        if data.get("error_code") != 0:
            print("接口返回错误：", data.get("reason"))
            return

        # 创建目录
        if not os.path.exists(OUTPUT_DIR):
            os.makedirs(OUTPUT_DIR)

        # 生成带时间戳文件名
        filename = f"{NEWS_TYPE}_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
        filepath = os.path.join(OUTPUT_DIR, filename)

        # 添加抓取时间
        data["_fetched_at"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

        with open(filepath, "w", encoding="utf-8") as f:
            json.dump(data, f, ensure_ascii=False, indent=2)

        print("抓取成功！")
        print("保存路径：", filepath)
        print("新闻条数：", len(data["result"]["data"]))

    except Exception as e:
        print("抓取失败：", e)


if __name__ == "__main__":
    fetch_news()
