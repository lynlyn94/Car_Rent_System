<template>
  <div class="admin-login-page">
    <div class="login-card">
      <div class="head">
        <div class="badge">管理后台</div>
        <div class="logo">🚗 汽车租赁管理系统</div>
        <p class="sub">请使用管理员账号登录</p>
      </div>

      <n-form ref="formRef" :model="form" :rules="rules" label-placement="top">
        <n-form-item label="管理员账号" path="username">
          <n-input v-model:value="form.username" placeholder="请输入管理员用户名" size="large" />
        </n-form-item>
        <n-form-item label="密码" path="password">
          <n-input v-model:value="form.password" type="password" show-password-on="click"
            placeholder="请输入密码" size="large" @keyup.enter="onSubmit" />
        </n-form-item>
        <n-button type="primary" size="large" block :loading="loading" @click="onSubmit">登 录</n-button>
      </n-form>

      <div class="foot">
        <router-link to="/login">← 返回用户登录</router-link>
      </div>
      <div class="tip">演示管理员：admin / admin123</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { NForm, NFormItem, NInput, NButton, useMessage } from 'naive-ui'
import type { FormInst, FormRules } from 'naive-ui'
import { adminLogin } from '../api'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const message = useMessage()

const loading = ref(false)
const formRef = ref<FormInst | null>(null)

const form = reactive({ username: '', password: '' })
const rules: FormRules = {
  username: { required: true, message: '请输入管理员用户名', trigger: ['blur', 'input'] },
  password: { required: true, message: '请输入密码', trigger: ['blur', 'input'] }
}

async function onSubmit() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    const res = await adminLogin({ ...form })
    userStore.setLogin(res.data)
    message.success('登录成功')
    router.push('/admin/dashboard')
  } catch {
    // 错误已由拦截器提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, #1f2937 0%, #111827 100%);
  padding: 20px;
}
.login-card {
  width: 400px;
  padding: 36px 32px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.35);
}
.head {
  text-align: center;
  margin-bottom: 20px;
}
.badge {
  display: inline-block;
  background: #111827;
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.05em;
  padding: 4px 12px;
  border-radius: 999px;
  margin-bottom: 12px;
}
.logo {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
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
}
.foot a {
  color: #2563eb;
}
.tip {
  text-align: center;
  margin-top: 10px;
  font-size: 12px;
  color: #9ca3af;
}
</style>
