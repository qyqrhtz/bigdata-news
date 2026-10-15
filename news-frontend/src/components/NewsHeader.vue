<template>
  <div class="news-header">
    <h1 class="header-title">社交媒体热点新闻深度溯源系统</h1>
    <div class="search-container">
      <el-input
        v-model="searchQuery"
        placeholder="输入关键词进行语义搜索"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch" type="primary">
            <el-icon><Search /></el-icon>
          </el-button>
        </template>
      </el-input>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { newsApi } from '../api/news'

const searchQuery = ref('')
const emit = defineEmits(['search-results'])

const handleSearch = async () => {
  if (!searchQuery.value.trim()) return
  
  try {
    const results = await newsApi.semanticSearch(searchQuery.value)
    emit('search-results', results)
  } catch (error) {
    console.error('搜索失败:', error)
  }
}
</script>

<style scoped>
.news-header {
  text-align: center;
  padding: 20px 0;
  background: linear-gradient(135deg, #001529 0%, #000c17 100%);
  border-bottom: 1px solid rgba(0, 210, 255, 0.2);
}

.header-title {
  font-size: 24px;
  font-weight: bold;
  color: #00d2ff;
  margin-bottom: 15px;
  text-shadow: 0 0 10px rgba(0, 210, 255, 0.5);
  letter-spacing: 2px;
}

.search-container {
  width: 600px;
  margin: 0 auto 20px;
}

:deep(.el-input__wrapper) {
  background: rgba(26, 58, 99, 0.5) !important;
  border: 1px solid rgba(0, 210, 255, 0.3) !important;
  box-shadow: 0 0 10px rgba(0, 210, 255, 0.1) inset !important;
}

:deep(.el-input__placeholder) {
  color: rgba(0, 210, 255, 0.5) !important;
}

:deep(.el-input__inner) {
  color: #00d2ff !important;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #00d2ff 0%, #3a7bd5 100%) !important;
  border: none !important;
  box-shadow: 0 0 10px rgba(0, 210, 255, 0.5) !important;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #3a7bd5 0%, #00d2ff 100%) !important;
  box-shadow: 0 0 15px rgba(0, 210, 255, 0.7) !important;
}
</style>
