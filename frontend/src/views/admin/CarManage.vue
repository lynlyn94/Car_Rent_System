<template>
  <div>
    <div class="toolbar">
      <n-input v-model:value="query.brand" placeholder="品牌" clearable style="width: 140px" />
      <n-input v-model:value="query.carType" placeholder="类型" clearable style="width: 140px" />
      <n-select v-model:value="query.rentStatus" :options="statusOptions" clearable placeholder="出租状态" style="width: 130px" />
      <n-input v-model:value="query.keyword" placeholder="车牌/品牌/类型关键词" clearable style="width: 200px" />
      <n-button type="primary" @click="onSearch">查询</n-button>
      <n-button @click="onReset">重置</n-button>
      <div class="spacer"></div>
      <n-button type="primary" @click="openAdd">＋ 新增车辆</n-button>
    </div>

    <div class="table-card">
      <n-data-table :columns="columns" :data="cars" :loading="loading" :bordered="false" :single-line="false" />
      <div class="pager">
        <n-pagination v-model:page="current" :page-size="size" :item-count="total" @update:page="load" />
      </div>
    </div>

    <n-modal v-model:show="showDialog" :title="form.id ? '编辑车辆' : '新增车辆'" preset="card" style="width: 640px">
      <n-form label-placement="left" label-width="90px">
        <n-grid :cols="2" :x-gap="12">
          <n-form-item-gi label="车牌号"><n-input v-model:value="form.plateNo" /></n-form-item-gi>
          <n-form-item-gi label="品牌"><n-input v-model:value="form.brand" /></n-form-item-gi>
          <n-form-item-gi label="类型"><n-input v-model:value="form.carType" placeholder="如：SUV / 经济轿车" /></n-form-item-gi>
          <n-form-item-gi label="颜色"><n-input v-model:value="form.color" /></n-form-item-gi>
          <n-form-item-gi label="座位数"><n-input-number v-model:value="form.seats" :min="2" :max="20" style="width:100%" /></n-form-item-gi>
          <n-form-item-gi label="维护状态"><n-input v-model:value="form.maintainStatus" placeholder="如：正常" /></n-form-item-gi>
          <n-form-item-gi label="日租金"><n-input-number v-model:value="form.dailyRent" :min="0" :precision="2" style="width:100%" /></n-form-item-gi>
          <n-form-item-gi label="押金"><n-input-number v-model:value="form.deposit" :min="0" :precision="2" style="width:100%" /></n-form-item-gi>
          <n-form-item-gi label="购入日期"><n-date-picker v-model:formatted-value="form.purchaseDate" type="date" value-format="yyyy-MM-dd" clearable style="width:100%" /></n-form-item-gi>
          <n-form-item-gi label="出租状态">
            <n-select v-model:value="form.rentStatus" :options="statusOptions" />
          </n-form-item-gi>
          <n-form-item-gi label="车辆图片" :span="2">
            <div class="upload-row">
              <n-upload :custom-request="customUpload" :show-file-list="false" accept="image/*">
                <n-button>上传图片</n-button>
              </n-upload>
              <n-image v-if="form.image" :src="form.image" width="120" height="72" object-fit="cover" class="preview" />
            </div>
            <n-input v-model:value="form.image" placeholder="或手动填写图片地址 https://..." style="margin-top: 8px" />
          </n-form-item-gi>
          <n-form-item-gi label="说明" :span="2">
            <n-input v-model:value="form.description" type="textarea" :rows="2" />
          </n-form-item-gi>
        </n-grid>
      </n-form>
      <template #footer>
        <n-button @click="dialog = false">取消</n-button>
        <n-button type="primary" :loading="saving" @click="onSave">保存</n-button>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { h, onMounted, reactive, ref } from 'vue'
import {
  NDataTable, NInput, NSelect, NButton, NPagination, NModal, NForm, NFormItemGi,
  NGrid, NInputNumber, NDatePicker, NUpload, NImage, NTag, NSpace, useMessage, useDialog
} from 'naive-ui'
import { adminAddCar, adminDeleteCar, adminGetCars, adminUpdateCar, uploadImage } from '../../api'
import { formatMoney } from '../../utils/format'
import type { Car } from '../../types'

const message = useMessage()
const dialog = useDialog()

const cars = ref<Car[]>([])
const total = ref(0)
const current = ref(1)
const size = ref(10)
const loading = ref(false)
const showDialog = ref(false)
const saving = ref(false)

const query = reactive({ brand: '', carType: '', rentStatus: null as number | null, keyword: '' })
const statusOptions = [
  { label: '可租', value: 0 },
  { label: '已出租', value: 1 }
]

const emptyForm = () => ({
  id: 0, plateNo: '', brand: '', carType: '', color: '', seats: 5,
  dailyRent: 0, deposit: 0, maintainStatus: '正常', purchaseDate: null as string | null,
  rentStatus: 0, image: '', description: ''
})
const form = reactive(emptyForm())

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '车牌号', key: 'plateNo', width: 120 },
  { title: '品牌', key: 'brand', width: 100 },
  { title: '类型', key: 'carType', width: 110 },
  { title: '颜色', key: 'color', width: 80 },
  { title: '座位', key: 'seats', width: 70 },
  {
    title: '日租金', key: 'dailyRent', width: 100,
    render: (row: Car) => h('span', { style: 'color:#ea580c;font-weight:600' }, '¥' + formatMoney(row.dailyRent))
  },
  { title: '维护状态', key: 'maintainStatus', width: 100 },
  {
    title: '出租状态', key: 'rentStatus', width: 100,
    render: (row: Car) => h(NTag, {
      type: row.rentStatus === 0 ? 'success' : 'error', size: 'small', round: true
    }, { default: () => (row.rentStatus === 0 ? '可租' : '已出租') })
  },
  {
    title: '操作', key: 'actions', width: 130,
    render: (row: Car) => h(NSpace, { size: 4 }, { default: () => [
      h(NButton, { size: 'small', text: true, type: 'primary', onClick: () => openEdit(row) }, { default: () => '编辑' }),
      h(NButton, { size: 'small', text: true, type: 'error', onClick: () => onDelete(row) }, { default: () => '删除' })
    ] })
  }
]

async function load(page?: number) {
  if (page !== undefined) current.value = page
  loading.value = true
  try {
    const res = await adminGetCars({
      current: current.value, size: size.value,
      brand: query.brand || undefined, carType: query.carType || undefined,
      rentStatus: query.rentStatus ?? undefined, keyword: query.keyword || undefined
    })
    cars.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function onSearch() { current.value = 1; load() }
function onReset() { query.brand = ''; query.carType = ''; query.rentStatus = null; query.keyword = ''; onSearch() }

function openAdd() { Object.assign(form, emptyForm()); showDialog.value = true }
function openEdit(row: Car) { Object.assign(form, row); showDialog.value = true }

async function onSave() {
  if (!form.plateNo) { message.warning('请输入车牌号'); return }
  saving.value = true
  try {
    const data = { ...form, purchaseDate: form.purchaseDate || null }
    if (form.id) await adminUpdateCar(form.id, data)
    else await adminAddCar(data)
    message.success('保存成功')
    showDialog.value = false
    load()
  } finally {
    saving.value = false
  }
}

async function onDelete(row: Car) {
  const confirmed = await dialog.warning({
    title: '删除确认',
    content: `确定删除车辆「${row.plateNo}」吗？`,
    positiveText: '删除',
    negativeText: '取消'
  })
  if (!confirmed) return
  await adminDeleteCar(row.id)
  message.success('删除成功')
  load()
}

function customUpload({ file, onFinish, onError }: any) {
  const f = file.file as File
  if (!['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(f.type)) {
    message.error('仅支持 jpg/png/gif/webp 图片')
    onError()
    return
  }
  if (f.size > 10 * 1024 * 1024) {
    message.error('图片不能超过 10MB')
    onError()
    return
  }
  uploadImage(f).then((res) => {
    form.image = res.data
    message.success('图片上传成功')
    onFinish()
  }).catch(() => onError())
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
.upload-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.preview {
  border-radius: 6px;
  overflow: hidden;
}
</style>
