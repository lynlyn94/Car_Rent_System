<template>
  <div class="page">
    <NavBar />
    <div class="container page-body">
      <n-steps :current="step" class="steps">
        <n-step title="选择车辆" />
        <n-step title="填写取还车信息" />
        <n-step title="确认支付" />
      </n-steps>

      <div v-if="car" class="content">
        <div class="car-summary">
          <n-image :src="car.image" object-fit="cover" class="thumb"
            :img-props="{ style: 'width:160px;height:100px;object-fit:cover' }" />
          <div class="car-info">
            <h3>{{ car.brand }} {{ car.carType }}</h3>
            <p class="plate">{{ car.plateNo }} · {{ car.color }} · {{ car.seats }}座</p>
            <p class="price">¥{{ formatMoney(car.dailyRent) }}<span>/天</span></p>
          </div>
        </div>

        <div class="form card">
          <h3 class="form-title">用车信息</h3>
          <n-form label-placement="top">
            <n-grid :cols="2" :x-gap="16">
              <n-form-item-gi label="取车时间">
                <n-date-picker v-model:formatted-value="form.takeTime" type="datetime"
                  value-format="yyyy-MM-dd HH:mm:ss" clearable placeholder="选择取车时间" style="width: 100%" />
              </n-form-item-gi>
              <n-form-item-gi label="还车时间">
                <n-date-picker v-model:formatted-value="form.returnTime" type="datetime"
                  value-format="yyyy-MM-dd HH:mm:ss" clearable placeholder="选择还车时间" style="width: 100%" />
              </n-form-item-gi>
              <n-form-item-gi label="取车地址">
                <n-input v-model:value="form.takeAddr" placeholder="请输入取车地址" />
              </n-form-item-gi>
              <n-form-item-gi label="还车地址">
                <n-input v-model:value="form.returnAddr" placeholder="请输入还车地址" />
              </n-form-item-gi>
            </n-grid>
          </n-form>
        </div>

        <div class="price-summary card">
          <h3 class="form-title">费用明细</h3>
          <div class="line"><span>日租金</span><span>¥{{ formatMoney(car.dailyRent) }} / 天</span></div>
          <div class="line"><span>租期</span><span>{{ days }} 天</span></div>
          <div class="line"><span>租金总额</span><span class="price">¥{{ formatMoney(totalRent) }}</span></div>
          <div class="line"><span>押金（还车退还）</span><span>¥{{ formatMoney(car.deposit) }}</span></div>
          <div class="total-line">
            <span>应付租金</span>
            <span class="price big">¥{{ formatMoney(totalRent) }}</span>
          </div>
          <n-button type="primary" size="large" block :loading="submitting" @click="onSubmit">提交订单</n-button>
        </div>
      </div>
    </div>
    <AppFooter />

    <n-modal v-model:show="payDialog" preset="card" title="确认支付" style="width: 420px"
      :mask-closable="false">
      <div class="pay-amount">
        <p>订单号：{{ orderNo }}</p>
        <p>应付租金：<span class="price big">¥{{ formatMoney(totalRent) }}</span></p>
      </div>
      <n-form label-placement="top">
        <n-form-item label="支付方式">
          <n-radio-group v-model:value="payType">
            <n-radio-button value="微信支付">微信支付</n-radio-button>
            <n-radio-button value="支付宝">支付宝</n-radio-button>
            <n-radio-button value="银行卡">银行卡</n-radio-button>
          </n-radio-group>
        </n-form-item>
      </n-form>
      <template #footer>
        <n-button @click="payDialog = false">稍后支付</n-button>
        <n-button type="primary" :loading="paying" @click="onPay">确认支付</n-button>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  NSteps, NStep, NImage, NForm, NFormItemGi, NGrid, NDatePicker, NInput,
  NButton, NModal, NRadioGroup, NRadioButton, useMessage
} from 'naive-ui'
import NavBar from '../components/NavBar.vue'
import AppFooter from '../components/AppFooter.vue'
import { createOrder, getCarDetail, payOrder } from '../api'
import { useSearchStore } from '../stores/search'
import { formatMoney } from '../utils/format'
import type { Car } from '../types'

const route = useRoute()
const router = useRouter()
const searchStore = useSearchStore()
const message = useMessage()

const car = ref<Car | null>(null)
const step = ref(1)
const submitting = ref(false)
const payDialog = ref(false)
const paying = ref(false)
const orderId = ref(0)
const orderNo = ref('')
const payType = ref('微信支付')

const form = reactive({
  takeTime: null as string | null,
  returnTime: null as string | null,
  takeAddr: '',
  returnAddr: ''
})

const days = computed(() => {
  if (!form.takeTime || !form.returnTime) return 0
  const a = new Date(form.takeTime.replace(' ', 'T')).getTime()
  const b = new Date(form.returnTime.replace(' ', 'T')).getTime()
  if (isNaN(a) || isNaN(b) || b <= a) return 0
  return Math.max(1, Math.ceil((b - a) / (1000 * 60 * 60 * 24)))
})

const totalRent = computed(() => (car.value ? car.value.dailyRent * days.value : 0))

async function onSubmit() {
  if (!form.takeTime || !form.returnTime) {
    message.warning('请选择取车与还车时间')
    return
  }
  if (new Date(form.returnTime.replace(' ', 'T')) <= new Date(form.takeTime.replace(' ', 'T'))) {
    message.warning('还车时间必须晚于取车时间')
    return
  }
  submitting.value = true
  try {
    const res = await createOrder({
      carId: car.value!.id,
      takeTime: form.takeTime,
      returnTime: form.returnTime,
      takeAddr: form.takeAddr,
      returnAddr: form.returnAddr
    })
    orderId.value = res.data.id
    orderNo.value = res.data.orderNo
    step.value = 2
    payDialog.value = true
  } catch {
    // 错误已由拦截器提示
  } finally {
    submitting.value = false
  }
}

async function onPay() {
  paying.value = true
  try {
    await payOrder(orderId.value, payType.value)
    message.success('支付成功')
    payDialog.value = false
    searchStore.clear()
    router.push('/orders')
  } catch {
    // 错误已由拦截器提示
  } finally {
    paying.value = false
  }
}

onMounted(async () => {
  const id = Number(route.query.carId)
  const res = await getCarDetail(id)
  car.value = res.data
  if (searchStore.takeTime) form.takeTime = searchStore.takeTime
  if (searchStore.returnTime) form.returnTime = searchStore.returnTime
  if (searchStore.takeAddr) form.takeAddr = searchStore.takeAddr
  if (searchStore.returnAddr) form.returnAddr = searchStore.returnAddr
})
</script>

<style scoped>
.page {
  background: #fff;
  min-height: 100vh;
}
.page-body {
  margin-top: 24px;
  max-width: 900px;
}
.steps {
  margin-bottom: 28px;
}
.content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.car-summary {
  display: flex;
  gap: 16px;
  padding: 16px;
  align-items: center;
  border: 1px solid #f0f0f0;
  border-radius: 14px;
}
.thumb {
  border-radius: 8px;
  overflow: hidden;
  flex: 0 0 auto;
}
.car-info h3 {
  color: #111827;
  margin: 0 0 4px;
}
.car-info .plate {
  color: #6b7280;
  font-size: 13px;
  margin: 0 0 6px;
}
.car-info .price {
  color: #ea580c;
  font-size: 22px;
  font-weight: 700;
}
.car-info .price span {
  font-size: 13px;
  color: #6b7280;
  font-weight: 400;
}
.card {
  padding: 20px;
  border: 1px solid #f0f0f0;
  border-radius: 14px;
}
.form-title {
  color: #111827;
  margin: 0 0 16px;
}
.line {
  display: flex;
  justify-content: space-between;
  color: #4b5563;
  font-size: 14px;
  padding: 8px 0;
}
.line .price {
  color: #ea580c;
  font-weight: 600;
}
.total-line {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  border-top: 1px dashed #e5e7eb;
  margin: 10px 0 16px;
  padding-top: 12px;
  color: #111827;
  font-weight: 600;
}
.price.big {
  color: #ea580c;
  font-size: 26px;
  font-weight: 700;
}
.pay-amount p {
  margin: 4px 0;
  color: #4b5563;
}
</style>
