<template>
  <div class="car-card" @click="goDetail">
    <div class="img-wrap">
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
        <n-button size="small" type="primary" secondary @click.stop="goDetail">查看详情</n-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { NImage, NTag, NButton } from 'naive-ui'
import type { Car } from '../types'
import { formatMoney } from '../utils/format'

const props = defineProps<{ car: Car }>()
const router = useRouter()

function goDetail() {
  router.push(`/cars/${props.car.id}`)
}
</script>

<style scoped>
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
.img-wrap {
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
</style>
