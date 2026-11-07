<template>
  <div>
    <div class="toolbar">
      <n-input v-model:value="query.orderNo" placeholder="订单号" clearable style="width: 180px" />
      <n-input v-model:value="query.customer" placeholder="客户" clearable style="width: 130px" />
      <n-input v-model:value="query.plateNo" placeholder="车牌号" clearable style="width: 130px" />
      <n-select v-model:value="query.status" :options="statusOptions" clearable placeholder="状态" style="width: 120px" />
      <n-date-picker v-model:formatted-value="dateRange" type="daterange" value-format="yyyy-MM-dd"
        clearable start-placeholder="开始日期" end-placeholder="结束日期" style="width: 250px" />
      <n-button type="primary" @click="onSearch">查询</n-button>
      <n-button @click="onReset">重置</n-button>
      <div class="spacer"></div>
      <n-button type="success" @click="onExport">⬇ 导出台账</n-button>
    </div>

    <div class="table-card">
      <n-data-table :columns="columns" :data="orders" :loading="loading" :bordered="false" />
      <div class="pager">
        <n-pagination v-model:page="current" :page-size="size" :item-count="total" @update:page="load" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { h, onMounted, reactive, ref } from 'vue'
import {
  NDataTable, NInput, NSelect, NButton, NPagination, NDatePicker, NTag, NSpace, useMessage, useDialog
} from 'naive-ui'
import { adminGetLedger, adminGetOrders, adminUpdateOrderStatus } from '../../api'
import { formatMoney, ORDER_STATUS, ORDER_STATUS_TYPE } from '../../utils/format'
import type { Order } from '../../types'

const message = useMessage()
const dialog = useDialog()

const orders = ref<Order[]>([])
const total = ref(0)
const current = ref(1)
const size = ref(10)
const loading = ref(false)
const dateRange = ref<[string, string] | null>(null)

const query = reactive({ orderNo: '', customer: '', plateNo: '', status: null as number | null })
const statusOptions = [
  { label: '待支付', value: 0 },
  { label: '租用中', value: 1 },
  { label: '已完成', value: 2 },
  { label: '已取消', value: 3 }
]

function buildParams() {
  return {
    orderNo: query.orderNo || undefined, customer: query.customer || undefined,
    plateNo: query.plateNo || undefined, status: query.status ?? undefined,
    startDate: dateRange.value?.[0], endDate: dateRange.value?.[1]
  }
}

const columns = [
  { title: '订单号', key: 'orderNo', width: 200 },
  {
    title: '车辆', key: 'car', width: 160,
    render: (row: Order) => h('div', [
      h('div', row.carBrand + ' ' + row.carType),
      h('div', { style: 'color:#9ca3af;font-size:12px' }, row.carPlateNo)
    ])
  },
  { title: '客户', key: 'userName', width: 100 },
  { title: '取车时间', key: 'takeTime', width: 160 },
  { title: '还车时间', key: 'returnTime', width: 160 },
  {
    title: '租金', key: 'totalRent', width: 100,
    render: (row: Order) => h('span', { style: 'color:#ea580c;font-weight:600' }, '¥' + formatMoney(row.totalRent))
  },
  {
    title: '状态', key: 'status', width: 90,
    render: (row: Order) => h(NTag, { type: ORDER_STATUS_TYPE[row.status], size: 'small', round: true },
      { default: () => ORDER_STATUS[row.status] })
  },
  { title: '下单时间', key: 'createTime', width: 160 },
  {
    title: '操作', key: 'actions', width: 120,
    render: (row: Order) => h(NSpace, { size: 4 }, { default: () => [
      row.status === 1
        ? h(NButton, { size: 'small', text: true, type: 'success', onClick: () => onComplete(row) }, { default: () => '完成' })
        : null,
      (row.status === 0 || row.status === 1)
        ? h(NButton, { size: 'small', text: true, type: 'error', onClick: () => onCancel(row) }, { default: () => '取消' })
        : null
    ] })
  }
]

async function load(page?: number) {
  if (page !== undefined) current.value = page
  loading.value = true
  try {
    const res = await adminGetOrders({ current: current.value, size: size.value, ...buildParams() })
    orders.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function onSearch() { current.value = 1; load() }
function onReset() { query.orderNo = ''; query.customer = ''; query.plateNo = ''; query.status = null; dateRange.value = null; onSearch() }

async function onComplete(row: Order) {
  const confirmed = await dialog.info({
    title: '完成确认',
    content: `确认订单「${row.orderNo}」已还车完成？`,
    positiveText: '确认完成',
    negativeText: '取消'
  })
  if (!confirmed) return
  await adminUpdateOrderStatus(row.id, 2)
  message.success('订单已完成')
  load()
}

async function onCancel(row: Order) {
  const confirmed = await dialog.warning({
    title: '取消确认',
    content: `确定取消订单「${row.orderNo}」吗？`,
    positiveText: '确定取消',
    negativeText: '再想想'
  })
  if (!confirmed) return
  await adminUpdateOrderStatus(row.id, 3)
  message.success('订单已取消')
  load()
}

function csvCell(v: unknown): string {
  return '"' + String(v ?? '').replace(/"/g, '""') + '"'
}

async function onExport() {
  const res = await adminGetLedger(buildParams())
  const rows = res.data
  const header = ['订单号', '客户', '车牌号', '品牌', '类型', '取车时间', '还车时间', '租金总额', '押金', '支付方式', '状态', '下单时间']
  const lines = rows.map((o) =>
    [o.orderNo, o.userName, o.carPlateNo, o.carBrand, o.carType, o.takeTime, o.returnTime,
      o.totalRent, o.deposit, o.payType, ORDER_STATUS[o.status], o.createTime].map(csvCell).join(','))
  const csv = '﻿' + [header.map(csvCell).join(','), ...lines].join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '车辆订单台账.csv'
  a.click()
  URL.revokeObjectURL(url)
  message.success('台账导出成功')
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 10px;
  padding: 14px;
  margin-bottom: 14px;
  flex-wrap: wrap;
  align-items: center;
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
}
.spacer { flex: 1; }
.table-card {
  padding: 8px 16px 16px;
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
}
.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 14px;
}
</style>
