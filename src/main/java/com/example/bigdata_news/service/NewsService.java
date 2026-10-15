package com.example.bigdata_news.service;

import com.example.bigdata_news.dto.RagSearchResult;
import com.example.bigdata_news.entity.News;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * 社交媒体热点新闻溯源分析服务接口
 * 优化方向：侧重于内容质量分析、异常作者识别及数据集时效性研究
 */
public interface NewsService {

    // --- 基础 CRUD 与分页 ---
    List<News> findAll();

    News findById(String uniquekey);

    News save(News news);

    void deleteById(String uniquekey);

    Page<News> findPage(int page, int size);

    // --- 深度研究维度 (用于 ECharts 可视化) ---

    /** * [研究点] 获取新闻分类占比 (饼图)
     * 用于分析 HuffPost 20万数据中各领域的分布情况
     */
    List<Map<String, Object>> getCategoryStats();

    /** * [研究点] 获取年度发布趋势 (折线图)
     * 代替原来的 7 日趋势，因为 V3 数据集跨度数年，分析年度趋势更有溯源价值
     */
    List<Map<String, Object>> getYearTrends();

    /** * [研究点] 获取发布量 Top 10 作者 (柱状图)
     */
    List<Map<String, Object>> getAuthorStats();

    /** * [研究点] 跨领域作者分析 (异常行为识别)
     * 识别一人跨多个互不相关领域发稿的情况，用于识别疑似自动抓取账号或机构号
     */
    List<Map<String, Object>> getCrossDomainAuthorStats();

    /** * [研究点] 各分类标题党指数 (分析标题长度分布)
     * 研究不同新闻领域在吸引点击方面的行为差异
     */
    List<Map<String, Object>> getClickbaitIndexStats();

    /** * [研究点] 内容信息密度统计 (分析摘要长度)
     * 统计长短摘要占比，分析 20w 数据中深度报道与碎片化消息的比例
     */
    List<Map<String, Object>> getDescriptionDensityStats();

    /** * [核心] 获取全局仪表盘聚合数据 (一次性获取所有维度)
     * 对应前端 DataScreen.vue 的初始化请求
     */
    Map<String, Object> getGlobalDashboardStats();
    /**
     * 对接 Python Flask 实现的语义搜索 (RAG)
     * @param query 用户输入的关键词
     * @param topK 返回匹配度最高的条数
     */
    List<RagSearchResult> semanticSearch(String query, int topK);
}
