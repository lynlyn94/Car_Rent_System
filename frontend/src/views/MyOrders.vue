<template>
  <div class="page">
    <NavBar />
    <div class="container page-body">
      <h2 class="page-title">我的订单</h2>
      <n-tabs v-model:value="statusTab" type="line" @update:value="onTabChange">
        <n-tab-pane name="all" tab="全部" />
        <n-tab-pane name="0" tab="待支付" />
        <n-tab-pane name="1" tab="租用中" />
        <n-tab-pane name="2" tab="已完成" />
        <n-tab-pane name="3" tab="已取消" />
      </n-tabs>

      <div v-if="orders.length" class="order-list">
        <div v-for="o in orders" :key="o.id" class="order-card">
          <div class="order-head">
            <span class="order-no">订单号：{{ o.orderNo }}</span>
            <span class="order-time">下单时间：{{ o.createTime }}</span>
            <n-tag :type="ORDER_STATUS_TYPE[o.status]" size="small" round>{{ ORDER_STATUS[o.status] }}</n-tag>
          </div>
          <div class="order-body">
            <n-image :src="o.carImage" object-fit="cover" class="thumb"
              :img-props="{ style: 'width:140px;height:88px;object-fit:cover' }" />
            <div class="order-info">
              <h4>{{ o.carBrand }} {{ o.carType }}</h4>
              <p>车牌：{{ o.carPlateNo }}</p>
              <p>取车：{{ o.takeTime }} · {{ o.takeAddr || '未填' }}</p>
              <p>还车：{{ o.returnTime }} · {{ o.returnAddr || '未填' }}</p>
            </div>
            <div class="order-amount">
              <p class="price">¥{{ formatMoney(o.totalRent) }}</p>
              <p class="deposit">押金 ¥{{ formatMoney(o.deposit) }}</p>
              <p v-if="o.payType" class="pay-type">{{ o.payType }}</p>
            </div>
            <div class="order-actions">
              <template v-if="o.status === 0">
                <n-button type="primary" size="small" @click="onPay(o)">继续支付</n-button>
                <n-button size="small" @click="onCancel(o)">取消订单</n-button>
              </template>
            </div>
          </div>
        </div>
      </div>
      <n-empty v-else-if="!loading" description="暂无订单" class="empty" />
      <div v-if="total > size" class="pager">
        <n-pagination v-model:page="current" :page-size="size" :item-count="total" @update:page="load" />
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  NTabs, NTabPane, NTag, NImage, NButton, NEmpty, NPagination, useMessage, useDialog
} from 'naive-ui'
import NavBar from '../components/NavBar.vue'
import AppFooter from '../components/AppFooter.vue'
import { cancelOrder, getMyOrders, payOrder } from '../api'
import { formatMoney, ORDER_STATUS, ORDER_STATUS_TYPE } from '../utils/format'
import type { Order } from '../types'

const message = useMessage()
const dialog = useDialog()

const orders = ref<Order[]>([])
const statusTab = ref('all')
const current = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)

async function load(page?: number) {
  if (page !== undefined) current.value = page
  loading.value = true
  try {
    const res = await getMyOrders({
      current: current.value,
      size: size.value,
      status: statusTab.value === 'all' ? undefined : Number(statusTab.value)
    })
    orders.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function onTabChange() {
  current.value = 1
  load()
}

async function onPay(o: Order) {
  const confirmed = await dialog.warning({
    title: '支付确认',
    content: `确认支付 ¥${formatMoney(o.totalRent)} 吗？`,
    positiveText: '确认支付',
    negativeText: '取消'
  })
  if (!confirmed) return
  await payOrder(o.id, '微信支付')
  message.success('支付成功')
  load()
}

async function onCancel(o: Order) {
  const confirmed = await dialog.warning({
    title: '取消订单',
    content: '确定取消该订单吗？',
    positiveText: '确定取消',
    negativeText: '再想想'
  })
  if (!confirmed) return
  await cancelOrder(o.id)
  message.success('订单已取消')
  load()
}

onMounted(load)
</script>

<style scoped>
.page {
  background: #fff;
  min-height: 100vh;
}
.page-body {
  margin-top: 24px;
  min-height: 50vh;
}
.page-title {
  color: #111827;
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 8px;
}
.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.order-card {
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 14px;
}
.order-head {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f3f4f6;
  font-size: 13px;
  color: #6b7280;
}
.order-no {
  font-weight: 600;
  color: #374151;
}
.order-time {
  flex: 1;
}
.order-body {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-top: 12px;
}
.thumb {
  border-radius: 8px;
  overflow: hidden;
  flex: 0 0 auto;
}
.order-info {
  flex: 1;
}
.order-info h4 {
  color: #111827;
  margin: 0 0 6px;
}
.order-info p {
  color: #6b7280;
  font-size: 13px;
  margin: 3px 0;
}
.order-amount {
  text-align: right;
}
.order-amount .price {
  color: #ea580c;
  font-size: 22px;
  font-weight: 700;
}
.order-amount .deposit,
.order-amount .pay-type {
  color: #6b7280;
  font-size: 12px;
  margin: 4px 0 0;
}
.order-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
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
