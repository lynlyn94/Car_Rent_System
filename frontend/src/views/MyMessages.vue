<template>
  <div class="page">
    <NavBar />
    <div class="container page-body">
      <h2 class="page-title">留言反馈</h2>

      <div class="submit-card">
        <h4>提交留言</h4>
        <n-input v-model:value="content" type="textarea" :rows="4" maxlength="500" show-count
          placeholder="请输入您的意见或建议，我们会尽快回复" />
        <div class="submit-btn">
          <n-button type="primary" :loading="submitting" @click="onSubmit">提交留言</n-button>
        </div>
      </div>

      <div v-if="messages.length" class="msg-list">
        <div v-for="m in messages" :key="m.id" class="msg-card">
          <div class="msg-content">
            <div class="msg-head"><span class="time">{{ m.createTime }}</span></div>
            <p>{{ m.content }}</p>
          </div>
          <div v-if="m.reply" class="msg-reply">
            <div class="reply-head">
              <span class="admin-tag">管理员回复</span>
              <span class="time">{{ m.replyTime }}</span>
            </div>
            <p>{{ m.reply }}</p>
          </div>
          <div v-else class="msg-reply pending">暂无回复，敬请等待</div>
        </div>
      </div>
      <n-empty v-else-if="!loading" description="暂无留言" class="empty" />
      <div v-if="total > size" class="pager">
        <n-pagination v-model:page="current" :page-size="size" :item-count="total" @update:page="load" />
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { NInput, NButton, NEmpty, NPagination, useMessage } from 'naive-ui'
import NavBar from '../components/NavBar.vue'
import AppFooter from '../components/AppFooter.vue'
import { getMyMessages, submitMessage } from '../api'
import type { Message } from '../types'

const message = useMessage()

const messages = ref<Message[]>([])
const content = ref('')
const submitting = ref(false)
const current = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)

async function load(page?: number) {
  if (page !== undefined) current.value = page
  loading.value = true
  try {
    const res = await getMyMessages({ current: current.value, size: size.value })
    messages.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function onSubmit() {
  if (!content.value.trim()) {
    message.warning('请输入留言内容')
    return
  }
  submitting.value = true
  try {
    await submitMessage(content.value.trim())
    message.success('留言提交成功')
    content.value = ''
    current.value = 1
    load()
  } finally {
    submitting.value = false
  }
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
  margin: 0 0 16px;
}
.submit-card {
  padding: 20px;
  margin-bottom: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 14px;
}
.submit-card h4 {
  color: #111827;
  margin: 0 0 12px;
}
.submit-btn {
  text-align: right;
  margin-top: 12px;
}
.msg-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.msg-card {
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 14px;
}
.msg-head,
.reply-head {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 6px;
}
.msg-content p {
  color: #111827;
  margin: 0;
  line-height: 1.7;
}
.msg-reply {
  margin-top: 12px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 10px;
}
.admin-tag {
  color: #2563eb;
  font-weight: 600;
}
.msg-reply p {
  color: #4b5563;
  margin: 0;
  line-height: 1.7;
}
.msg-reply.pending {
  color: #9ca3af;
  font-size: 13px;
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
