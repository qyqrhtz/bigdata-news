<template>
  <div class="page">
    <div class="topbar">
      <el-button size="small" @click="back">返回菜单</el-button>
      <div class="title">突发检测（按月 z-score）</div>
      <el-button size="small" type="primary" @click="refresh">刷新</el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="10">
        <NewsChart title="分类突发 Top20（z）" type="bar" :chartData="categoryTop20" height="360px" />
      </el-col>
      <el-col :span="14">
        <el-card class="card" shadow="never">
          <div class="card-title">突发点明细（Top30）</div>
          <el-table :data="categoryRows" size="small" height="360">
            <el-table-column prop="name" label="对象" min-width="160" />
            <el-table-column prop="period" label="月份" width="90" />
            <el-table-column prop="count" label="计数" width="70" />
            <el-table-column prop="z" label="z" width="90" />
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

const categoryTop20 = ref([])
const categoryRows = ref([])

const load = async () => {
  const data = await analyticsApi.getBursts()
  const rows = Array.isArray(data?.categoryBursts) ? data.categoryBursts : []
  categoryRows.value = rows.slice(0, 30).map(r => ({
    name: `${r.name} @ ${r.period}`,
    period: r.period,
    count: r.count,
    z: +(Number(r.z || 0)).toFixed(2)
  }))
  categoryTop20.value = rows.slice(0, 20).map(r => ({
    name: `${r.name} @ ${r.period}`,
    value: +(Number(r.z || 0)).toFixed(2)
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
</style>

