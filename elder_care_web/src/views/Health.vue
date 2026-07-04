<template>
  <div>
    <!-- 管理员查看所有健康数据模式 -->
    <el-card v-if="isAdmin && !elderId">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <div>
          <h3>健康数据监测 - 管理员模式</h3>
          <p style="margin: 8px 0 0; color: #666;">查看所有老人的健康数据</p>
        </div>
        <div>
          <el-button @click="switchToSingleMode" type="primary" plain>切换到单老人模式</el-button>
          <el-button @click="openForm" type="primary">录入数据</el-button>
        </div>
      </div>
    </el-card>
    
    <!-- 单老人模式或非管理员模式 -->
    <el-card v-if="(!isAdmin && !elderId) || (isAdmin && !elderId)">
      <p v-if="isAdmin && !elderId">请先选择老人查看健康数据，或从 <router-link to="/elders">老人信息</router-link> 进入。</p>
      <p v-else-if="!elders.length">您暂无关联老人，请联系管理员在【用户管理】中为您关联老人后再查看健康数据。</p>
      <p v-else>请选择老人查看健康数据。</p>
      <el-select v-if="elders.length" v-model="elderId" placeholder="选择老人" filterable style="width:300px" @change="load">
        <el-option v-for="e in elders" :key="e.id" :label="e.name" :value="e.id" />
      </el-select>
    </el-card>
    
    <!-- 健康数据表格 -->
    <el-card v-if="isAdmin || (elderId && elders.length)">
      <template #header>
        <div class="card-header">
          <span>健康数据监测 · {{ elderName }}</span>
          <el-button type="primary" @click="openForm">录入数据</el-button>
        </div>
      </template>
      <el-table :data="list" v-loading="loading" stripe>
        <!-- 管理员模式下显示老人姓名 -->
        <el-table-column v-if="isAdmin && !elderId" prop="elderName" label="老人姓名" width="120" />
        <el-table-column prop="recordDate" label="日期" width="120" :formatter="(_, __, v) => formatDate(v)" />
        <el-table-column prop="bloodPressureHigh" label="收缩压" width="90" :formatter="(_, __, v) => formatNum(v)" />
        <el-table-column prop="bloodPressureLow" label="舒张压" width="90" :formatter="(_, __, v) => formatNum(v)" />
        <el-table-column prop="heartRate" label="心率" width="80" :formatter="(_, __, v) => formatNum(v)" />
        <el-table-column prop="bloodSugar" label="血糖" width="80" :formatter="(_, __, v) => formatNum(v)" />
        <el-table-column prop="temperature" label="体温" width="80" :formatter="(_, __, v) => formatNum(v)" />
        <el-table-column prop="weightKg" label="体重(kg)" width="90" :formatter="(_, __, v) => formatNum(v)" />
        <el-table-column prop="source" label="来源" width="100" :formatter="(_, __, v) => v ?? '-'" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip :formatter="(_, __, v) => v ?? '-'" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @current-change="load" style="margin-top:16px" />
    </el-card>
      <el-dialog v-model="dialogVisible" title="录入健康数据" width="500px" @close="form = {}">
        <el-form :model="form" label-width="100px">
          <!-- 管理员模式下可以选择老人 -->
          <el-form-item v-if="isAdmin && !elderId" label="选择老人">
            <el-select v-model="form.elderId" placeholder="请选择老人" style="width:100%">
              <el-option v-for="e in elders" :key="e.id" :label="e.name" :value="e.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="记录日期"><el-date-picker v-model="form.recordDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
          <el-form-item label="收缩压"><el-input-number v-model="form.bloodPressureHigh" :min="0" :max="300" /></el-form-item>
          <el-form-item label="舒张压"><el-input-number v-model="form.bloodPressureLow" :min="0" :max="200" /></el-form-item>
          <el-form-item label="心率"><el-input-number v-model="form.heartRate" :min="0" /></el-form-item>
          <el-form-item label="血糖"><el-input-number v-model="form.bloodSugar" :min="0" :precision="2" /></el-form-item>
          <el-form-item label="体温"><el-input-number v-model="form.temperature" :min="30" :max="45" :precision="2" /></el-form-item>
          <el-form-item label="体重(kg)"><el-input-number v-model="form.weightKg" :min="0" :precision="2" /></el-form-item>
          <el-form-item label="来源"><el-input v-model="form.source" placeholder="如：手环、手动" /></el-form-item>
          <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit">确定</el-button>
        </template>
      </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { getHealthByElder, createHealthRecord, deleteHealthRecord, getAllHealthRecords } from '../api/health'
import { getElders } from '../api/elders'

const route = useRoute()
const auth = useAuthStore()
const isAdmin = computed(() => {
  if (!auth.user || !auth.user.role) return false;
  return auth.user.role.toUpperCase() === 'ADMIN';
});
const elderId = ref(route.params.elderId ? Number(route.params.elderId) : null)
const elders = ref([])
const elderName = computed(() => elders.value.find(e => e.id === elderId.value)?.name || '')
const loading = ref(false)
const list = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const form = ref({})

async function loadElders() {
  const res = await getElders({ page: 0, size: 500 })
  elders.value = Array.isArray(res?.content) ? res.content : []
  // 管理员模式下不要自动选择老人，以便显示所有老人的健康数据
  if (!isAdmin.value) {
    if (!elderId.value && elders.value.length) elderId.value = elders.value[0].id
    if (!elderId.value && auth.user?.linkedElderId && elders.value.some(e => e.id === auth.user.linkedElderId)) elderId.value = auth.user.linkedElderId
  }
}

function formatDate(v) {
  if (v == null) return '-'
  if (typeof v === 'string') return v
  if (Array.isArray(v)) return v.join('-')
  return String(v)
}
function formatNum(v) {
  if (v == null || v === '') return '-'
  const n = Number(v)
  return isNaN(n) ? '-' : n
}

async function load() {
  loading.value = true
  try {
    // 管理员查看所有数据模式
    if (isAdmin.value && !elderId.value) {
      console.log('Admin mode: loading all health records')
      const res = await getAllHealthRecords({ page: page.value - 1, size: size.value })
      console.log('Admin mode response:', res)
      list.value = Array.isArray(res?.content) ? res.content : []
      total.value = res?.totalElements ?? 0
    } else {
      // 单老人模式
      if (!elderId.value) {
        console.log('No elder selected, clearing list')
        list.value = []
        total.value = 0
        return
      }
      console.log('Single elder mode: loading health records for elder', elderId.value)
      const res = await getHealthByElder(elderId.value, { page: page.value - 1, size: size.value })
      console.log('Single elder mode response:', res)
      list.value = Array.isArray(res?.content) ? res.content : []
      total.value = res?.totalElements ?? 0
    }
  } catch (error) {
    console.error('Error loading health records:', error)
    ElMessage.error('加载健康数据失败: ' + (error?.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 切换到单老人模式
function switchToSingleMode() {
  elderId.value = elders.value.length ? elders.value[0].id : null
  page.value = 1
  load()
}

function openForm() {
  const d = new Date()
  const today = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  form.value = { elderId: elderId.value, recordDate: today, bloodPressureHigh: undefined, bloodPressureLow: undefined, heartRate: undefined, bloodSugar: undefined, temperature: undefined, weightKg: undefined, source: '', remark: '' }
  dialogVisible.value = true
}

async function submit() {
  try {
    await createHealthRecord({ 
      elderId: form.value.elderId, 
      recordDate: form.value.recordDate, 
      bloodPressureHigh: form.value.bloodPressureHigh ?? null, 
      bloodPressureLow: form.value.bloodPressureLow ?? null, 
      heartRate: form.value.heartRate ?? null, 
      bloodSugar: form.value.bloodSugar ?? null, 
      temperature: form.value.temperature ?? null, 
      weightKg: form.value.weightKg ?? null, 
      source: form.value.source || null, 
      remark: form.value.remark || null 
    })
    ElMessage.success('录入成功')
    dialogVisible.value = false
    page.value = 1
    await load()
  } catch (e) {
    const msg = typeof e === 'string' ? e : (e?.response?.data?.message ?? e?.message ?? '录入失败')
    ElMessage.error(msg)
  }
}

async function del(id) {
  await ElMessageBox.confirm('确定删除？')
  await deleteHealthRecord(id)
  ElMessage.success('已删除')
  load()
}

watch(elderId, load)
onMounted(() => { loadElders().then(load) })
</script>

<style scoped>
.card-header { display: flex; align-items: center; justify-content: space-between; }
</style>