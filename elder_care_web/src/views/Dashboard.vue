<template>
  <div>
    <div class="header">
      <h2 style="margin:0">工作台</h2>
      <el-button :icon="Refresh" circle @click="fetchStats" :loading="loading" title="刷新数据" />
    </div>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat">
            <el-icon size="32" color="#409EFF"><User /></el-icon>
            <div>
              <div class="stat-value">{{ stats.elderCount ?? '-' }}</div>
              <div class="stat-label">老人总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat">
            <el-icon size="32" color="#67C23A"><Avatar /></el-icon>
            <div>
              <div class="stat-value">{{ stats.staffCount ?? '-' }}</div>
              <div class="stat-label">护理人员</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat">
            <el-icon size="32" color="#E6A23C"><Calendar /></el-icon>
            <div>
              <div class="stat-value">{{ stats.appointmentCount ?? '-' }}</div>
              <div class="stat-label">预约总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat">
            <el-icon size="32" color="#F56C6C"><FirstAidKit /></el-icon>
            <div>
              <div class="stat-value">{{ stats.healthRecordCount ?? '-' }}</div>
              <div class="stat-label">健康记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { getStats } from '../api/admin'

const stats = ref({})
const loading = ref(false)
const REFRESH_INTERVAL = 15000 // 每 15 秒刷新一次

async function fetchStats() {
  loading.value = true
  try {
    stats.value = await getStats()
  } catch (_) {}
  finally {
    loading.value = false
  }
}

let timer = null

onMounted(() => {
  fetchStats()
  timer = setInterval(fetchStats, REFRESH_INTERVAL)
  document.addEventListener('visibilitychange', onVisibilityChange)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
  document.removeEventListener('visibilitychange', onVisibilityChange)
})

function onVisibilityChange() {
  if (document.visibilityState === 'visible') fetchStats()
}
</script>

<style scoped>
.header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.stat { display: flex; align-items: center; gap: 16px; }
.stat-value { font-size: 24px; font-weight: bold; }
.stat-label { font-size: 14px; color: #909399; }
</style>
