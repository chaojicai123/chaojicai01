<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">养老服务系统</div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/"><el-icon><HomeFilled /></el-icon> 工作台</el-menu-item>
        <el-menu-item index="/elders"><el-icon><User /></el-icon> 老人信息</el-menu-item>
        <el-menu-item index="/staff"><el-icon><Avatar /></el-icon> 护理人员</el-menu-item>
        <el-menu-item index="/appointments"><el-icon><Calendar /></el-icon> 服务预约</el-menu-item>
        <el-menu-item index="/health"><el-icon><FirstAidKit /></el-icon> 健康监测</el-menu-item>
        <el-menu-item index="/notifications"><el-icon><Bell /></el-icon> 消息通知</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="title">{{ $route.meta.title || '养老服务系统' }}</span>
        <el-dropdown @command="handleCommand">
          <span class="user">{{ auth.user?.realName || auth.user?.username }}</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

function handleCommand(cmd) {
  if (cmd === 'logout') {
    auth.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout { height: 100%; }
.aside { background: #304156; }
.logo { color: #fff; padding: 20px; font-size: 18px; font-weight: bold; text-align: center; }
.header { display: flex; align-items: center; justify-content: space-between; background: #fff; border-bottom: 1px solid #eee; padding: 0 20px; }
.title { font-size: 18px; }
.user { cursor: pointer; }
.main { background: #f0f2f5; padding: 20px; }
</style>
