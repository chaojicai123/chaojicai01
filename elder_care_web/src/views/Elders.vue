<template>
  <div>
    <el-card>
      <template #header>
        <span>老人信息管理</span>
        <el-button type="primary" style="float:right" @click="openForm()">新增老人</el-button>
      </template>
      <el-form inline>
        <el-form-item label="姓名">
          <el-input v-model="query.name" placeholder="姓名" clearable @keyup.enter="load" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="load">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openForm(row)">编辑</el-button>
            <el-button link type="primary" @click="$router.push(`/health/${row.id}`)">健康</el-button>
            <el-button link type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @current-change="load"
        @size-change="load"
        style="margin-top:16px"
      />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑老人' : '新增老人'" width="600px" @close="form = {}">
      <el-form :model="form" label-width="100px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="性别"><el-select v-model="form.gender" placeholder="性别"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select></el-form-item>
        <el-form-item label="出生日期"><el-date-picker v-model="form.birthDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="身份证"><el-input v-model="form.idCard" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="紧急联系人"><el-input v-model="form.emergencyContact" /></el-form-item>
        <el-form-item label="紧急电话"><el-input v-model="form.emergencyPhone" /></el-form-item>
        <el-form-item label="健康状况"><el-input v-model="form.healthCondition" type="textarea" /></el-form-item>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getElders, createElder, updateElder, deleteElder } from '../api/elders'

const loading = ref(false)
const list = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const query = reactive({ name: '' })
const dialogVisible = ref(false)
const form = ref({})

async function load() {
  loading.value = true
  try {
    const res = await getElders({ page: page.value - 1, size: size.value, name: query.name || undefined })
    list.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

function openForm(row) {
  form.value = row?.id ? { ...row } : { name: '', gender: '', birthDate: '', idCard: '', address: '', phone: '', emergencyContact: '', emergencyPhone: '', healthCondition: '', remark: '', status: 'ACTIVE' }
  dialogVisible.value = true
}

async function submit() {
  try {
    if (form.value.id) await updateElder(form.value.id, form.value)
    else await createElder(form.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error(e || '保存失败')
  }
}

async function del(id) {
  await ElMessageBox.confirm('确定删除？')
  await deleteElder(id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>
