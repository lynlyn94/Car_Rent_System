import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes: RouteRecordRaw[] = [
  { path: '/', name: 'home', component: () => import('../views/Home.vue') },
  { path: '/cars', name: 'cars', component: () => import('../views/CarList.vue') },
  { path: '/cars/:id', name: 'carDetail', component: () => import('../views/CarDetail.vue') },
  { path: '/login', name: 'login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'register', component: () => import('../views/Register.vue') },
  {
    path: '/order/confirm',
    name: 'orderConfirm',
    component: () => import('../views/OrderConfirm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'myOrders',
    component: () => import('../views/MyOrders.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/messages',
    name: 'myMessages',
    component: () => import('../views/MyMessages.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin/login',
    name: 'adminLogin',
    component: () => import('../views/AdminLogin.vue')
  },
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { requiresAdmin: true },
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', name: 'adminDashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'cars', name: 'adminCars', component: () => import('../views/admin/CarManage.vue') },
      { path: 'customers', name: 'adminCustomers', component: () => import('../views/admin/CustomerManage.vue') },
      { path: 'orders', name: 'adminOrders', component: () => import('../views/admin/OrderManage.vue') },
      { path: 'messages', name: 'adminMessages', component: () => import('../views/admin/MessageManage.vue') }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLogin) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    return { path: '/admin/login' }
  }
  return true
})

export default router
