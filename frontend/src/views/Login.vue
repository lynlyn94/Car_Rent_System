<template>
  <div class="login-page">
    <div class="login-card">
      <div class="head">
        <div class="logo">🚗 汽车租赁</div>
        <p class="sub">登录您的账号，开启租车之旅</p>
      </div>

      <n-form ref="formRef" :model="form" :rules="rules" label-placement="top">
        <n-form-item label="用户名" path="username">
          <n-input v-model:value="form.username" placeholder="请输入用户名" size="large" />
        </n-form-item>
        <n-form-item label="密码" path="password">
          <n-input v-model:value="form.password" type="password" show-password-on="click"
            placeholder="请输入密码" size="large" @keyup.enter="onSubmit" />
        </n-form-item>
        <n-button type="primary" size="large" block :loading="loading" @click="onSubmit">登 录</n-button>
      </n-form>

      <div class="foot">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
      <div class="admin-entry">
        <router-link to="/admin/login" class="admin-link">管理员登录 →</router-link>
      </div>
      <div class="tip">演示账号：user1 / 123456</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NForm, NFormItem, NInput, NButton, useMessage } from 'naive-ui'
import type { FormInst, FormRules } from 'naive-ui'
import { login } from '../api'
import { useUserStore } from '../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const message = useMessage()

const loading = ref(false)
const formRef = ref<FormInst | null>(null)

const form = reactive({ username: '', password: '' })
const rules: FormRules = {
  username: { required: true, message: '请输入用户名', trigger: ['blur', 'input'] },
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
    const res = await login({ ...form })
    userStore.setLogin(res.data)
    message.success('登录成功')
    router.push((route.query.redirect as string) || '/')
  } catch {
    // 错误已由拦截器提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f9fafb;
  padding: 20px;
}
.login-card {
  width: 400px;
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
.admin-entry {
  text-align: center;
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid #f3f4f6;
}
.admin-link {
  color: #6b7280;
  font-size: 14px;
  text-decoration: none;
}
.admin-link:hover {
  color: #2563eb;
}
.tip {
  text-align: center;
  margin-top: 12px;
  font-size: 12px;
  color: #9ca3af;
}
</style>
