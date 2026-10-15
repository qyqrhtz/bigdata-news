<template>
  <el-dialog v-model="visible" title="新闻深度溯源详情" width="50%">
    <div v-if="newsDetail" class="detail-content">
      <h3 class="detail-title">{{ newsDetail.headline || newsDetail.title }}</h3>
      
      <div class="detail-meta">
        <el-tag size="small">{{ newsDetail.category || '政治/社会' }}</el-tag>
        <span>日期: {{ newsDetail.date || '2017-2018' }}</span>
        <span>作者: {{ newsDetail.authorName || 'Huffington Post' }}</span>
      </div>
      
      <el-divider>摘要 / 溯源链接</el-divider>
      <div class="detail-description">
        {{ newsDetail.shortDescription || '该条目是通过向量语义匹配检索出的高度相关新闻。' }}
      </div>
      
      <div class="detail-link">
        <el-link :href="newsDetail.link || newsDetail.url" target="_blank" type="primary">
          前往原始媒体查看
        </el-link>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { newsApi } from '../api/news'

const visible = ref(false)
const newsDetail = ref(null)

const open = async (id) => {
  try {
    const news = await newsApi.getNewsById(id)
    newsDetail.value = news
    visible.value = true
  } catch (error) {
    console.error('获取新闻详情失败:', error)
  }
}

defineExpose({
  open
})
</script>

<style scoped>
.detail-content {
  padding: 10px;
}

.detail-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.detail-meta {
  margin-bottom: 15px;
  font-size: 14px;
  color: #666;
}

.detail-meta span {
  margin-right: 15px;
}

.detail-description {
  margin-bottom: 15px;
  line-height: 1.5;
  color: #333;
}

.detail-link {
  margin-top: 15px;
}
</style>