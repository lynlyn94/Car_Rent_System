<template>
  <div>
    <div class="stat-grid">
      <div v-for="s in statCards" :key="s.label" class="stat-card">
        <div class="stat-icon" :style="{ background: s.bg }">{{ s.icon }}</div>
        <div>
          <div class="stat-value">{{ s.value }}</div>
          <div class="stat-label">{{ s.label }}</div>
        </div>
      </div>
    </div>

    <div class="chart-row">
      <div class="chart-card">
        <h4>近 7 日订单趋势</h4>
        <div ref="trendRef" class="chart"></div>
      </div>
      <div class="chart-card">
        <h4>车辆品牌分布</h4>
        <div ref="brandRef" class="chart"></div>
      </div>
    </div>

    <div class="chart-card">
      <h4>热门车型 Top5</h4>
      <div ref="hotRef" class="chart"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref } from 'vue'
import * as echarts from 'echarts'
import { adminGetStats } from '../../api'
import { formatMoney } from '../../utils/format'

const trendRef = ref<HTMLElement>()
const brandRef = ref<HTMLElement>()
const hotRef = ref<HTMLElement>()

const statCards = ref<{ label: string; value: string; icon: string; bg: string }[]>([])

let charts: echarts.ECharts[] = []

function renderCharts(data: Record<string, any>) {
  const trend = data.orderTrend || []
  const trendChart = echarts.init(trendRef.value!)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: trend.map((t: any) => t.date) },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{
      type: 'line',
      smooth: true,
      data: trend.map((t: any) => t.count),
      areaStyle: { opacity: 0.12 },
      itemStyle: { color: '#2563EB' }
    }]
  })

  const brand = data.brandDistribution || []
  const brandChart = echarts.init(brandRef.value!)
  brandChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, type: 'scroll' },
    series: [{
      type: 'pie',
      radius: ['40%', '65%'],
      data: brand,
      itemStyle: { borderRadius: 6 }
    }]
  })

  const hot = data.hotModels || []
  const hotChart = echarts.init(hotRef.value!)
  hotChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 30, bottom: 60 },
    xAxis: { type: 'category', data: hot.map((h: any) => h.name), axisLabel: { interval: 0 } },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{
      type: 'bar',
      data: hot.map((h: any) => h.value),
      itemStyle: { color: '#EA580C', borderRadius: [6, 6, 0, 0] }
    }]
  })

  charts = [trendChart, brandChart, hotChart]
}

function onResize() {
  charts.forEach((c) => c.resize())
}

onMounted(async () => {
  const res = await adminGetStats()
  const d = res.data as Record<string, any>
  statCards.value = [
    { label: '车辆总数', value: String(d.totalCars ?? 0), icon: '🚗', bg: '#EFF6FF' },
    { label: '可租车辆', value: String(d.availableCars ?? 0), icon: '✅', bg: '#ECFDF5' },
    { label: '客户总数', value: String(d.totalUsers ?? 0), icon: '👥', bg: '#FFF7ED' },
    { label: '订单总数', value: String(d.totalOrders ?? 0), icon: '📋', bg: '#F5F3FF' },
    { label: '累计营收(元)', value: formatMoney(d.revenue as number), icon: '💰', bg: '#FDF2F8' }
  ]
  renderCharts(d)
  window.addEventListener('resize', onResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  charts.forEach((c) => c.dispose())
})
</script>

<style scoped>
.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}
.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}
.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
}
.stat-label {
  font-size: 12px;
  color: #6b7280;
}
.chart-row {
  display: grid;
  grid-template-columns: 7fr 5fr;
  gap: 16px;
  margin-bottom: 16px;
}
.chart-card {
  padding: 16px;
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
}
.chart-card h4 {
  color: #111827;
  margin: 0 0 8px;
}
.chart {
  height: 300px;
}
</style>
