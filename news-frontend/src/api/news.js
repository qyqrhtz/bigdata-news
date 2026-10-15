import request from './request'

export const newsApi = {
  semanticSearch(query, topK = 10) {
    return request.get('/news/search', {
      params: { query, topK }
    })
  },
  getDashboardStats() {
    return request.get('/news/stats/dashboard')
  },

  ragChat(question) {
    return request.post('/news/rag/chat', { question })
  },

  getNewsById(id) {
    return request.get(`/news/${id}`)
  },

  getNewsPage(page, size = 10) {
    return request.get('/news/page', {
      params: { page: page - 1, size }
    })
  }
}
