<template>
  <div class="chat-page">
    <div class="header">
      <div class="back-btn" @click="goBack()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </div>
      <div class="title">智能问答系统</div>
      <div class="sub">基于新闻数据的智能问答与检索</div>
    </div>

    <div class="chat-container">
      <!-- 对话历史 -->
      <div class="messages-container" ref="messagesRef">
        <div v-if="messages.length === 0" class="welcome-message">
          <el-icon size="48"><ChatDotRound /></el-icon>
          <p>您好！我是您的智能新闻助手</p>
          <p class="hint">请输入您的问题，我将基于新闻数据为您解答</p>
        </div>

        <div v-for="(msg, index) in messages" :key="index" class="message-wrapper">
          <!-- 用户问题 -->
          <div class="message user-message">
            <div class="avatar user-avatar">
              <el-icon><User /></el-icon>
            </div>
            <div class="message-content">
              <div class="message-bubble user-bubble">
                <pre>{{ msg.question }}</pre>
              </div>
              <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
            </div>
          </div>

          <!-- AI回答 -->
          <div v-if="msg.answer" class="message ai-message">
            <div class="avatar ai-avatar">
              <el-icon><Service /></el-icon>
            </div>
            <div class="message-content">
              <div class="message-bubble ai-bubble" v-loading="msg.loading">
                <p v-if="!msg.loading">{{ msg.answer }}</p>
              </div>
              
              <!-- 来源列表 -->
              <div v-if="msg.sources && msg.sources.length > 0 && !msg.loading" class="sources-section">
                <el-divider content-position="left">参考来源</el-divider>
                <div class="sources-list">
                  <div 
                    v-for="(source, sIdx) in msg.sources" 
                    :key="sIdx" 
                    class="source-item"
                  >
                    <div class="source-title">
                      <el-icon size="12"><Document /></el-icon>
                      {{ source.title || '无标题' }}
                    </div>
                    <div class="source-content" v-if="source.content">
                      {{ source.content }}
                    </div>
                  </div>
                </div>
              </div>

              <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
            </div>
          </div>

          <!-- 错误提示 -->
          <div v-if="msg.error" class="error-message">
            <el-alert
              :title="msg.error"
              type="error"
              :closable="false"
              show-icon
            />
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <el-input
          v-model="inputValue"
          type="textarea"
          :rows="3"
          placeholder="请输入您的问题..."
          @keydown.enter="handleSubmit"
          :disabled="isLoading"
          resize="none"
        />
        <div class="input-actions">
          <el-button 
            type="primary" 
            @click="handleSubmit" 
            :loading="isLoading"
            :disabled="!inputValue.trim()"
          >
            <el-icon><Promotion /></el-icon>
            提交问题
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, ChatDotRound, User, Service, Document, Promotion } from '@element-plus/icons-vue'
import { newsApi } from '@/api/news'

const router = useRouter()
const messagesRef = ref(null)
const inputValue = ref('')
const messages = ref([])
const isLoading = ref(false)

const goBack = () => {
  router.push('/')
}

const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

const handleSubmit = async () => {
  const question = inputValue.value.trim()
  if (!question || isLoading.value) return

  // 创建新消息
  const newMessage = {
    question,
    answer: '',
    sources: [],
    timestamp: Date.now(),
    loading: true,
    error: null
  }

  messages.value.push(newMessage)
  inputValue.value = ''
  isLoading.value = true
  scrollToBottom()

  try {
    const response = await newsApi.ragChat(question)

    // 找到最后一条消息并更新
    const lastIndex = messages.value.length - 1
    messages.value[lastIndex].answer = response.answer || '抱歉，我无法回答这个问题'
    messages.value[lastIndex].sources = response.sources || []
    messages.value[lastIndex].loading = false

  } catch (error) {
    console.error('请求失败:', error)
    const lastIndex = messages.value.length - 1
    messages.value[lastIndex].loading = false
    messages.value[lastIndex].error = error.response?.data?.message || error.message || '请求失败，请稍后重试'
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.chat-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: radial-gradient(circle at center, #001529 0%, #000c17 100%);
  overflow: hidden;
}

.header {
  text-align: center;
  padding: 20px;
  border-bottom: 1px solid rgba(0, 210, 255, 0.2);
  position: relative;
}

.back-btn {
  position: absolute;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  color: #00d2ff;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  transition: opacity 0.2s;
}

.back-btn:hover {
  opacity: 0.8;
}

.title {
  font-size: 24px;
  font-weight: 800;
  color: #00d2ff;
  letter-spacing: 2px;
  text-shadow: 0 0 10px rgba(0, 210, 255, 0.45);
}

.sub {
  margin-top: 5px;
  color: rgba(255, 255, 255, 0.75);
  font-size: 13px;
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: 900px;
  width: 100%;
  margin: 0 auto;
  padding: 20px;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: rgba(26, 58, 99, 0.15);
  border: 1px solid rgba(0, 210, 255, 0.15);
  border-radius: 10px;
  margin-bottom: 20px;
}

.welcome-message {
  text-align: center;
  padding: 60px 20px;
  color: rgba(255, 255, 255, 0.7);
}

.welcome-message .el-icon {
  color: #00d2ff;
  margin-bottom: 20px;
}

.welcome-message p {
  margin: 10px 0;
  font-size: 16px;
}

.welcome-message .hint {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.message-wrapper {
  margin-bottom: 20px;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-avatar {
  background: linear-gradient(135deg, #00d2ff 0%, #3a7bd5 100%);
  color: white;
}

.ai-avatar {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  color: white;
}

.message-content {
  flex: 1;
  max-width: calc(100% - 52px);
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  word-break: break-word;
  white-space: pre-wrap;
}

.user-bubble {
  background: linear-gradient(135deg, rgba(0, 210, 255, 0.3) 0%, rgba(58, 123, 213, 0.3) 100%);
  border: 1px solid rgba(0, 210, 255, 0.3);
  color: white;
  display: inline-block;
  max-width: 100%;
}

.user-bubble pre {
  margin: 0;
  font-family: inherit;
  font-size: inherit;
  white-space: pre-wrap;
}

.ai-bubble {
  background: rgba(82, 196, 26, 0.15);
  border: 1px solid rgba(82, 196, 26, 0.3);
  color: white;
  line-height: 1.6;
}

.ai-bubble p {
  margin: 0;
}

.message-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
  margin-top: 5px;
}

.sources-section {
  margin-top: 15px;
}

.sources-section :deep(.el-divider) {
  margin: 10px 0;
}

.sources-section :deep(.el-divider__text) {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
}

.sources-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.source-item {
  background: rgba(26, 58, 99, 0.3);
  border: 1px solid rgba(0, 210, 255, 0.15);
  border-radius: 6px;
  padding: 10px 12px;
}

.source-title {
  color: #00d2ff;
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 5px;
  margin-bottom: 5px;
}

.source-content {
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.error-message {
  margin-top: 10px;
}

.input-area {
  background: rgba(26, 58, 99, 0.2);
  border: 1px solid rgba(0, 210, 255, 0.2);
  border-radius: 10px;
  padding: 15px;
}

.input-area :deep(.el-textarea__inner) {
  background: rgba(26, 58, 99, 0.5);
  border: 1px solid rgba(0, 210, 255, 0.3);
  color: white;
  resize: none;
}

.input-area :deep(.el-textarea__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4);
}

.input-area :deep(.el-textarea__inner:focus) {
  border-color: #00d2ff;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.input-actions .el-button {
  background: linear-gradient(135deg, #00d2ff 0%, #3a7bd5 100%);
  border: none;
}

/* 滚动条样式 */
.messages-container::-webkit-scrollbar {
  width: 6px;
}

.messages-container::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
}

.messages-container::-webkit-scrollbar-thumb {
  background: rgba(0, 210, 255, 0.3);
  border-radius: 3px;
}

.messages-container::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 210, 255, 0.5);
}
</style>
