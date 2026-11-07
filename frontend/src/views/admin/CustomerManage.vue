<template>
  <div>
    <div class="toolbar">
      <n-input v-model:value="query.keyword" placeholder="用户名/姓名/手机号" clearable style="width: 200px" />
      <n-select v-model:value="query.gender" :options="genderOptions" clearable placeholder="性别" style="width: 120px" />
      <n-button type="primary" @click="onSearch">查询</n-button>
      <n-button @click="onReset">重置</n-button>
      <div class="spacer"></div>
      <n-button type="primary" @click="openAdd">＋ 新增客户</n-button>
    </div>

    <div class="table-card">
      <n-data-table :columns="columns" :data="customers" :loading="loading" :bordered="false" />
      <div class="pager">
        <n-pagination v-model:page="current" :page-size="size" :item-count="total" @update:page="load" />
      </div>
    </div>

    <n-modal v-model:show="showDialog" :title="form.id ? '编辑客户' : '新增客户'" preset="card" style="width: 560px">
      <n-form label-placement="left" label-width="90px">
        <n-grid :cols="2" :x-gap="12">
          <n-form-item-gi label="用户名"><n-input v-model:value="form.username" /></n-form-item-gi>
          <n-form-item-gi label="密码"><n-input v-model:value="form.password" type="password" :placeholder="form.id ? '留空则不修改' : '留空默认 123456'" /></n-form-item-gi>
          <n-form-item-gi label="姓名"><n-input v-model:value="form.name" /></n-form-item-gi>
          <n-form-item-gi label="性别"><n-select v-model:value="form.gender" :options="genderOptions" /></n-form-item-gi>
          <n-form-item-gi label="出生日期"><n-date-picker v-model:formatted-value="form.birthDate" type="date" value-format="yyyy-MM-dd" clearable style="width:100%" /></n-form-item-gi>
          <n-form-item-gi label="身份证号"><n-input v-model:value="form.idCard" /></n-form-item-gi>
          <n-form-item-gi label="手机号"><n-input v-model:value="form.phone" /></n-form-item-gi>
          <n-form-item-gi label="驾龄"><n-input-number v-model:value="form.driveYears" :min="0" :max="60" style="width:100%" /></n-form-item-gi>
          <n-form-item-gi label="状态">
            <n-select v-model:value="form.status" :options="statusOptions" />
          </n-form-item-gi>
        </n-grid>
      </n-form>
      <template #footer>
        <n-button @click="showDialog = false">取消</n-button>
        <n-button type="primary" :loading="saving" @click="onSave">保存</n-button>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { h, onMounted, reactive, ref } from 'vue'
import {
  NDataTable, NInput, NSelect, NButton, NPagination, NModal, NForm, NFormItemGi,
  NGrid, NDatePicker, NInputNumber, NTag, NSpace, useMessage, useDialog
} from 'naive-ui'
import { adminAddCustomer, adminDeleteCustomer, adminGetCustomers, adminUpdateCustomer } from '../../api'
import { calcAge, genderText } from '../../utils/format'
import type { User } from '../../types'

const message = useMessage()
const dialog = useDialog()

const customers = ref<User[]>([])
const total = ref(0)
const current = ref(1)
const size = ref(10)
const loading = ref(false)
const showDialog = ref(false)
const saving = ref(false)

const query = reactive({ keyword: '', gender: null as number | null })
const genderOptions = [
  { label: '男', value: 1 },
  { label: '女', value: 0 }
]
const statusOptions = [
  { label: '正常', value: 1 },
  { label: '禁用', value: 0 }
]

const emptyForm = () => ({
  id: 0, username: '', password: '', name: '', gender: 1,
  birthDate: null as string | null, idCard: '', phone: '', driveYears: 0, status: 1
})
const form = reactive(emptyForm())

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '用户名', key: 'username', width: 120 },
  { title: '姓名', key: 'name', width: 100 },
  { title: '性别', key: 'gender', width: 70, render: (row: User) => genderText(row.gender) },
  { title: '年龄', key: 'age', width: 70, render: (row: User) => calcAge(row.birthDate) },
  { title: '手机号', key: 'phone', width: 130 },
  { title: '身份证号', key: 'idCard', width: 180 },
  { title: '驾龄', key: 'driveYears', width: 70 },
  {
    title: '状态', key: 'status', width: 80,
    render: (row: User) => h(NTag, {
      type: row.status === 0 ? 'default' : 'success', size: 'small', round: true
    }, { default: () => (row.status === 0 ? '禁用' : '正常') })
  },
  { title: '注册时间', key: 'createTime', width: 170 },
  {
    title: '操作', key: 'actions', width: 130,
    render: (row: User) => h(NSpace, { size: 4 }, { default: () => [
      h(NButton, { size: 'small', text: true, type: 'primary', onClick: () => openEdit(row) }, { default: () => '编辑' }),
      h(NButton, { size: 'small', text: true, type: 'error', onClick: () => onDelete(row) }, { default: () => '删除' })
    ] })
  }
]

async function load(page?: number) {
  if (page !== undefined) current.value = page
  loading.value = true
  try {
    const res = await adminGetCustomers({
      current: current.value, size: size.value,
      keyword: query.keyword || undefined, gender: query.gender ?? undefined
    })
    customers.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function onSearch() { current.value = 1; load() }
function onReset() { query.keyword = ''; query.gender = null; onSearch() }

function openAdd() { Object.assign(form, emptyForm()); showDialog.value = true }
function openEdit(row: User) { Object.assign(form, { ...row, password: '' }); showDialog.value = true }

async function onSave() {
  if (!form.username) { message.warning('请输入用户名'); return }
  saving.value = true
  try {
    const data = { ...form, birthDate: form.birthDate || null }
    if (form.id) await adminUpdateCustomer(form.id, data)
    else await adminAddCustomer(data)
    message.success('保存成功')
    showDialog.value = false
    load()
  } finally {
    saving.value = false
  }
}

async function onDelete(row: User) {
  const confirmed = await dialog.warning({
    title: '删除确认',
    content: `确定删除客户「${row.name || row.username}」吗？`,
    positiveText: '删除',
    negativeText: '取消'
  })
  if (!confirmed) return
  await adminDeleteCustomer(row.id)
  message.success('删除成功')
  load()
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
