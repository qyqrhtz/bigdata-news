import request from './request'

export const analyticsApi = {
  recompute() {
    return request.post('/analytics/recompute')
  },

  getTopics() {
    return request.get('/analytics/topics')
  },

  getAuthorAnomaly() {
    return request.get('/analytics/authors/anomaly')
  },

  getDuplicates() {
    return request.get('/analytics/duplicates')
  },

  getBursts() {
    return request.get('/analytics/bursts')
  }
}

