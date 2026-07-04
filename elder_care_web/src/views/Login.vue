<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        <span>养老服务系统 · 登录</span>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit" style="width:100%">登录</el-button>
        </el-form-item>
      </el-form>
      <p class="tip">开发环境默认：admin / admin123</p>
      <p class="tip">没有账号？<router-link to="/register">去注册</router-link></p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { login } from '../api/auth'

const router = useRouter()
const auth = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function onSubmit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    const res = await login(form)
    auth.setLogin(res.token, { userId: res.userId, username: res.username, realName: res.realName, role: res.role, linkedElderId: res.linkedElderId })
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    ElMessage.error(e || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.login-card { width: 400px; }
.tip { font-size: 12px; color: #999; margin-top: -10px; }
.tip a { color: #409eff; }
</style>
