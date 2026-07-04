import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue'), meta: { guest: true } },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue'), meta: { guest: true } },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'Dashboard', component: () => import('../views/Dashboard.vue') },
      { path: 'elders', name: 'Elders', component: () => import('../views/Elders.vue') },
      { path: 'staff', name: 'Staff', component: () => import('../views/Staff.vue') },
      { path: 'appointments', name: 'Appointments', component: () => import('../views/Appointments.vue') },
      { path: 'health/:elderId?', name: 'Health', component: () => import('../views/Health.vue') },
      { path: 'notifications', name: 'Notifications', component: () => import('../views/Notifications.vue') }
    ]
  }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.token) {
    next({ name: 'Login' })
  } else if (to.meta.guest && auth.token) {
    next({ name: 'Dashboard' })
  } else {
    next()
  }
})

export default router
