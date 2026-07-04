<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>服务预约与分配</span>
          <el-button type="primary" @click="openForm()">新建预约</el-button>
        </div>
      </template>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="elderName" label="老人" :formatter="(_, __, v) => v ?? '-'" />
        <el-table-column prop="staffName" label="护理人员" :formatter="(_, __, v) => v ?? '-'" />
        <el-table-column prop="serviceType" label="服务类型" :formatter="(_, __, v) => v ?? '-'" />
        <el-table-column prop="scheduledAt" label="预约时间" width="160" :formatter="(_, __, v) => formatDateTime(v)" />
        <el-table-column prop="status" label="状态" width="100" :formatter="(_, __, v) => formatStatus(v)" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 'PENDING' || row.status === 'CONFIRMED'">
              <el-select v-if="availableStaff.length" placeholder="选择护理人员" size="small" style="width:140px" @change="(val) => assign(row.id, val)">
                <el-option v-for="s in availableStaff" :key="s.id" :label="s.name" :value="s.id" />
              </el-select>
              <span v-else class="no-staff-tip">请先在【护理人员】中添加</span>
            </template>
            <el-button link type="primary" @click="updateStatus(row.id, 'COMPLETED')" v-if="row.status === 'IN_PROGRESS'">完成</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, sizes, prev, pager, next" @current-change="load" @size-change="load" style="margin-top:16px" />
    </el-card>
    <el-dialog v-model="dialogVisible" title="新建预约" width="500px" @close="form = {}">
      <el-form :model="form" label-width="100px">
        <el-form-item label="老人"><el-select v-model="form.elderId" placeholder="选择老人" filterable style="width:100%"><el-option v-for="e in elders" :key="e.id" :label="e.name" :value="e.id" /></el-select></el-form-item>
        <el-form-item label="服务类型"><el-input v-model="form.serviceType" placeholder="如：日常护理、康复" /></el-form-item>
        <el-form-item label="预约时间"><el-date-picker v-model="form.scheduledAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.note" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAppointments, createAppointment, updateAppointmentStatus, assignStaff as apiAssignStaff } from '../api/appointments'
import { getElders } from '../api/elders'
import { getAvailableStaff } from '../api/staff'

const loading = ref(false)
const list = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const form = ref({})
const elders = ref([])
const availableStaff = ref([])

function formatDateTime(v) {
  if (v == null) return '-'
  if (typeof v === 'string') return v.replace('T', ' ').slice(0, 19)
  if (Array.isArray(v) && v.length >= 6) return `${v[0]}-${String(v[1]).padStart(2,'0')}-${String(v[2]).padStart(2,'0')} ${String(v[3]).padStart(2,'0')}:${String(v[4]).padStart(2,'0')}:${String(v[5]).padStart(2,'0')}`
  return String(v)
}
function formatStatus(v) {
  const map = { PENDING: '待处理', CONFIRMED: '已确认', IN_PROGRESS: '进行中', COMPLETED: '已完成', CANCELLED: '已取消' }
  return (v && map[v]) || v || '-'
}

async function load() {
  loading.value = true
  try {
    const res = await getAppointments({ page: page.value - 1, size: size.value })
    list.value = Array.isArray(res?.content) ? res.content : []
    total.value = res?.totalElements ?? 0
  } finally {
    loading.value = false
  }
}

async function loadElders() {
  const res = await getElders({ page: 0, size: 500 })
  elders.value = res.content
}
async function loadStaff() {
  try {
    const res = await getAvailableStaff()
    availableStaff.value = Array.isArray(res) ? res : []
  } catch (_) {
    availableStaff.value = []
  }
}

function openForm() {
  form.value = { elderId: null, serviceType: '', scheduledAt: '', note: '' }
  dialogVisible.value = true
  loadElders()
}

async function submit() {
  if (!form.value.elderId) { ElMessage.warning('请选择老人'); return }
  try {
    await createAppointment({ elder: { id: form.value.elderId }, serviceType: form.value.serviceType || null, scheduledAt: form.value.scheduledAt || null, note: form.value.note || null })
    ElMessage.success('创建成功')
    dialogVisible.value = false
    page.value = 1
    await load()
  } catch (e) {
    const msg = typeof e === 'string' ? e : (e?.response?.data?.message ?? e?.message ?? '创建失败')
    ElMessage.error(msg)
  }
}

async function updateStatus(id, status) {
  await updateAppointmentStatus(id, status)
  ElMessage.success('已更新')
  load()
}

async function assign(id, staffId) {
  if (!staffId) return
  try {
    await apiAssignStaff(id, staffId)
    ElMessage.success('已分配')
    await load()
  } catch (e) {
    const msg = typeof e === 'string' ? e : (e?.response?.data?.message ?? e?.message ?? '分配失败')
    ElMessage.error(msg)
  }
}

onMounted(() => { load(); loadStaff() })
</script>

<style scoped>
.card-header { display: flex; align-items: center; justify-content: space-between; }
.no-staff-tip { font-size: 12px; color: #909399; }
</style>
