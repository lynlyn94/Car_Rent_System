<template>
  <div class="page">
    <NavBar />
    <div class="container page-body">
      <h2 class="page-title">个人中心</h2>
      <div class="profile-card">
        <n-form label-placement="top">
          <n-grid :cols="2" :x-gap="16">
            <n-form-item-gi label="用户名">
              <n-input v-model:value="form.username" disabled />
            </n-form-item-gi>
            <n-form-item-gi label="姓名">
              <n-input v-model:value="form.name" placeholder="真实姓名" />
            </n-form-item-gi>
            <n-form-item-gi label="性别">
              <n-select v-model:value="form.gender" :options="genderOptions" />
            </n-form-item-gi>
            <n-form-item-gi label="出生日期">
              <n-date-picker v-model:formatted-value="form.birthDate" type="date" value-format="yyyy-MM-dd" clearable style="width: 100%" />
            </n-form-item-gi>
            <n-form-item-gi label="身份证号">
              <n-input v-model:value="form.idCard" placeholder="身份证号" />
            </n-form-item-gi>
            <n-form-item-gi label="手机号">
              <n-input v-model:value="form.phone" placeholder="手机号" />
            </n-form-item-gi>
            <n-form-item-gi label="驾龄（年）" :span="2">
              <n-input-number v-model:value="form.driveYears" :min="0" :max="60" style="width: 100%" />
            </n-form-item-gi>
          </n-grid>
          <n-button type="primary" :loading="saving" @click="onSave">保存修改</n-button>
        </n-form>
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { NForm, NFormItemGi, NGrid, NInput, NSelect, NDatePicker, NInputNumber, NButton, useMessage } from 'naive-ui'
import NavBar from '../components/NavBar.vue'
import AppFooter from '../components/AppFooter.vue'
import { getMe, updateProfile } from '../api'

const message = useMessage()

const genderOptions = [
  { label: '男', value: 1 },
  { label: '女', value: 0 }
]

const form = reactive({
  username: '',
  name: '',
  gender: 1,
  birthDate: null as string | null,
  idCard: '',
  phone: '',
  driveYears: 0
})
const saving = ref(false)

async function onSave() {
  saving.value = true
  try {
    await updateProfile({
      name: form.name,
      gender: form.gender,
      birthDate: form.birthDate || null,
      idCard: form.idCard,
      phone: form.phone,
      driveYears: form.driveYears
    })
    message.success('保存成功')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  const res = await getMe()
  const u = res.data
  form.username = u.username
  form.name = u.name || ''
  form.gender = u.gender ?? 1
  form.birthDate = u.birthDate || null
  form.idCard = u.idCard || ''
  form.phone = u.phone || ''
  form.driveYears = u.driveYears ?? 0
})
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
  margin: 0 0 16px;
}
.profile-card {
  padding: 24px;
  max-width: 720px;
  border: 1px solid #f0f0f0;
  border-radius: 14px;
}
</style>
