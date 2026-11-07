<template>
  <div class="home">
      <!-- 导航栏 -->
      <header class="nav">
        <div class="container nav-inner">
          <div class="logo" @click="go('/')">🚗 <span>汽车租赁</span></div>
          <nav class="links">
            <a :class="{ active: route.path === '/' }" @click="go('/')">首页</a>
            <a :class="{ active: route.path === '/cars' }" @click="go('/cars')">车辆列表</a>
          </nav>
          <div class="actions">
            <template v-if="!userStore.isLogin">
              <n-button quaternary @click="go('/login')">登录</n-button>
              <n-button type="primary" @click="go('/register')">注册</n-button>
            </template>
            <n-dropdown v-else trigger="hover" :options="userOptions" @select="onUserSelect">
              <div class="user">
                <n-avatar round :size="30" color="#2563EB">{{ userStore.displayName.charAt(0) }}</n-avatar>
                <span class="uname">{{ userStore.displayName }}</span>
              </div>
            </n-dropdown>
          </div>
        </div>
      </header>

      <!-- 首屏 -->
      <section class="hero">
        <div class="container">
          <h1>轻松租车，说走就走</h1>
          <p class="sub">多品牌车型任选 · 在线预订支付 · 取还车灵活</p>
          <div class="search-card">
            <div class="search-item">
              <label>取车地点</label>
              <n-input v-model:value="form.takeAddr" placeholder="如：北京首都机场" />
            </div>
            <div class="search-item">
              <label>取车时间</label>
              <n-date-picker v-model:formatted-value="form.takeTime" type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss" clearable placeholder="选择取车时间" style="width: 100%" />
            </div>
            <div class="search-item">
              <label>还车地点</label>
              <n-input v-model:value="form.returnAddr" placeholder="如：北京南站" />
            </div>
            <div class="search-item">
              <label>还车时间</label>
              <n-date-picker v-model:formatted-value="form.returnTime" type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss" clearable placeholder="选择还车时间" style="width: 100%" />
            </div>
            <n-button type="primary" size="large" class="search-btn" @click="onSearch">立即搜索</n-button>
          </div>
        </div>
      </section>

      <!-- 热门车型 -->
      <section class="container section">
        <div class="section-head">
          <h2>热门车型推荐</h2>
          <a class="more" @click="go('/cars')">查看全部 →</a>
        </div>
        <div v-if="loading" class="car-grid">
          <n-skeleton v-for="i in 8" :key="i" height="280px" :sharp="false" />
        </div>
        <div v-else class="car-grid">
          <div v-for="car in hotCars" :key="car.id" class="car-card" @click="go(`/cars/${car.id}`)">
            <div class="car-img-wrap">
              <n-image :src="car.image" object-fit="cover"
                :img-props="{ style: 'width:100%;height:160px;object-fit:cover;display:block' }" />
              <n-tag :type="car.rentStatus === 0 ? 'success' : 'error'" size="small" round class="status-tag">
                {{ car.rentStatus === 0 ? '可租' : '已出租' }}
              </n-tag>
            </div>
            <div class="car-body">
              <div class="car-title">
                <span class="brand">{{ car.brand }} {{ car.carType }}</span>
                <span class="plate">{{ car.plateNo }}</span>
              </div>
              <div class="car-meta">{{ car.color }} · {{ car.seats }}座</div>
              <div class="car-footer">
                <div class="rent"><span class="price">¥{{ formatMoney(car.dailyRent) }}</span><span class="unit">/天</span></div>
                <n-button size="small" type="primary" secondary @click.stop="go(`/cars/${car.id}`)">查看详情</n-button>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 服务保障 -->
      <section class="container section">
        <div class="feature-grid">
          <div class="feat"><span class="feat-icon">🛡️</span><b>安全保障</b><p>车辆定期维护，全程保障</p></div>
          <div class="feat"><span class="feat-icon">⚡</span><b>在线支付</b><p>下单即付，流程便捷</p></div>
          <div class="feat"><span class="feat-icon">📍</span><b>灵活取还</b><p>支持异地取还车</p></div>
        </div>
      </section>

      <!-- 页脚 -->
      <footer class="footer">
        <div class="container">
          <p>汽车租赁管理系统 · 软件工程课程设计</p>
          <p class="foot-sub">在线选车 · 预订支付 · 订单台账 · 留言反馈</p>
        </div>
      </footer>
    </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  NButton, NInput, NDatePicker, NDropdown,
  NAvatar, NImage, NTag, NSkeleton
} from 'naive-ui'
import { getCars } from '../api'
import { useSearchStore } from '../stores/search'
import { useUserStore } from '../stores/user'
import { formatMoney } from '../utils/format'
import type { Car } from '../types'

const router = useRouter()
const route = useRoute()
const searchStore = useSearchStore()
const userStore = useUserStore()

const form = reactive({
  takeAddr: '',
  returnAddr: '',
  takeTime: null as string | null,
  returnTime: null as string | null
})
const hotCars = ref<Car[]>([])
const loading = ref(true)

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

function go(path: string) {
  router.push(path)
}

function onSearch() {
  searchStore.set({
    takeAddr: form.takeAddr,
    returnAddr: form.returnAddr,
    takeTime: form.takeTime || '',
    returnTime: form.returnTime || ''
  })
  router.push('/cars')
}

function onUserSelect(key: string) {
  if (key === 'logout') {
    userStore.logout()
    router.push('/')
    return
  }
  if (key === 'admin') {
    router.push('/admin/dashboard')
    return
  }
  router.push('/' + key)
}

onMounted(async () => {
  try {
    const res = await getCars({ current: 1, size: 8, rentStatus: 0 })
    hotCars.value = res.data.records
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.home {
  background: #fff;
  min-height: 100vh;
}
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 导航 */
.nav {
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
.links {
  display: flex;
  gap: 28px;
  flex: 1;
}
.links a {
  color: #4b5563;
  font-size: 14px;
  cursor: pointer;
  padding: 4px 0;
  border-bottom: 2px solid transparent;
}
.links a:hover {
  color: #111827;
}
.links a.active {
  color: #2563eb;
  font-weight: 600;
  border-bottom-color: #2563eb;
}
.actions {
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
}

/* 首屏 */
.hero {
  padding: 64px 0 72px;
}
.hero h1 {
  font-size: 44px;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: #111827;
  margin: 0 0 14px;
}
.sub {
  color: #6b7280;
  font-size: 17px;
  margin: 0 0 36px;
}
.search-card {
  display: flex;
  gap: 14px;
  align-items: flex-end;
  padding: 22px;
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 16px;
  box-shadow: 0 12px 40px rgba(17, 24, 39, 0.07);
}
.search-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 7px;
}
.search-item label {
  font-size: 13px;
  color: #6b7280;
}
.search-btn {
  height: 40px;
  flex: 0 0 auto;
}

/* 区块 */
.section {
  margin-top: 44px;
}
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 22px;
}
.section-head h2 {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: #111827;
  margin: 0;
}
.more {
  color: #2563eb;
  font-size: 14px;
  cursor: pointer;
}

/* 车辆网格 */
.car-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 22px;
}
.car-card {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
}
.car-card:hover {
  box-shadow: 0 16px 40px rgba(17, 24, 39, 0.1);
  transform: translateY(-4px);
}
.car-img-wrap {
  position: relative;
  height: 160px;
  background: #f3f4f6;
}
.status-tag {
  position: absolute;
  top: 10px;
  left: 10px;
}
.car-body {
  padding: 14px 16px 16px;
}
.car-title {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 8px;
}
.brand {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}
.plate {
  font-size: 12px;
  color: #9ca3af;
  white-space: nowrap;
}
.car-meta {
  color: #6b7280;
  font-size: 13px;
  margin: 8px 0 14px;
}
.car-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.rent .price {
  color: #ea580c;
  font-size: 22px;
  font-weight: 700;
}
.rent .unit {
  color: #9ca3af;
  font-size: 12px;
  margin-left: 2px;
}

/* 服务保障 */
.feature-grid {
  display: flex;
  gap: 22px;
}
.feat {
  flex: 1;
  background: #f9fafb;
  border-radius: 14px;
  padding: 26px 24px;
  text-align: center;
}
.feat-icon {
  font-size: 32px;
}
.feat b {
  display: block;
  color: #111827;
  margin: 12px 0 6px;
  font-size: 15px;
}
.feat p {
  margin: 0;
  color: #6b7280;
  font-size: 13px;
}

/* 页脚 */
.footer {
  border-top: 1px solid #f0f0f0;
  padding: 28px 0;
  text-align: center;
  color: #9ca3af;
  font-size: 13px;
  margin-top: 56px;
}
.foot-sub {
  margin-top: 4px;
  font-size: 12px;
}
</style>
