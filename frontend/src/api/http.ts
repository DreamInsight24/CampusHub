import axios from 'axios'
import { ElMessage } from 'element-plus'

import { getToken } from '@/utils/storage'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
})

http.interceptors.request.use((config) => {
  const token = getToken()

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
    config.headers.token = token
  }

  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error.response?.data?.message || error.message || '请求失败，请稍后重试'
    ElMessage.error(message)
    return Promise.reject(error)
  },
)

export default http
