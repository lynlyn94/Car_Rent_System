<template>
  <div>
    <div class="toolbar">
      <n-select v-model:value="status" :options="statusOptions" clearable placeholder="状态" style="width: 140px" @update:value="onSearch" />
    </div>

    <div class="table-card">
      <n-data-table :columns="columns" :data="messages" :loading="loading" :bordered="false" />
      <div class="pager">
        <n-pagination v-model:page="current" :page-size="size" :item-count="total" @update:page="load" />
      </div>
    </div>

    <n-modal v-model:show="showDialog" title="回复留言" preset="card" style="width: 520px">
      <div class="origin">
        <p class="label">用户留言：</p>
        <p class="content">{{ currentMessage?.content }}</p>
      </div>
      <n-input v-model:value="replyText" type="textarea" :rows="4" placeholder="请输入回复内容" />
      <template #footer>
        <n-button @click="showDialog = false">取消</n-button>
        <n-button type="primary" :loading="saving" @click="onSave">回复</n-button>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { h, onMounted, ref } from 'vue'
import { NDataTable, NSelect, NPagination, NModal, NInput, NButton, NTag, useMessage } from 'naive-ui'
import { adminGetMessages, adminReplyMessage } from '../../api'
import type { Message } from '../../types'

const message = useMessage()

const messages = ref<Message[]>([])
const total = ref(0)
const current = ref(1)
const size = ref(10)
const loading = ref(false)
const status = ref<number | null>(null)
const showDialog = ref(false)
const replyText = ref('')
const currentMessage = ref<Message | null>(null)
const saving = ref(false)

const statusOptions = [
  { label: '待回复', value: 0 },
  { label: '已回复', value: 1 }
]

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '用户', key: 'userName', width: 100 },
  { title: '留言内容', key: 'content', minWidth: 220, ellipsis: { tooltip: true } },
  { title: '留言时间', key: 'createTime', width: 160 },
  {
    title: '状态', key: 'status', width: 90,
    render: (row: Message) => h(NTag, {
      type: row.status === 0 ? 'warning' : 'success', size: 'small', round: true
    }, { default: () => (row.status === 0 ? '待回复' : '已回复') })
  },
  { title: '回复内容', key: 'reply', minWidth: 200, ellipsis: { tooltip: true }, render: (row: Message) => row.reply || '—' },
  { title: '回复时间', key: 'replyTime', width: 160, render: (row: Message) => row.replyTime || '—' },
  {
    title: '操作', key: 'actions', width: 90,
    render: (row: Message) => h(NButton, { size: 'small', text: true, type: 'primary', onClick: () => openReply(row) }, { default: () => '回复' })
  }
]

async function load(page?: number) {
  if (page !== undefined) current.value = page
  loading.value = true
  try {
    const res = await adminGetMessages({ current: current.value, size: size.value, status: status.value ?? undefined })
    messages.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function onSearch() { current.value = 1; load() }

function openReply(row: Message) {
  currentMessage.value = row
  replyText.value = row.reply || ''
  showDialog.value = true
}

async function onSave() {
  if (!replyText.value.trim()) { message.warning('请输入回复内容'); return }
  saving.value = true
  try {
    await adminReplyMessage(currentMessage.value!.id, replyText.value.trim())
    message.success('回复成功')
    showDialog.value = false
    load()
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 10px;
  padding: 14px;
  margin-bottom: 14px;
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
}
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
.origin {
  background: #f9fafb;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
}
.origin .label { color: #6b7280; font-size: 13px; margin: 0 0 6px; }
.origin .content { color: #111827; margin: 0; }
</style>
