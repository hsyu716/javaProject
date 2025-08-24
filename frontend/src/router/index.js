import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import FavoriteProducts from '../views/FavoriteProducts.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '商品清單' }
  },
  {
    path: '/favorites',
    name: 'FavoriteProducts',
    component: FavoriteProducts,
    meta: { title: '喜好商品' }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登入' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('isLoggedIn') === 'true'
  
  if (to.path === '/login') {
    if (isAuthenticated) {
      next('/')
    } else {
      next()
    }
  } else {
    if (!isAuthenticated) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router
