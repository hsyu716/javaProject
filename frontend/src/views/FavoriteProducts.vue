<template>
  <div class="favorite-products">
    <!-- 頁面標題 -->
    <div class="page-header">
      <h1>⭐ 喜好商品清單</h1>
      <p>管理您的喜好金融商品</p>
    </div>

    <!-- 搜尋和篩選 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input
            v-model="searchForm.productName"
            placeholder="搜尋商品名稱"
            clearable
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-input
            v-model="searchForm.account"
            placeholder="搜尋帳號"
            clearable
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><CreditCard /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-button type="info" @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 喜好商品列表 -->
    <el-card class="favorites-list-card">
      <el-table
        :data="filteredFavorites"
        style="width: 100%"
        v-loading="loading"
      >
                 <el-table-column label="商品名稱" min-width="150">
           <template #default="scope">
             {{ scope.row.product?.productName || '' }}
           </template>
         </el-table-column>
        <el-table-column label="購買數量" width="100">
          <template #default="scope">
            {{ scope.row.quantity || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="account" label="扣款帳號" width="120" />
        <el-table-column label="訂單名稱" width="150">
          <template #default="scope">
            {{ scope.row.orderName || scope.row.product?.productName || '' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="總金額" width="120">
          <template #default="scope">
            ${{ formatNumber(scope.row.totalAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalFee" label="總手續費" width="120">
          <template #default="scope">
            ${{ formatNumber(scope.row.totalFee) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="建立時間" width="160">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <!-- 編輯喜好商品 -->
            <el-button 
              size="small" 
              type="warning" 
              @click="editFavorite(scope.row)"
            >
              <el-icon><Edit /></el-icon>
              編輯
            </el-button>
            
            <!-- 刪除喜好商品 -->
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteFavorite(scope.row)"
              :loading="deletingFavorite"
            >
              <el-icon><Delete /></el-icon>
              刪除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分頁 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalFavorites"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 編輯喜好商品對話框 -->
    <el-dialog
      v-model="showEditDialog"
      title="編輯喜好商品"
      width="500px"
    >
      <el-form
        ref="favoriteFormRef"
        :model="favoriteForm"
        :rules="favoriteRules"
        label-width="120px"
      >
        <el-form-item label="商品名稱">
          <el-input v-model="favoriteForm.productName" disabled />
        </el-form-item>
        
        <el-form-item label="購買數量" prop="quantity">
          <el-input-number
            v-model="favoriteForm.quantity"
            :min="1"
            :step="1"
            style="width: 100%"
            placeholder="請輸入購買數量"
          />
        </el-form-item>
        
        <el-form-item label="扣款帳號" prop="account">
          <el-input
            v-model="favoriteForm.account"
            placeholder="請輸入扣款帳號"
            maxlength="20"
          />
        </el-form-item>
        
        <el-form-item label="訂單名稱" prop="orderName">
          <el-input
            v-model="favoriteForm.orderName"
            placeholder="請輸入訂單名稱（可選）"
            maxlength="50"
          />
        </el-form-item>

        <!-- 計算結果預覽 -->
        <el-form-item label="預覽">
          <div class="calculation-preview">
            <p>總金額: ${{ formatNumber(calculateTotalAmount()) }}</p>
            <p>總手續費: ${{ formatNumber(calculateTotalFee()) }}</p>
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="saveFavorite" :loading="savingFavorite">
            更新
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'FavoriteProducts',
  setup() {
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(20)
    const totalFavorites = ref(0)
    
    const favorites = ref([])
    const products = ref([])

    const showEditDialog = ref(false)
    const editingFavorite = ref(null)
    const savingFavorite = ref(false)
    const deletingFavorite = ref(false)
    const favoriteFormRef = ref()

    const favoriteForm = reactive({
      productName: '',
      quantity: 1,
      account: '',
      orderName: '',
      productNo: null,
      price: null,
      feeRate: null
    })

    const favoriteRules = {
      quantity: [
        { required: true, message: '請輸入購買數量', trigger: 'blur' },
        { type: 'number', min: 1, message: '購買數量必須大於0', trigger: 'blur' }
      ],
      account: [
        { required: true, message: '請輸入扣款帳號', trigger: 'blur' },
        { min: 1, max: 20, message: '扣款帳號長度在1-20個字符', trigger: 'blur' }
      ],
      orderName: [
        { max: 50, message: '訂單名稱不能超過50個字符', trigger: 'blur' }
      ]
    }

    const searchForm = reactive({
      productName: '',
      account: ''
    })

    const filteredFavorites = computed(() => {
      let filtered = favorites.value

      if (searchForm.productName) {
        filtered = filtered.filter(f => 
          f.product?.productName?.toLowerCase().includes(searchForm.productName.toLowerCase())
        )
      }

      if (searchForm.account) {
        filtered = filtered.filter(f => 
          f.account.includes(searchForm.account)
        )
      }

      return filtered
    })

    const loadFavorites = async () => {
      try {
        loading.value = true
        const userEmail = localStorage.getItem('userEmail')
        if (userEmail) {
          const userId = localStorage.getItem('userId')
          if (userId) {
            const response = await fetch(`/api/like-list/user/${userId}`)
            if (response.ok) {
              const data = await response.json()
              favorites.value = data || []
              totalFavorites.value = favorites.value.length
            }
          }
        }
      } catch (error) {
        ElMessage.error('載入喜好清單失敗：' + error.message)
      } finally {
        loading.value = false
      }
    }

    const loadProducts = async () => {
      try {
        const response = await fetch('/api/products')
        if (response.ok) {
          const data = await response.json()
          products.value = data || []
        }
      } catch (error) {
        console.error('載入商品失敗:', error)
      }
    }

    const editFavorite = (favorite) => {
      editingFavorite.value = favorite
      favoriteForm.productName = favorite.product?.productName || ''
      favoriteForm.quantity = favorite.quantity
      favoriteForm.account = favorite.account
      favoriteForm.orderName = favorite.orderName || favorite.product?.productName || ''
      favoriteForm.productNo = favorite.product?.no
      favoriteForm.price = favorite.product?.price
      favoriteForm.feeRate = favorite.product?.feeRate
      showEditDialog.value = true
    }

    const calculateTotalAmount = () => {
      if (!favoriteForm.price || !favoriteForm.quantity) return 0
      return favoriteForm.price * favoriteForm.quantity
    }

    const calculateTotalFee = () => {
      if (!favoriteForm.feeRate || !favoriteForm.quantity) return 0
      return calculateTotalAmount() * favoriteForm.feeRate
    }

    const saveFavorite = async () => {
      try {
        await favoriteFormRef.value.validate()
        savingFavorite.value = true

        const requestData = {
          userId: localStorage.getItem('userId'),
          productNo: favoriteForm.productNo,
          quantity: favoriteForm.quantity,
          account: favoriteForm.account,
          orderName: favoriteForm.orderName || favoriteForm.productName
        }

        const response = await fetch(`/api/like-list/${editingFavorite.value.sn}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(requestData)
        })

        if (response.ok) {
          ElMessage.success('喜好商品更新成功')
          showEditDialog.value = false
          await loadFavorites()
        } else {
          const errorData = await response.json()
          throw new Error(errorData.message || '更新失敗')
        }
      } catch (error) {
        if (error.message !== '驗證失敗') {
          ElMessage.error('更新失敗：' + error.message)
        }
      } finally {
        savingFavorite.value = false
      }
    }

    const deleteFavorite = async (favorite) => {
      try {
        await ElMessageBox.confirm(
          `確定要刪除喜好商品「${favorite.product?.productName || '未知商品'}」嗎？`,
          '確認刪除',
          {
            confirmButtonText: '確定刪除',
            cancelButtonText: '取消',
            type: 'warning',
            dangerouslyUseHTMLString: false
          }
        )

        deletingFavorite.value = true
        
        const response = await fetch(`/api/like-list/${favorite.sn}`, {
          method: 'DELETE'
        })

        if (response.ok) {
          ElMessage.success('喜好商品刪除成功')
          await loadFavorites()
        } else {
          throw new Error('刪除失敗')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('刪除失敗：' + error.message)
        }
      } finally {
        deletingFavorite.value = false
      }
    }

    const handleSearch = () => {
      currentPage.value = 1
    }

    const resetSearch = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      currentPage.value = 1
    }

    const handleSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
    }

    const handleCurrentChange = (page) => {
      currentPage.value = page
    }

    const formatNumber = (num) => {
      if (num === null || num === undefined) return '0'
      return new Intl.NumberFormat('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      }).format(num)
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-TW', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    onMounted(async () => {
      await loadFavorites()
      await loadProducts()
    })

    return {
      loading,
      currentPage,
      pageSize,
      totalFavorites,
      favorites,
      searchForm,
      filteredFavorites,
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      formatNumber,
      formatDate,
      showEditDialog,
      editingFavorite,
      savingFavorite,
      deletingFavorite,
      favoriteFormRef,
      favoriteForm,
      favoriteRules,
      editFavorite,
      saveFavorite,
      deleteFavorite,
      calculateTotalAmount,
      calculateTotalFee
    }
  }
}
</script>

<style scoped>
.favorite-products {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 2.5rem;
  color: #303133;
  margin-bottom: 10px;
  font-weight: 600;
}

.page-header p {
  font-size: 1.1rem;
  color: #606266;
  margin: 0;
}

.search-card {
  margin-bottom: 20px;
}

.favorites-list-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.calculation-preview {
  background-color: #f0f9ff;
  padding: 15px;
  border-radius: 6px;
  border-left: 4px solid #409eff;
}

.calculation-preview p {
  margin: 5px 0;
  color: #303133;
  font-weight: 500;
}

@media (max-width: 768px) {
  .page-header h1 {
    font-size: 2rem;
  }
  
  .page-header p {
    font-size: 1rem;
  }
}
</style>
