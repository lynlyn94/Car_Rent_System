<template>
  <div class="page">
    <NavBar />
    <div class="container page-body">
      <div v-if="hasSearch" class="search-summary">
        <span>📍 取车：{{ searchStore.takeAddr || '不限' }} <template v-if="searchStore.takeTime">{{ searchStore.takeTime }}</template></span>
        <span>📍 还车：{{ searchStore.returnAddr || '不限' }} <template v-if="searchStore.returnTime">{{ searchStore.returnTime }}</template></span>
      </div>

      <div class="layout">
        <aside class="filter">
          <h3 class="filter-title">筛选条件</h3>
          <div class="filter-item">
            <label>品牌</label>
            <n-select v-model:value="filter.brand" :options="brandOptions" clearable placeholder="全部品牌" />
          </div>
          <div class="filter-item">
            <label>类型</label>
            <n-select v-model:value="filter.carType" :options="typeOptions" clearable placeholder="全部类型" />
          </div>
          <div class="filter-item">
            <label>日租金（元）</label>
            <div class="price-range">
              <n-input-number v-model:value="filter.minRent" :min="0" :step="50" clearable placeholder="最低" style="flex:1" />
              <span class="dash">—</span>
              <n-input-number v-model:value="filter.maxRent" :min="0" :step="50" clearable placeholder="最高" style="flex:1" />
            </div>
          </div>
          <div class="filter-item">
            <label>座位数</label>
            <n-select v-model:value="filter.seats" :options="seatOptions" clearable placeholder="不限" />
          </div>
          <div class="filter-item">
            <label>出租状态</label>
            <n-select v-model:value="filter.rentStatus" :options="statusOptions" clearable placeholder="全部" />
          </div>
          <div class="filter-btns">
            <n-button type="primary" block @click="search">查询</n-button>
            <n-button block @click="reset">重置</n-button>
          </div>
        </aside>

        <div class="main">
          <div v-if="loading" class="car-grid">
            <n-skeleton v-for="i in 6" :key="i" height="280px" :sharp="false" />
          </div>
          <div v-else-if="cars.length" class="car-grid">
            <CarCard v-for="car in cars" :key="car.id" :car="car" />
          </div>
          <n-empty v-else description="没有符合条件的车辆" class="empty" />
          <div v-if="!loading && total > size" class="pager">
            <n-pagination v-model:page="current" :page-size="size" :item-count="total" @update:page="load" />
          </div>
        </div>
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { NSelect, NInputNumber, NButton, NSkeleton, NEmpty, NPagination } from 'naive-ui'
import NavBar from '../components/NavBar.vue'
import CarCard from '../components/CarCard.vue'
import AppFooter from '../components/AppFooter.vue'
import { getBrands, getCarTypes, getCars } from '../api'
import { useSearchStore } from '../stores/search'
import type { Car } from '../types'

const searchStore = useSearchStore()

const brandOptions = ref<{ label: string; value: string }[]>([])
const typeOptions = ref<{ label: string; value: string }[]>([])
const seatOptions = [2, 4, 5, 7].map((s) => ({ label: s + ' 座', value: s }))
const statusOptions = [
  { label: '可租', value: 0 },
  { label: '已出租', value: 1 }
]

const cars = ref<Car[]>([])
const total = ref(0)
const current = ref(1)
const size = ref(8)
const loading = ref(false)

const filter = reactive({
  brand: null as string | null,
  carType: null as string | null,
  minRent: null as number | null,
  maxRent: null as number | null,
  seats: null as number | null,
  rentStatus: null as number | null
})

const hasSearch = computed(() => !!(searchStore.takeAddr || searchStore.returnAddr))

async function load(page?: number) {
  if (page !== undefined) current.value = page
  loading.value = true
  try {
    const res = await getCars({
      current: current.value,
      size: size.value,
      brand: filter.brand || undefined,
      carType: filter.carType || undefined,
      minRent: filter.minRent ?? undefined,
      maxRent: filter.maxRent ?? undefined,
      seats: filter.seats ?? undefined,
      rentStatus: filter.rentStatus ?? undefined
    })
    cars.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function search() {
  current.value = 1
  load()
}

function reset() {
  filter.brand = null
  filter.carType = null
  filter.minRent = null
  filter.maxRent = null
  filter.seats = null
  filter.rentStatus = null
  search()
}

onMounted(async () => {
  const [b, t] = await Promise.all([getBrands(), getCarTypes()])
  brandOptions.value = b.data.map((x) => ({ label: x, value: x }))
  typeOptions.value = t.data.map((x) => ({ label: x, value: x }))
  load()
})
</script>

<style scoped>
.page {
  background: #fff;
  min-height: 100vh;
}
.page-body {
  margin-top: 24px;
}
.search-summary {
  display: flex;
  gap: 32px;
  padding: 12px 16px;
  margin-bottom: 16px;
  color: #4b5563;
  font-size: 13px;
  background: #eff6ff;
  border-radius: 12px;
}
.layout {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}
.filter {
  width: 240px;
  flex: 0 0 240px;
  padding: 18px;
  border: 1px solid #f0f0f0;
  border-radius: 14px;
}
.filter-title {
  color: #111827;
  font-size: 16px;
  margin: 0 0 16px;
}
.filter-item {
  margin-bottom: 16px;
}
.filter-item label {
  display: block;
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 6px;
}
.price-range {
  display: flex;
  align-items: center;
  gap: 6px;
}
.dash {
  color: #9ca3af;
}
.filter-btns {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.main {
  flex: 1;
  min-width: 0;
}
.car-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 18px;
}
.empty {
  padding: 60px 0;
}
.pager {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
