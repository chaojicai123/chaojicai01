<template>
  <div class="register-page">
    <el-card class="register-card">
      <template #header>
        <span>养老服务系统 · 注册</span>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="2-50 个字符" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="至少 6 位" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="选填" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="选填" />
        </el-form-item>
        <el-form-item label="身份" prop="role">
          <el-select v-model="form.role" placeholder="请选择" style="width:100%">
            <el-option label="家属" value="FAMILY" />
            <el-option label="护理人员" value="STAFF" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit" style="width:100%">注册</el-button>
        </el-form-item>
        <p class="tip">
          已有账号？
          <router-link to="/login">去登录</router-link>
        </p>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { register } from '../api/auth'

const router = useRouter()
const auth = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  role: 'FAMILY'
})

const validateConfirm = (rule, value, callback) => {
  if (value !== form.password) callback(new Error('两次输入的密码不一致'))
  else callback()
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 50, message: '长度 2-50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择身份', trigger: 'change' }]
}

async function onSubmit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    const res = await register({
      username: form.username,
      password: form.password,
      realName: form.realName || undefined,
      phone: form.phone || undefined,
      role: form.role
    })
    auth.setLogin(res.token, { userId: res.userId, username: res.username, realName: res.realName, role: res.role, linkedElderId: res.linkedElderId })
    ElMessage.success('注册成功')
    router.push('/')
  } catch (e) {
    const msg = typeof e === 'string' ? e : (e?.response?.data?.message ?? e?.message ?? '注册失败')
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.register-card { width: 400px; }
.tip { font-size: 12px; color: #999; margin-top: -10px; }
.tip a { color: #409eff; }
</style>
