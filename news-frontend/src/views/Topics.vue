<template>
  <div class="page">
    <div class="topbar">
      <el-button size="small" @click="back">返回菜单</el-button>
      <div class="title">主题建模（LDA）</div>
      <el-button size="small" type="primary" @click="refresh">刷新</el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="10">
        <NewsChart title="主题总体分布（Top12）" type="bar" :chartData="topicDistribution" height="360px" />
      </el-col>
      <el-col :span="14">
        <el-card class="card" shadow="never">
          <div class="card-title">主题关键词（Top12）</div>
          <div v-if="topicWords.length === 0" class="empty">暂无数据</div>
          <div v-else class="topics">
            <div v-for="t in topicWords" :key="t.topic" class="topic">
              <div class="topic-name">Topic {{ t.topic }}</div>
              <div class="topic-words">
                <el-tag v-for="w in t.words" :key="w.word" size="small" class="tag">{{ w.word }}</el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NewsChart from '../components/NewsChart.vue'
import { analyticsApi } from '../api/analytics'

const router = useRouter()
const back = () => router.push('/')

const topicDistribution = ref([])
const topicWords = ref([])

const sum = (arr) => arr.reduce((a, b) => a + b, 0)

const load = async () => {
  const topics = await analyticsApi.getTopics()
  const series = topics?.timeSeries?.series
  if (Array.isArray(series)) {
    topicDistribution.value = series.map(s => ({
      name: s.name,
      value: sum(Array.isArray(s.data) ? s.data.map(Number) : [])
    })).sort((a, b) => b.value - a.value).slice(0, 12)
  }
  if (Array.isArray(topics?.topicWords)) {
    topicWords.value = topics.topicWords
  }
}

const refresh = () => load()

onMounted(load)
</script>

<style scoped>
.page {
  width: 100vw;
  height: 100vh;
  padding: 18px 20px;
  background: radial-gradient(circle at center, #001529 0%, #000c17 100%);
  overflow: auto;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.title {
  font-size: 18px;
  font-weight: 800;
  color: #00d2ff;
  letter-spacing: 1px;
}

.card {
  background: rgba(26, 58, 99, 0.25);
  border: 1px solid rgba(0, 210, 255, 0.18);
}

.card-title {
  font-size: 14px;
  font-weight: 700;
  color: #00d2ff;
  margin-bottom: 10px;
}

.topics {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.topic {
  padding: 10px;
  border: 1px solid rgba(0, 210, 255, 0.12);
  border-radius: 8px;
  background: rgba(0, 12, 23, 0.25);
}

.topic-name {
  font-weight: 700;
  margin-bottom: 8px;
}

.topic-words {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag {
  border-color: rgba(0, 210, 255, 0.25);
}

.empty {
  color: rgba(255, 255, 255, 0.7);
}
</style>

