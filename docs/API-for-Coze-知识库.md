# 社交媒体热点新闻深度溯源系统 - 后端 API 说明

本文档描述后端提供的全部 HTTP 接口，便于对接与问答。服务默认端口：8081。若部署到服务器，请将下文中的 `http://localhost:8081` 替换为实际域名或 IP。

---

## 一、新闻与统计（/api/news、/api/stats）

### 1. 综合统计（大屏用）
- **方法**：GET  
- **地址**：`http://localhost:8081/api/news/stats/dashboard`  
- **说明**：一次性返回分类占比、年度趋势、作者统计、跨领域作者、标题长度、信息密度等，供前端大屏展示。  
- **参数**：无  
- **返回**：JSON 对象，包含 categories、yearTrends、authors、botDetection、clickbaitIndex、density 等字段。

### 2. 统计大屏（备用）
- **方法**：GET  
- **地址**：`http://localhost:8081/api/stats/dashboard`  
- **说明**：与上一接口数据一致，同为全局统计聚合。  
- **参数**：无  

### 3. 分页新闻列表
- **方法**：GET  
- **地址**：`http://localhost:8081/api/news/page`  
- **参数**：  
  - `page`（可选，默认 0）：页码  
  - `size`（可选，默认 10）：每页条数  
- **示例**：`/api/news/page?page=0&size=20`  
- **返回**：分页对象，content 为新闻列表，每项包含 uniquekey、title、date、category、authorName、url 等。

### 4. 单条新闻详情
- **方法**：GET  
- **地址**：`http://localhost:8081/api/news/{id}`  
- **说明**：id 为新闻的 uniquekey。  
- **示例**：`/api/news/00045ba3706d32d693d9c2de6ea8a937`  

### 5. 删除单条新闻
- **方法**：DELETE  
- **地址**：`http://localhost:8081/api/news/{id}`  
- **说明**：id 为新闻的 uniquekey。  

---

## 二、深度分析（/api/analytics）

以下接口返回的数据来自离线计算并缓存在 analytics_result 表中。若从未执行过“重算”，部分接口可能返回提示“请先调用 POST /api/analytics/recompute”。

### 6. 主题建模（LDA）
- **方法**：GET  
- **地址**：`http://localhost:8081/api/analytics/topics`  
- **说明**：基于新闻标题的 LDA 主题结果，包含 topicWords（每主题 Top 词）、timeSeries（主题随月份变化）、topicCategoryHeatmap（主题-分类热力数据）。  

### 7. 作者异常画像
- **方法**：GET  
- **地址**：`http://localhost:8081/api/analytics/authors/anomaly`  
- **说明**：按行为特征计算的异常分数 Top 作者，含 score、posts、categoriesCovered、reasons 等。  

### 8. 重复标题检测
- **方法**：GET  
- **地址**：`http://localhost:8081/api/analytics/duplicates`  
- **说明**：SimHash 近重复统计，含 duplicateRate、topClusters（重复簇及样例标题）等。  

### 9. 突发检测
- **方法**：GET  
- **地址**：`http://localhost:8081/api/analytics/bursts`  
- **说明**：按月滑动 z-score 的突发点，含 categoryBursts、topicBursts 等。  

### 10. 重算全部分析（离线任务）
- **方法**：POST  
- **地址**：`http://localhost:8081/api/analytics/recompute`  
- **说明**：触发一次全量重算（主题、异常作者、重复、突发），结果写入缓存表。耗时较长，适合定时任务或手动触发。  
- **请求体**：无或空 JSON 均可  
- **返回**：包含 updatedAt、keys（如 topics_v1、author_anomaly_v1、duplicates_v1、bursts_v1）。  

---

## 三、知识检索（/api/knowledge）

供 Coze 工作流或 Bot 调用，基于 news 表做关键词检索。

### 12. 知识检索
- **方法**：GET  
- **地址**：`http://localhost:8081/api/knowledge/search`  
- **参数**：  
  - `query`（必填）：关键词，对标题、作者做模糊匹配  
  - `topK`（可选，默认 5）：返回条数，最大 50  
  - `category`（可选）：按分类过滤  
- **示例**：`/api/knowledge/search?query=Trump&topK=5&category=POLITICS`  
- **返回**：`{ "total": number, "results": [ News... ], "query": "..." }`，每条 News 含 uniquekey、title、date、category、authorName、url 等（无 content 字段）。

### 13. 知识详情
- **方法**：GET  
- **地址**：`http://localhost:8081/api/knowledge/{id}`  
- **说明**：id 为 news 的 uniquekey。

---

## 四、健康检查

### 14. 根路径
- **方法**：GET  
- **地址**：`http://localhost:8081/`  
- **说明**：用于检查服务是否启动，返回一段文本如 "Spring Boot is running!"。  

---

## 使用说明（给 Coze / 调用方）

- 除特别说明外，均为 GET 请求，浏览器或 curl 直接访问即可。  
- 若前端跨域访问，后端已配置 CORS，可直接从浏览器前端请求上述地址。  
- 深度分析接口（/api/analytics/*）返回的 JSON 体积可能较大，适合给前端做图表或给 Bot 做摘要回答，不建议逐字朗读给用户。

---

## 五、Coze 工作流配置示例（知识检索 + 大模型回答）

### 步骤 2：在 Coze 创建工作流

1. 进入 **Coze → 资源库 → 工作流 → 创建工作流**。  
2. **命名**：`website_knowledge_search`。  
3. **工作流结构**：

```text
开始节点 (query, category?)
    ↓
HTTP 请求节点
    ↓
大模型节点
    ↓
结束节点
```

4. **HTTP 请求节点配置**  
   - 方法：`GET`  
   - URL：`http://localhost:8081/api/knowledge/search?query={{start.query}}&topK=5&category={{start.category}}`  
   - 若部署到服务器，将 `localhost:8081` 改为实际域名或 IP。

5. **大模型节点配置**  
   - 提示词（已按本系统返回结构适配：results 中为新闻列表，无 content，有 title、category、authorName、date 等）：

```text
# 角色
你是基于检索结果回答问题的助手。

## 上下文
以下是从知识库检索到的内容（JSON）：
{{http_request.body}}

## 用户问题
{{start.query}}

## 要求
1. 仅根据上下文的 results 字段回答，每条记录包含 title、category、authorName、date、url 等（无 content 字段）。
2. 若找到相关内容，按条总结：标题(title)、分类(category)、作者(authorName)、日期(date)，可附带 url。
3. 若 results 为空或 total 为 0，明确告知「知识库中未找到相关内容」。
4. 引用时注明分类信息。
```

6. **结束节点**：输出大模型节点的回复即可。
