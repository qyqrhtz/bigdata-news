import requests
url = "https://feed.mix.sina.com.cn/api/ro;;/get"

params = {
     "pageid":"153",
     "lid":"2510",
     "k":"",
     "num":20,
     "page":1
 }

headers = {
     "User-Agent":"Mozilla/5.0"
 }

response = requests.get(url, params=params, headers=headers)

print(response.text)