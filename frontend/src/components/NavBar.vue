<template>
  <header class="navbar">
    <div class="container nav-inner">
      <div class="logo" @click="router.push('/')">🚗 <span>汽车租赁</span></div>
      <nav class="nav-links">
        <router-link to="/">首页</router-link>
        <router-link to="/cars">车辆列表</router-link>
      </nav>
      <div class="nav-right">
        <template v-if="!userStore.isLogin">
          <n-button quaternary @click="router.push('/login')">登录</n-button>
          <n-button type="primary" @click="router.push('/register')">注册</n-button>
        </template>
        <n-dropdown v-else trigger="hover" :options="userOptions" @select="onCommand">
          <div class="user">
            <n-avatar round :size="30" color="#2563EB">{{ userStore.displayName.charAt(0) }}</n-avatar>
            <span class="uname">{{ userStore.displayName }}</span>
          </div>
        </n-dropdown>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NDropdown, NAvatar, useMessage } from 'naive-ui'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const message = useMessage()

const userOptions = computed(() => {
  const opts: any[] = [
    { label: '我的订单', key: 'orders' },
    { label: '留言反馈', key: 'messages' },
    { label: '个人中心', key: 'profile' }
  ]
  if (userStore.isAdmin) opts.push({ label: '管理后台', key: 'admin' })
  opts.push({ type: 'divider', key: 'divider' }, { label: '退出登录', key: 'logout' })
  return opts
})

function onCommand(key: string) {
  if (key === 'logout') {
    userStore.logout()
    message.success('已退出登录')
    router.push('/')
    return
  }
  if (key === 'admin') {
    router.push('/admin/dashboard')
    return
  }
  router.push('/' + key)
}
</script>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: saturate(180%) blur(14px);
  -webkit-backdrop-filter: saturate(180%) blur(14px);
  border-bottom: 1px solid #f0f0f0;
}
.nav-inner {
  display: flex;
  align-items: center;
  height: 64px;
}
.logo {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-right: 40px;
}
.logo span {
  color: #2563eb;
}
.nav-links {
  display: flex;
  gap: 28px;
  flex: 1;
}
.nav-links a {
  color: #4b5563;
  font-size: 14px;
  cursor: pointer;
  padding: 4px 0;
  border-bottom: 2px solid transparent;
}
.nav-links a:hover {
  color: #111827;
}
.nav-links a.router-link-active {
  color: #2563eb;
  font-weight: 600;
  border-bottom-color: #2563eb;
}
.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}
.uname {
  font-size: 14px;
  color: #111827;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
