<template>
  <div class="table-container">
    <el-table
      :data="newsList"
      style="width: 100%"
      @row-click="handleRowClick"
      size="small"
      :header-cell-style="headerCellStyle"
      :cell-style="cellStyle"
    >
      <el-table-column prop="title" label="新闻标题" show-overflow-tooltip min-width="200" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="authorName" label="作者" width="120" />
      <el-table-column prop="date" label="发布日期" width="120" />
    </el-table>
    
    <el-pagination
      v-if="total > 0"
      layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      :current-page="currentPage"
      @current-change="handlePageChange"
      class="pagination"
      background
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { newsApi } from '../api/news'

const emit = defineEmits(['row-click'])

const newsList = ref([])
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(1)

const headerCellStyle = {
  backgroundColor: 'rgba(26, 49, 58, 0.8)',
  color: '#00d2ff',
  fontWeight: 'bold',
  borderBottom: '1px solid rgba(0, 210, 255, 0.2)'
}

const cellStyle = {
  backgroundColor: 'rgba(0, 28, 55, 0.5)',
  color: '#ffffff',
  borderBottom: '1px solid rgba(26, 58, 99, 0.3)'
}

const fetchNews = async (page) => {
  try {
    console.log('开始获取第', page, '页数据，每页', pageSize.value, '条')
    const response = await newsApi.getNewsPage(page, pageSize.value)
    console.log('后端返回数据:', response)
    console.log('总数据量:', response.totalElements)
    console.log('当前页数据量:', response.content.length)
    newsList.value = response.content
    total.value = response.totalElements
    currentPage.value = page
  } catch (error) {
    console.error('获取新闻列表失败:', error)
  }
}

const handlePageChange = (page) => {
  fetchNews(page)
}

const handleRowClick = (row) => {
  emit('row-click', row)
}

onMounted(() => {
  fetchNews(1)
})
</script>

<style scoped>
.table-container {
  background: rgba(26, 58, 99, 0.3);
  border: 1px solid rgba(0, 210, 255, 0.2);
  border-radius: 8px;
  padding: 15px;
  height: 400px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 8px rgba(0, 210, 255, 0.1);
}

:deep(.el-table) {
  flex: 1;
  overflow: auto;
}

:deep(.el-table__body tr:hover > td) {
  background-color: rgba(0, 210, 255, 0.1) !important;
  cursor: pointer;
}

.pagination {
  margin-top: 15px;
  display: flex;
  justify-content: center;
}

:deep(.el-pagination__button) {
  background: rgba(26, 58, 99, 0.5) !important;
  border: 1px solid rgba(0, 210, 255, 0.3) !important;
  color: #00d2ff !important;
}

:deep(.el-pagination__button:hover) {
  background: rgba(0, 210, 255, 0.2) !important;
}

:deep(.el-pagination__current) {
  background: #00d2ff !important;
  border-color: #00d2ff !important;
  color: #000c17 !important;
}
</style>
