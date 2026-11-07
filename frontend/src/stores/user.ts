import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { LoginVO } from '../types'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const id = ref(Number(localStorage.getItem('userId') || 0))
  const username = ref(localStorage.getItem('username') || '')
  const name = ref(localStorage.getItem('name') || '')
  const role = ref(localStorage.getItem('role') || '')

  const isLogin = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'ADMIN')
  const displayName = computed(() => name.value || username.value || '用户')

  function setLogin(vo: LoginVO) {
    token.value = vo.token
    id.value = vo.id
    username.value = vo.username
    name.value = vo.name || ''
    role.value = vo.role
    localStorage.setItem('token', vo.token)
    localStorage.setItem('userId', String(vo.id))
    localStorage.setItem('username', vo.username)
    localStorage.setItem('name', vo.name || '')
    localStorage.setItem('role', vo.role)
  }

  function logout() {
    token.value = ''
    id.value = 0
    username.value = ''
    name.value = ''
    role.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('name')
    localStorage.removeItem('role')
  }

  return { token, id, username, name, role, isLogin, isAdmin, displayName, setLogin, logout }
})
