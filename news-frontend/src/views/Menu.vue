<template>
  <div class="menu-page">
    <div class="header">
      <div class="title">社交媒体热点新闻深度溯源系统</div>
      <div class="sub">选择一个分析模块进入查看图表</div>
    </div>

    <el-row :gutter="20" class="grid">
      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="card" shadow="hover" @click="go('/dashboard')">
          <div class="card-title">综合大屏</div>
          <div class="card-desc">原始新闻 + 统计概览 + 深度分析摘要</div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="card" shadow="hover" @click="go('/topics')">
          <div class="card-title">主题建模（LDA）</div>
          <div class="card-desc">主题分布与主题词 Top</div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="card" shadow="hover" @click="go('/authors')">
          <div class="card-title">作者异常画像</div>
          <div class="card-desc">异常分数 Top 作者与原因解释</div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="card" shadow="hover" @click="go('/duplicates')">
          <div class="card-title">重复标题检测</div>
          <div class="card-desc">SimHash 近重复率与重复簇样例</div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="card" shadow="hover" @click="go('/bursts')">
          <div class="card-title">突发检测</div>
          <div class="card-desc">按月滑动 z-score 的突发点榜单</div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="card" shadow="hover" @click="go('/chat')">
          <div class="card-title">智能问答</div>
          <div class="card-desc">基于新闻数据的智能问答与检索</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const go = (path) => router.push(path)
</script>

<style scoped>
.menu-page {
  width: 100vw;
  height: 100vh;
  padding: 24px;
  background: radial-gradient(circle at center, #001529 0%, #000c17 100%);
  overflow: auto;
}

.header {
  text-align: center;
  margin-bottom: 22px;
}

.title {
  font-size: 26px;
  font-weight: 800;
  color: #00d2ff;
  letter-spacing: 2px;
  text-shadow: 0 0 10px rgba(0, 210, 255, 0.45);
}

.sub {
  margin-top: 10px;
  color: rgba(255, 255, 255, 0.75);
  font-size: 14px;
}

.grid {
  max-width: 1100px;
  margin: 0 auto;
}

.card {
  cursor: pointer;
  background: rgba(26, 58, 99, 0.25);
  border: 1px solid rgba(0, 210, 255, 0.18);
  border-radius: 10px;
  margin-bottom: 18px;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(0, 210, 255, 0.12);
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  color: #00d2ff;
  margin-bottom: 8px;
}

.card-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
}
</style>

