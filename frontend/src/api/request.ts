import axios from 'axios'
import { createDiscreteApi } from 'naive-ui'

const { message } = createDiscreteApi(['message'])

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

function handleUnauthorized() {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  localStorage.removeItem('name')
  localStorage.removeItem('role')
  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  }
}

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res && res.code === 200) {
      return res
    }
    if (res && res.code === 401) {
      message.error(res.message || '请先登录')
      handleUnauthorized()
      return Promise.reject(new Error(res.message))
    }
    message.error(res?.message || '请求失败')
    return Promise.reject(new Error(res?.message || '请求失败'))
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      message.error(error.response?.data?.message || '登录已过期，请重新登录')
      handleUnauthorized()
    } else {
      message.error(error.response?.data?.message || error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
