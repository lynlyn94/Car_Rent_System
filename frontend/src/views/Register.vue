<template>
  <div class="register-page">
    <div class="register-card">
      <div class="head">
        <div class="logo">🚗 汽车租赁</div>
        <p class="sub">注册账号，开启租车之旅</p>
      </div>

      <n-form ref="formRef" :model="form" :rules="rules" label-placement="top">
        <n-grid :cols="2" :x-gap="16">
          <n-form-item-gi label="用户名" path="username">
            <n-input v-model:value="form.username" placeholder="登录用户名" />
          </n-form-item-gi>
          <n-form-item-gi label="姓名" path="name">
            <n-input v-model:value="form.name" placeholder="真实姓名" />
          </n-form-item-gi>
          <n-form-item-gi label="密码" path="password">
            <n-input v-model:value="form.password" type="password" show-password-on="click" placeholder="登录密码" />
          </n-form-item-gi>
          <n-form-item-gi label="确认密码" path="confirm">
            <n-input v-model:value="form.confirm" type="password" show-password-on="click" placeholder="再次输入密码" />
          </n-form-item-gi>
          <n-form-item-gi label="性别">
            <n-select v-model:value="form.gender" :options="genderOptions" />
          </n-form-item-gi>
          <n-form-item-gi label="出生日期">
            <n-date-picker v-model:formatted-value="form.birthDate" type="date" value-format="yyyy-MM-dd" clearable placeholder="选择日期" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="身份证号">
            <n-input v-model:value="form.idCard" placeholder="18 位身份证号" />
          </n-form-item-gi>
          <n-form-item-gi label="手机号">
            <n-input v-model:value="form.phone" placeholder="手机号" />
          </n-form-item-gi>
          <n-form-item-gi label="驾龄（年）" :span="2">
            <n-input-number v-model:value="form.driveYears" :min="0" :max="60" style="width: 100%" />
          </n-form-item-gi>
        </n-grid>
        <n-button type="primary" size="large" block :loading="loading" @click="onSubmit">注 册</n-button>
      </n-form>

      <div class="foot">
        已有账号？<router-link to="/login">去登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  NForm, NFormItemGi, NGrid, NInput, NSelect, NDatePicker, NInputNumber, NButton, useMessage
} from 'naive-ui'
import type { FormInst, FormRules } from 'naive-ui'
import { register } from '../api'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const message = useMessage()
const formRef = ref<FormInst | null>(null)
const loading = ref(false)

const genderOptions = [
  { label: '男', value: 1 },
  { label: '女', value: 0 }
]

const form = reactive({
  username: '',
  password: '',
  confirm: '',
  name: '',
  gender: 1,
  birthDate: null as string | null,
  idCard: '',
  phone: '',
  driveYears: 0
})

const rules: FormRules = {
  username: { required: true, message: '请输入用户名', trigger: ['blur', 'input'] },
  name: { required: true, message: '请输入姓名', trigger: ['blur', 'input'] },
  password: { required: true, message: '请输入密码', trigger: ['blur', 'input'] },
  confirm: {
    required: true,
    trigger: ['blur', 'input'],
    validator: (_rule, value: string) => {
      if (!value) return new Error('请再次输入密码')
      if (value !== form.password) return new Error('两次输入的密码不一致')
      return true
    }
  }
}

async function onSubmit() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    const { confirm, ...data } = form
    const res = await register({ ...data, birthDate: form.birthDate || null })
    userStore.setLogin(res.data)
    message.success('注册成功')
    router.push('/')
  } catch {
    // 错误已由拦截器提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f9fafb;
  padding: 20px;
}
.register-card {
  width: 560px;
  padding: 36px 32px;
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 16px;
  box-shadow: 0 12px 40px rgba(17, 24, 39, 0.06);
}
.head {
  text-align: center;
  margin-bottom: 20px;
}
.logo {
  font-size: 24px;
  font-weight: 700;
  color: #2563eb;
}
.sub {
  color: #6b7280;
  font-size: 13px;
  margin: 8px 0 0;
}
.foot {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #6b7280;
}
.foot a {
  color: #2563eb;
}
</style>
