import argparse
import requests
import json
import os
from datetime import datetime


def parse_args():
    parser = argparse.ArgumentParser(description="Fetch news data from Juhe Toutiao API.")
    parser.add_argument("--appkey", default=os.getenv("JUHE_APPKEY", ""), help="Juhe API key. Can also use JUHE_APPKEY.")
    parser.add_argument(
        "--news-type",
        default=os.getenv("NEWS_TYPE", "guonei"),
        choices=["top", "guonei", "guoji", "keji", "caijing"],
        help="News category to fetch.",
    )
    parser.add_argument("--output-dir", default=os.getenv("NEWS_OUTPUT_DIR", "news_data"), help="Output directory.")
    return parser.parse_args()


def fetch_news(appkey, news_type, output_dir):
    if not appkey:
        raise ValueError("Juhe APPKEY is required. Set JUHE_APPKEY or pass --appkey.")

    url = "http://v.juhe.cn/toutiao/index"

    params = {
        "key": appkey,
        "type": news_type
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
        if not os.path.exists(output_dir):
            os.makedirs(output_dir)

        # 生成带时间戳文件名
        filename = f"{news_type}_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
        filepath = os.path.join(output_dir, filename)

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
    args = parse_args()
    fetch_news(args.appkey, args.news_type, args.output_dir)
