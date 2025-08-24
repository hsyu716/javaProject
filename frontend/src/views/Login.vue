<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>🔐 歡迎回來</h1>
        <p>請登入您的帳戶以繼續使用服務</p>
      </div>

      <el-form 
        ref="loginForm" 
        :model="loginData" 
        :rules="loginRules" 
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="email">
          <el-input
            v-model="loginData.email"
            type="email"
            placeholder="請輸入電子郵件"
            size="large"
            prefix-icon="Message"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginData.password"
            type="password"
            placeholder="請輸入密碼"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="loginData.rememberMe">
            記住我
          </el-checkbox>
          <el-link type="primary" class="forgot-password">
            忘記密碼？
          </el-link>
        </el-form-item>

        <el-form-item>
          <el-button 
            type="primary" 
            size="large" 
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登入中...' : '登入' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p>還沒有帳戶？</p>
        <el-button type="text" disabled>
          <el-icon><Tools /></el-icon>
          註冊功能開發中
        </el-button>
      </div>
    </div>

    <!-- 背景裝飾 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const loginForm = ref(null)
const loading = ref(false)

const loginData = reactive({
  email: '',
  password: '',
  rememberMe: false
})



const loginRules = {
  email: [
    { required: true, message: '請輸入電子郵件', trigger: 'blur' },
    { type: 'email', message: '請輸入正確的電子郵件格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '請輸入密碼', trigger: 'blur' },
    { min: 6, message: '密碼長度至少6位', trigger: 'blur' }
  ]
}



const handleLogin = async () => {
  try {
    const valid = await loginForm.value.validate()
    if (!valid) return

    loading.value = true
    
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        userId: loginData.email,
        password: loginData.password
      })
    })
    
    const result = await response.json()
    
    if (result.success) {
      ElMessage.success('登入成功！')
      
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('userEmail', result.email || loginData.email)
      localStorage.setItem('userName', result.userName || '用戶')
      localStorage.setItem('userId', result.userId || loginData.email)
      localStorage.setItem('userAccount', result.account || '')
      
      window.dispatchEvent(new Event('storage'))
      
      router.push('/')
    } else {
      ElMessage.error(result.message || '登入失敗')
    }
  } catch (error) {
    ElMessage.error('登入失敗，請稍後重試')
    console.error('登入錯誤:', error)
  } finally {
    loading.value = false
  }
}


</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-card {
  background: white;
  border-radius: 20px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 2rem;
}

.login-header p {
  margin: 0;
  color: #909399;
  font-size: 0.9rem;
}

.login-form {
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: 1.1rem;
  border-radius: 25px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.forgot-password {
  float: right;
  font-size: 0.9rem;
}

.login-footer {
  text-align: center;
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
}

.login-footer p {
  margin: 0 0 10px 0;
  color: #909399;
  font-size: 0.9rem;
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 100px;
  height: 100px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 10%;
  animation-delay: 2s;
}

.circle-3 {
  width: 80px;
  height: 80px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

/* 響應式設計 */
@media (max-width: 480px) {
  .login-card {
    margin: 20px;
    padding: 30px 20px;
  }
  
  .login-header h1 {
    font-size: 1.5rem;
  }
}

/* 表單驗證樣式 */
:deep(.el-form-item__error) {
  color: #f56c6c;
  font-size: 0.8rem;
  margin-top: 5px;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
}

:deep(.el-button--text) {
  color: #667eea;
}

:deep(.el-button--text:hover) {
  color: #764ba2;
}

:deep(.el-button--text:disabled) {
  color: #c0c4cc;
  cursor: not-allowed;
}

:deep(.el-button--text:disabled:hover) {
  color: #c0c4cc;
}
</style>
