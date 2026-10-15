<template>
  <div class="page">
    <div class="topbar">
      <el-button size="small" @click="back">返回菜单</el-button>
      <div class="title">作者异常画像</div>
      <el-button size="small" type="primary" @click="refresh">刷新</el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="10">
        <NewsChart title="异常分数 Top10" type="bar" :chartData="top10" height="360px" />
      </el-col>
      <el-col :span="14">
        <el-card class="card" shadow="never">
          <div class="card-title">Top10 详情（原因解释）</div>
          <el-table :data="top10Rows" size="small" height="360">
            <el-table-column prop="author" label="作者" min-width="220" />
            <el-table-column prop="score" label="分数" width="90" />
            <el-table-column prop="posts" label="发稿" width="70" />
            <el-table-column prop="categoriesCovered" label="分类数" width="80" />
            <el-table-column label="原因" min-width="240">
              <template #default="{ row }">
                <span class="reason" v-for="r in row.reasons" :key="r.feature">
                  {{ r.feature }}
                </span>
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

const top10 = ref([])
const top10Rows = ref([])

const load = async () => {
  const data = await analyticsApi.getAuthorAnomaly()
  const items = Array.isArray(data?.items) ? data.items : []
  top10Rows.value = items.slice(0, 10)
  top10.value = top10Rows.value.map(i => ({
    name: i.author,
    value: +(Number(i.score || 0)).toFixed(2)
  }))
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

.reason {
  display: inline-block;
  margin-right: 8px;
  color: rgba(255, 255, 255, 0.8);
}
</style>

