<template>
  <div id="app">
    <div v-if="isLoginPage" class="login-layout">
      <router-view />
    </div>
    
    <el-container v-else>
      <el-header class="header">
        <div class="header-content">
          <div class="logo">
            <el-icon class="logo-icon"><Money /></el-icon>
            <h1>金融商品喜好紀錄系統</h1>
          </div>
          <div class="nav-menu">
            <el-menu
              mode="horizontal"
              :router="true"
              :default-active="$route.path"
              class="nav-menu"
            >
              <el-menu-item index="/">
                <el-icon><House /></el-icon>
                商品清單
              </el-menu-item>

              <el-menu-item index="/favorites">
                <el-icon><Star /></el-icon>
                喜好商品
              </el-menu-item>
            </el-menu>
          </div>
          <div class="user-info">
            <el-dropdown>
              <span class="user-dropdown">
                <el-avatar :size="32" icon="UserFilled" />
                <div class="user-info-text">
                  <span class="username">{{ currentUserDisplay }}</span>
                  <span class="user-role">{{ userRoleDisplay }}</span>
                </div>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="showProfile">
                    <el-icon><User /></el-icon>
                    個人資料
                  </el-dropdown-item>
                  <el-dropdown-item @click="logout">
                    <el-icon><SwitchButton /></el-icon>
                    登出
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>

      <el-footer class="footer">
        <p>&copy; 2024 金融商品喜好紀錄系統. All rights reserved.</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'App',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const currentUser = ref('')
    
    const userInfoTrigger = ref(0)

    const isLoginPage = computed(() => {
      return route.path === '/login'
    })



    const showProfile = () => {
      ElMessage.info('個人資料功能開發中...')
    }

    const logout = () => {
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('userEmail')
      localStorage.removeItem('userName')
      localStorage.removeItem('userId')
      
      window.dispatchEvent(new Event('storage'))
      
      ElMessage.success('已登出')
      router.push('/login')
    }

    const currentUserDisplay = computed(() => {
      userInfoTrigger.value
      
      const userName = localStorage.getItem('userName')
      const userEmail = localStorage.getItem('userEmail')
      const isLoggedIn = localStorage.getItem('isLoggedIn')
      
      if (!isLoggedIn) {
        return '未登入'
      }
      
      if (userName && userName.trim()) {
        return userName
      } else if (userEmail && userEmail.trim()) {
        return userEmail
      } else {
        return '用戶'
      }
    })

    const userRoleDisplay = computed(() => {
      userInfoTrigger.value
      
      const userEmail = localStorage.getItem('userEmail')

      return '用戶'
    })

    const updateUserInfo = () => {
      const userName = localStorage.getItem('userName')
      const userEmail = localStorage.getItem('userEmail')
      if (userName) {
        currentUser.value = userName
      } else if (userEmail) {
        currentUser.value = userEmail
      } else {
        currentUser.value = ''
      }
      
      userInfoTrigger.value++
    }

    onMounted(() => {
      updateUserInfo()
      
      window.addEventListener('storage', updateUserInfo)
    })

    watch(() => route.path, () => {
      updateUserInfo()
    })

    onUnmounted(() => {
      window.removeEventListener('storage', updateUserInfo)
    })

    return {
      currentUser,
      currentUserDisplay,
      userRoleDisplay,
      isLoginPage,
      
      showProfile,
      logout
    }
  }
}
</script>

<style scoped>
/* 登入頁面佈局 */
.login-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  font-size: 2rem;
  color: #ffd700;
}

.logo h1 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.nav-menu {
  background: transparent;
  border: none;
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-menu .el-menu {
  background: transparent;
  border: none;
  display: flex;
  justify-content: center;
}

.nav-menu .el-menu-item {
  color: white;
  background: transparent;
  border: none;
  white-space: nowrap;
}

.nav-menu .el-menu-item:hover,
.nav-menu .el-menu-item.is-active {
  background: rgba(255, 255, 255, 0.1);
  color: #ffd700;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background: rgba(255, 255, 255, 0.1);
}

.user-info-text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.username {
  color: white;
  font-weight: 500;
  font-size: 14px;
  line-height: 1;
}

.user-role {
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
  line-height: 1;
}

.main-content {
  min-height: calc(100vh - 120px);
  background: #f5f7fa;
  padding: 20px;
}

.footer {
  background: #2c3e50;
  color: white;
  text-align: center;
  padding: 20px;
  font-size: 14px;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 15px;
    padding: 10px;
  }

  .logo h1 {
    font-size: 1.2rem;
  }

  .nav-menu {
    width: 100%;
  }

  .nav-menu .el-menu {
    flex-wrap: wrap;
    justify-content: center;
  }

  .nav-menu .el-menu-item {
    font-size: 0.9rem;
    padding: 0 8px;
  }

  .user-info {
    margin-top: 10px;
  }
}

/* 防止導航欄摺疊 */
.el-menu--horizontal {
  border-bottom: none !important;
}

.el-menu--horizontal > .el-menu-item {
  border-bottom: none !important;
}

.el-menu--horizontal > .el-menu-item:hover {
  border-bottom: none !important;
}
</style>
