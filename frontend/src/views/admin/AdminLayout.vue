<template>
  <n-layout has-sider class="admin">
    <n-layout-sider bordered :width="220" class="sider">
      <div class="logo">🚗 汽车租赁管理</div>
      <n-menu :value="route.path" :options="menuOptions" @update:value="onMenuSelect" />
    </n-layout-sider>
    <n-layout>
      <n-layout-header bordered class="header">
        <span class="title">{{ pageTitle }}</span>
        <div class="right">
          <router-link to="/" class="home-link">回到前台</router-link>
          <n-button text @click="onLogout">退出登录</n-button>
        </div>
      </n-layout-header>
      <n-layout-content class="main">
        <router-view />
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NLayout, NLayoutSider, NLayoutHeader, NLayoutContent, NMenu, NButton } from 'naive-ui'
import { useUserStore } from '../../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const menuOptions = [
  { label: '📊 数据看板', key: '/admin/dashboard' },
  { label: '🚗 车辆管理', key: '/admin/cars' },
  { label: '👥 客户管理', key: '/admin/customers' },
  { label: '📋 订单管理', key: '/admin/orders' },
  { label: '💬 留言管理', key: '/admin/messages' }
]

const titles: Record<string, string> = {
  '/admin/dashboard': '数据看板',
  '/admin/cars': '车辆信息管理',
  '/admin/customers': '客户信息管理',
  '/admin/orders': '订单管理与台账',
  '/admin/messages': '留言管理'
}

const pageTitle = computed(() => titles[route.path] || '管理后台')

function onMenuSelect(key: string) {
  router.push(key)
}

function onLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.admin {
  height: 100vh;
}
.sider {
  background: #fff;
}
.logo {
  color: #2563eb;
  font-size: 17px;
  font-weight: 700;
  padding: 20px 16px;
  border-bottom: 1px solid #f3f4f6;
}
.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px;
}
.title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}
.right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.home-link {
  color: #6b7280;
  font-size: 13px;
  text-decoration: none;
}
.home-link:hover {
  color: #2563eb;
}
.main {
  background: #f9fafb;
  padding: 24px;
}
</style>
