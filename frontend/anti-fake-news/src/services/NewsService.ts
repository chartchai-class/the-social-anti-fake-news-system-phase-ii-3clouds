import axios from 'axios'

const BACKEND_URL = import.meta.env.VITE_BACKEND_URL // http://localhost:8080

const apiClient = axios.create({
  baseURL: `${BACKEND_URL}`,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
  timeout: 30000, // 30 วินาที
})

export default {
  getNews() {
    return apiClient.get('/api/news')
  },
  getNewsById(id: number) {
    return apiClient.get(`/api/news/${id}`)
  },

}
