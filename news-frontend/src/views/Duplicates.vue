<template>
  <div class="page">
    <div class="topbar">
      <el-button size="small" @click="back">返回菜单</el-button>
      <div class="title">重复标题检测（SimHash）</div>
      <el-button size="small" type="primary" @click="refresh">刷新</el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="9">
        <NewsChart title="近重复占比" type="doughnut" :chartData="pie" height="320px" />
        <el-card class="card" shadow="never" style="margin-top: 14px;">
          <div class="meta">
            <div><b>documents</b>: {{ meta.documents }}</div>
            <div><b>duplicateDocuments</b>: {{ meta.duplicateDocuments }}</div>
            <div><b>clusters</b>: {{ meta.clusters }}</div>
            <div><b>duplicateRate</b>: {{ meta.duplicateRate }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="15">
        <el-card class="card" shadow="never">
          <div class="card-title">重复簇样例（Top10）</div>
          <el-table :data="clusters" size="small" height="420">
            <el-table-column prop="size" label="簇大小" width="80" />
            <el-table-column label="样例标题">
              <template #default="{ row }">
                <div v-for="s in row.samples" :key="s.title" class="sample">
                  {{ s.title }}
                </div>
              </template>
            </el-table-column>
          </el-table>
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

const pie = ref([
  { name: 'Near-duplicate', value: 0 },
  { name: 'Unique', value: 100 }
])
const meta = ref({ documents: 0, duplicateDocuments: 0, clusters: 0, duplicateRate: 0 })
const clusters = ref([])

const load = async () => {
  const data = await analyticsApi.getDuplicates()
  const rate = typeof data?.duplicateRate === 'number' ? Math.max(0, Math.min(1, data.duplicateRate)) : 0
  pie.value = [
    { name: 'Near-duplicate', value: +(rate * 100).toFixed(2) },
    { name: 'Unique', value: +((1 - rate) * 100).toFixed(2) }
  ]
  meta.value = {
    documents: data?.documents ?? 0,
    duplicateDocuments: data?.duplicateDocuments ?? 0,
    clusters: data?.clusters ?? 0,
    duplicateRate: +(rate * 100).toFixed(2) + '%'
  }
  clusters.value = Array.isArray(data?.topClusters) ? data.topClusters.slice(0, 10) : []
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

.meta {
  display: grid;
  gap: 8px;
  color: rgba(255, 255, 255, 0.85);
  font-size: 13px;
}

.sample {
  padding: 6px 0;
  border-bottom: 1px dashed rgba(255, 255, 255, 0.12);
}
</style>

