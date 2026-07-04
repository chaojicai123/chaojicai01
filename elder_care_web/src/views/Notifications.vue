<template>
  <div>
    <el-card>
      <template #header>消息通知</template>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="readFlag" label="已读" width="80"><template #default="{ row }">{{ row.readFlag ? '是' : '否' }}</template></el-table-column>
        <el-table-column prop="createdAt" label="时间" width="180" />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button v-if="!row.readFlag" link type="primary" @click="markRead(row.id)">标为已读</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, prev, pager, next" @current-change="load" style="margin-top:16px" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getNotifications, markRead as apiMarkRead } from '../api/notifications'

const loading = ref(false)
const list = ref([])
const page = ref(1)
const size = ref(20)
const total = ref(0)

async function load() {
  loading.value = true
  try {
    const res = await getNotifications({ page: page.value - 1, size: size.value })
    list.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

async function markRead(id) {
  await apiMarkRead(id)
  ElMessage.success('已标为已读')
  load()
}

onMounted(load)
</script>
