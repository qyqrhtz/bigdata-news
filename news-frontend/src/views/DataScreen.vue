<template>
  <div class="data-screen">
    <NewsHeader 
      @search-results="handleSearchResults" 
    />
    
    <div class="main-content">
      <el-row :gutter="20">
        <!-- 左侧图表区域 -->
        <el-col :span="7">
          <NewsChart 
            title="新闻热点分类占比" 
            type="pie" 
            :chartData="stats.categories" 
          />
          <NewsChart 
            title="近重复标题占比（SimHash）" 
            type="doughnut" 
            :chartData="analytics.duplicates" 
          />
        </el-col>
        
        <!-- 中间图表和表格区域 -->
        <el-col :span="10">
          <NewsChart 
            title="年度发稿历史溯源" 
            type="line" 
            :chartData="stats.yearTrends" 
            height="300px"
          />
          <div v-if="showSearchResults" class="search-results">
            <div class="search-header">
              <h3>搜索结果</h3>
              <el-button @click="showSearchResults = false" size="small">返回列表</el-button>
            </div>
            <div class="search-list">
              <div 
                v-for="(item, index) in searchResults" 
                :key="index" 
                class="search-item" 
                @click="handleRowClick(item)"
              >
                <h4>{{ item.headline || item.title }}</h4>
                <p class="search-meta">
                  <span>{{ item.category || '未分类' }}</span>
                  <span>{{ item.date || '' }}</span>
                </p>
                <p class="search-description">{{ item.shortDescription || item.description || '无描述' }}</p>
              </div>
              <div v-if="searchResults.length === 0" class="no-results">
                未找到相关结果
              </div>
            </div>
          </div>
          <NewsTable 
            v-else
            @row-click="handleRowClick" 
          />
        </el-col>
        
        <!-- 右侧图表区域 -->
        <el-col :span="7">
          <NewsChart 
            title="作者异常分数 Top10" 
            type="categoryBar" 
            :chartData="analytics.authorAnomalyTop" 
          />
          <NewsChart 
            title="主题总体分布（LDA）" 
            type="bar" 
            :chartData="analytics.topicDistribution" 
          />
        </el-col>
      </el-row>
    </div>
    
    <NewsDetail ref="newsDetailRef" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { newsApi } from '../api/news'
import { analyticsApi } from '../api/analytics'
import NewsHeader from '../components/NewsHeader.vue'
import NewsChart from '../components/NewsChart.vue'
import NewsTable from '../components/NewsTable.vue'
import NewsDetail from '../components/NewsDetail.vue'

const stats = ref({
  categories: [
    { name: '媒体', value: 32.7 },
    { name: '美国新闻', value: 26.3 },
    { name: '健康', value: 18.9 },
    { name: '宗教', value: 14.2 },
    { name: '教育', value: 7.9 }
  ],
  yearTrends: [
    { name: '2020', value: 12478 },
    { name: '2021', value: 15632 },
    { name: '2022', value: 18925 },
    { name: '2023', value: 20456 },
    { name: '2024', value: 22789 }
  ],
  botDetection: [
    { name: '疑似机器人', value: 15.8 },
    { name: '正常账号', value: 84.2 }
  ],
  clickbaitIndex: [
    { name: '媒体', value: 37.2 },
    { name: '美国新闻', value: 45.8 },
    { name: '健康', value: 23.5 },
    { name: '宗教', value: 16.9 },
    { name: '教育', value: 8.7 }
  ],
  density: [
    { name: '高', value: 41.3 },
    { name: '中', value: 34.7 },
    { name: '低', value: 24.0 }
  ]
})

const analytics = ref({
  duplicates: [
    { name: 'Near-duplicate', value: 0 },
    { name: 'Unique', value: 100 }
  ],
  authorAnomalyTop: [],
  topicDistribution: []
})

const newsDetailRef = ref(null)
const selectedCategory = ref('')
const showSearchResults = ref(false)
const searchResults = ref([])

const fetchDashboardStats = async () => {
  try {
    console.log('获取仪表盘数据')
    const data = await newsApi.getDashboardStats()
    if (data) {
      // 只更新有数据的字段，确保有数据才更新
      if (data.categories && data.categories.length > 0) {
        stats.value.categories = data.categories
      }
      if (data.yearTrends && data.yearTrends.length > 0) {
        stats.value.yearTrends = data.yearTrends
      }
      if (data.botDetection && data.botDetection.length > 0) {
        stats.value.botDetection = data.botDetection
      }
      if (data.clickbaitIndex && data.clickbaitIndex.length > 0) {
        stats.value.clickbaitIndex = data.clickbaitIndex
      }
      if (data.density && data.density.length > 0) {
        stats.value.density = data.density
      }
    }
  } catch (error) {
    console.error('获取仪表盘数据失败，使用默认数据:', error)
  }
}

const handleRowClick = (row) => {
  newsDetailRef.value.open(row.uniquekey)
}

const handleFilterChange = (category) => {
  selectedCategory.value = category
  console.log('分类筛选:', category)
  // 分类筛选只影响新闻列表，不影响仪表盘数据
}

const handleSearchResults = (results) => {
  searchResults.value = results
  showSearchResults.value = true
  console.log('搜索结果:', results)
}

const sum = (arr) => arr.reduce((a, b) => a + b, 0)

const fetchAnalytics = async () => {
  try {
    const [topics, anomaly, duplicates] = await Promise.all([
      analyticsApi.getTopics(),
      analyticsApi.getAuthorAnomaly(),
      analyticsApi.getDuplicates()
    ])

    if (duplicates && typeof duplicates.duplicateRate === 'number') {
      const rate = Math.max(0, Math.min(1, duplicates.duplicateRate))
      analytics.value.duplicates = [
        { name: 'Near-duplicate', value: +(rate * 100).toFixed(2) },
        { name: 'Unique', value: +((1 - rate) * 100).toFixed(2) }
      ]
    }

    if (anomaly && Array.isArray(anomaly.items)) {
      analytics.value.authorAnomalyTop = anomaly.items.slice(0, 10).map(i => ({
        name: i.author,
        value: +(Number(i.score || 0)).toFixed(2)
      }))
    }

    // topic distribution: sum each topic's counts across all periods
    const series = topics?.timeSeries?.series
    if (Array.isArray(series)) {
      analytics.value.topicDistribution = series.map(s => ({
        name: s.name,
        value: sum(Array.isArray(s.data) ? s.data.map(Number) : [])
      })).sort((a, b) => b.value - a.value).slice(0, 12)
    }
  } catch (e) {
    console.error('获取 analytics 数据失败:', e)
  }
}

onMounted(() => {
  fetchDashboardStats()
  fetchAnalytics()
})
</script>

<style scoped>
.data-screen {
  width: 100vw;
  height: 100vh;
  background: radial-gradient(circle at center, #001529 0%, #000c17 100%);
  padding: 20px;
  overflow: hidden;
}

.main-content {
  margin-top: 20px;
  height: calc(100vh - 120px);
  overflow: hidden;
}

/* 响应式布局调整 */
@media (max-width: 1200px) {
  .main-content {
    height: auto;
    overflow: auto;
  }
  
  .el-col {
    margin-bottom: 20px;
  }
}

.search-results {
  background: rgba(26, 58, 99, 0.3);
  border: 1px solid rgba(0, 210, 255, 0.2);
  border-radius: 8px;
  padding: 15px;
  height: 400px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 8px rgba(0, 210, 255, 0.1);
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.search-header h3 {
  color: #00d2ff;
  font-size: 16px;
  font-weight: bold;
  margin: 0;
}

.search-list {
  flex: 1;
  overflow-y: auto;
}

.search-item {
  padding: 15px;
  border-bottom: 1px solid rgba(0, 210, 255, 0.2);
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-item:hover {
  background: rgba(0, 210, 255, 0.1);
  border-radius: 4px;
}

.search-item h4 {
  color: #ffffff;
  font-size: 14px;
  font-weight: bold;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.search-meta {
  color: #00d2ff;
  font-size: 12px;
  margin: 0 0 8px 0;
  display: flex;
  gap: 15px;
}

.search-description {
  color: #cccccc;
  font-size: 13px;
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.no-results {
  text-align: center;
  color: #999999;
  padding: 40px 0;
  font-size: 14px;
}

:deep(.el-button) {
  background: rgba(26, 58, 99, 0.5) !important;
  border: 1px solid rgba(0, 210, 255, 0.3) !important;
  color: #00d2ff !important;
}

:deep(.el-button:hover) {
  background: rgba(0, 210, 255, 0.2) !important;
  border-color: #00d2ff !important;
}
</style>
