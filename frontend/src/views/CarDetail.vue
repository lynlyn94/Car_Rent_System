<template>
  <div class="page">
    <NavBar />
    <div class="container page-body">
      <div class="back" @click="router.back()">← 返回</div>
      <div v-if="car" class="detail">
        <div class="left">
          <n-image :src="car.image" object-fit="cover" class="main-img"
            :img-props="{ style: 'width:100%;height:340px;object-fit:cover' }" />
        </div>
        <div class="right">
          <h1 class="title">{{ car.brand }} {{ car.carType }}</h1>
          <p class="plate">{{ car.plateNo }}</p>
          <div class="rent-line">
            <span class="price">¥{{ formatMoney(car.dailyRent) }}</span>
            <span class="unit">/天</span>
            <span class="deposit">押金 ¥{{ formatMoney(car.deposit) }}</span>
          </div>
          <n-tag :type="car.rentStatus === 0 ? 'success' : 'error'" size="large">
            {{ car.rentStatus === 0 ? '可租' : '已出租' }}
          </n-tag>
          <n-descriptions bordered :column="2" label-placement="left" class="params">
            <n-descriptions-item label="品牌">{{ car.brand }}</n-descriptions-item>
            <n-descriptions-item label="类型">{{ car.carType }}</n-descriptions-item>
            <n-descriptions-item label="颜色">{{ car.color }}</n-descriptions-item>
            <n-descriptions-item label="座位数">{{ car.seats }} 座</n-descriptions-item>
            <n-descriptions-item label="维护状态">{{ car.maintainStatus }}</n-descriptions-item>
            <n-descriptions-item label="购入日期">{{ car.purchaseDate }}</n-descriptions-item>
          </n-descriptions>
          <div class="desc">
            <h4>车辆说明</h4>
            <p>{{ car.description || '暂无说明' }}</p>
          </div>
          <div class="actions">
            <n-button type="primary" size="large" :disabled="car.rentStatus === 1" @click="onBook">
              {{ car.rentStatus === 1 ? '该车已出租' : '立即预订' }}
            </n-button>
          </div>
        </div>
      </div>

      <div class="notice">
        <h3>租车须知</h3>
        <ul>
          <li>取车时请携带本人有效身份证件与驾驶证。</li>
          <li>租期不足一天按一天计费，押金在还车验车无误后退还。</li>
          <li>还车时请保持车辆油量、电量与取车时一致。</li>
          <li>如有违章或事故请及时联系门店处理。</li>
        </ul>
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NImage, NTag, NDescriptions, NDescriptionsItem, NButton } from 'naive-ui'
import NavBar from '../components/NavBar.vue'
import AppFooter from '../components/AppFooter.vue'
import { getCarDetail } from '../api'
import { formatMoney } from '../utils/format'
import type { Car } from '../types'

const route = useRoute()
const router = useRouter()
const car = ref<Car | null>(null)

function onBook() {
  if (!car.value) return
  router.push(`/order/confirm?carId=${car.value.id}`)
}

onMounted(async () => {
  const id = Number(route.params.id)
  const res = await getCarDetail(id)
  car.value = res.data
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
.back {
  color: #6b7280;
  cursor: pointer;
  margin-bottom: 14px;
  font-size: 14px;
}
.back:hover {
  color: #2563eb;
}
.detail {
  display: flex;
  gap: 28px;
  padding: 24px;
  border: 1px solid #f0f0f0;
  border-radius: 14px;
}
.left {
  flex: 1;
  min-width: 0;
}
.main-img {
  border-radius: 10px;
  overflow: hidden;
}
.right {
  flex: 1;
}
.title {
  color: #111827;
  font-size: 26px;
  font-weight: 700;
  margin: 0;
}
.plate {
  color: #6b7280;
  margin: 6px 0 14px;
}
.rent-line {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-bottom: 14px;
}
.rent-line .price {
  color: #ea580c;
  font-size: 32px;
  font-weight: 700;
}
.rent-line .unit {
  color: #6b7280;
}
.rent-line .deposit {
  color: #6b7280;
  font-size: 13px;
  margin-left: 10px;
}
.params {
  margin: 16px 0;
}
.desc h4 {
  color: #111827;
  margin: 0 0 6px;
}
.desc p {
  color: #4b5563;
  font-size: 14px;
  line-height: 1.7;
}
.actions {
  margin-top: 20px;
}
.notice {
  margin-top: 16px;
  padding: 20px 24px;
  border: 1px solid #f0f0f0;
  border-radius: 14px;
}
.notice h3 {
  color: #111827;
  margin: 0 0 10px;
}
.notice ul {
  margin: 0;
  padding-left: 20px;
  color: #4b5563;
  line-height: 1.9;
}
</style>
