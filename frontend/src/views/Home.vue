<template>
  <div class="home">
    <!-- 頁面標題 -->
           <div class="page-header">
         <h1>📋 商品清單</h1>
         <p>瀏覽所有可用的金融商品，將您喜歡的商品加入喜好清單</p>
         <!-- 新增商品按鈕 -->
         <el-button 
           type="primary" 
           @click="addProduct"
           class="add-product-btn"
         >
           <el-icon><Plus /></el-icon>
           新增商品
         </el-button>
       </div>

    

    <!-- 商品列表 -->
    <el-card class="product-list-card">
             <el-table
         :data="filteredProducts"
         style="width: 100%"
         v-loading="loading"
       >
         <el-table-column prop="productName" label="商品名稱" min-width="200" />
                   <el-table-column prop="price" label="價格" width="120">
            <template #default="scope">
              ${{ formatNumber(scope.row.price) }}
            </template>
          </el-table-column>
         <el-table-column prop="feeRate" label="手續費率" width="120">
           <template #default="scope">
             {{ (scope.row.feeRate * 100).toFixed(4) }}%
           </template>
         </el-table-column>
                                      <el-table-column label="操作" width="280" fixed="right">
             <template #default="scope">
               <!-- 加入喜好 -->
               <el-button 
                 size="small" 
                 type="primary" 
                 @click="addToFavorites(scope.row)"
                 :loading="scope.row.addingToFavorites"
                 :disabled="scope.row.isInFavorites"
               >
                 <el-icon><Star /></el-icon>
                 {{ scope.row.isInFavorites ? '已加入' : '加入喜好' }}
               </el-button>
               
                               <!-- 編輯商品 -->
                <el-button 
                  size="small" 
                  type="warning" 
                  @click="editProduct(scope.row)"
                >
                  <el-icon><Edit /></el-icon>
                  修改
                </el-button>
                
                <!-- 刪除商品 -->
                <el-button 
                  size="small" 
                  type="danger" 
                  @click="deleteProduct(scope.row)"
                  :loading="deletingProduct"
                >
                  <el-icon><Delete /></el-icon>
                  刪除
                </el-button>
               
               
             </template>
           </el-table-column>
       </el-table>

      
    </el-card>

    

    <!-- 加入喜好表單對話框 -->
    <el-dialog
      v-model="showAddToFavoritesDialog"
      title="加入喜好清單"
      width="500px"
    >
      <div v-if="productToAdd" class="add-to-favorites-form">
        <!-- 商品基本信息 -->
        <el-card class="product-info-card" shadow="never">
          <div class="product-info">
            <h3>{{ productToAdd.productName }}</h3>
                         <p>價格: ${{ productToAdd.price }}</p>
            <p>手續費率: {{ (productToAdd.feeRate * 100).toFixed(4) }}%</p>
          </div>
        </el-card>

        <!-- 表單 -->
        <el-form
          ref="favoriteFormRef"
          :model="favoriteForm"
          :rules="favoriteRules"
          label-width="100px"
          class="favorite-form"
        >
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
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddToFavoritesDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitAddToFavorites"
            :loading="submitting"
          >
            <el-icon><Star /></el-icon>
            確認加入
          </el-button>
        </span>
             </template>
     </el-dialog>

     <!-- 新增/編輯商品對話框 -->
     <el-dialog
       v-model="showAddProductDialog"
       title="新增商品"
       width="500px"
     >
       <el-form
         ref="productFormRef"
         :model="productForm"
         :rules="productRules"
         label-width="120px"
       >
         <el-form-item label="商品名稱" prop="productName">
           <el-input v-model="productForm.productName" placeholder="請輸入商品名稱" />
         </el-form-item>
         
         <el-form-item label="商品價格" prop="productPrice">
           <el-input-number
             v-model="productForm.productPrice"
             :precision="2"
             :min="0"
             :step="0.01"
             style="width: 100%"
             placeholder="請輸入商品價格"
           />
         </el-form-item>
         
         <el-form-item label="手續費率" prop="feeRate">
           <el-input-number
             v-model="productForm.feeRate"
             :precision="6"
             :min="0"
             :step="0.0001"
             style="width: 100%"
             placeholder="請輸入手續費率"
           />
           <span class="form-hint">例如：0.001425 表示 0.1425%</span>
         </el-form-item>
       </el-form>
       
       <template #footer>
         <span class="dialog-footer">
           <el-button @click="showAddProductDialog = false">取消</el-button>
           <el-button type="primary" @click="saveProduct" :loading="savingProduct">
             新增
           </el-button>
         </span>
       </template>
     </el-dialog>

     <!-- 編輯商品對話框 -->
     <el-dialog
       v-model="showEditProductDialog"
       title="編輯商品"
       width="500px"
     >
       <el-form
         ref="productFormRef"
         :model="productForm"
         :rules="productRules"
         label-width="120px"
       >
         <el-form-item label="商品名稱" prop="productName">
           <el-input v-model="productForm.productName" placeholder="請輸入商品名稱" />
         </el-form-item>
         
         <el-form-item label="商品價格" prop="productPrice">
           <el-input-number
             v-model="productForm.productPrice"
             :precision="2"
             :min="0"
             :step="0.01"
             style="width: 100%"
             placeholder="請輸入商品價格"
           />
         </el-form-item>
         
         <el-form-item label="手續費率" prop="feeRate">
           <el-input-number
             v-model="productForm.feeRate"
             :precision="6"
             :min="0"
             :step="0.0001"
             style="width: 100%"
             placeholder="請輸入手續費率"
           />
           <span class="form-hint">例如：0.001425 表示 0.1425%</span>
         </el-form-item>
       </el-form>
       
       <template #footer>
         <span class="dialog-footer">
           <el-button @click="showEditProductDialog = false">取消</el-button>
           <el-button type="primary" @click="saveProduct" :loading="savingProduct">
             更新
           </el-button>
         </span>
       </template>
     </el-dialog>
   </div>
 </template>

<script>
 import { ref, reactive, computed, onMounted } from 'vue'
 import { useRouter } from 'vue-router'
 import { ElMessage, ElMessageBox } from 'element-plus'
   import { getAvailableProducts, createFavoriteProduct, updateFavoriteProduct, deleteProduct as deleteProductAPI } from '../api/products'

export default {
  name: 'Home',
  setup() {
    const router = useRouter()
              const loading = ref(false)
    
    const products = ref([])
    const userFavorites = ref([])

    const showAddToFavoritesDialog = ref(false)
    const productToAdd = ref(null)
    const submitting = ref(false)
    const favoriteFormRef = ref()

    const showAddProductDialog = ref(false)
    const showEditProductDialog = ref(false)
    const editingProduct = ref(null)
    const savingProduct = ref(false)
    const productFormRef = ref()
    const deletingProduct = ref(false)

    const favoriteForm = reactive({
       quantity: 1,
       account: '',
       orderName: ''
     })

    const productForm = reactive({
       productName: '',
       productPrice: null,
       feeRate: null
     })

         const favoriteRules = {
        quantity: [
          { required: true, message: '請輸入購買數量', trigger: 'blur' }
        ],
        account: [
          { required: true, message: '請輸入扣款帳號', trigger: 'blur' }
        ]
      }

         const productRules = {
        productName: [
          { required: true, message: '請輸入商品名稱', trigger: 'blur' }
        ],
        productPrice: [
          { required: true, message: '請輸入商品價格', trigger: 'blur' }
        ],
        feeRate: [
          { required: true, message: '請輸入手續費率', trigger: 'blur' }
        ]
      }

    



         const filteredProducts = computed(() => {
       // 標記已加入喜好的商品
       return products.value.map(product => ({
         ...product,
         isInFavorites: userFavorites.value.some(fav => fav.product && fav.product.no === product.no),
         addingToFavorites: false
       }))
     })

    const loadProducts = async () => {
      try {
        loading.value = true
                 const data = await getAvailableProducts()
         products.value = data || []
      } catch (error) {
        ElMessage.error('載入商品失敗：' + error.message)
      } finally {
        loading.value = false
      }
         }

    const loadUserFavorites = async () => {
       try {
         const userEmail = localStorage.getItem('userEmail')
         if (userEmail) {
           const userId = localStorage.getItem('userId')
           if (userId) {
             const response = await fetch(`/api/like-list/user/${userId}`)
             if (response.ok) {
               const favorites = await response.json()
               userFavorites.value = favorites || []
             }
           }
         }
       } catch (error) {
         console.error('載入喜好清單失敗:', error)
       }
     }

    const addToFavorites = async (product) => {
       const userEmail = localStorage.getItem('userEmail')
       if (!userEmail) {
         ElMessage.warning('請先登入帳號')
         return
       }

       if (product.isInFavorites) {
         ElMessage.info('該商品已在您的喜好清單中')
         return
       }

       productToAdd.value = product
       favoriteForm.quantity = 1
       favoriteForm.account = localStorage.getItem('userAccount') || ''
       favoriteForm.orderName = product.productName || ''
       showAddToFavoritesDialog.value = true
     }

    const calculateTotalAmount = () => {
       if (!productToAdd.value || !favoriteForm.quantity) return 0
       return productToAdd.value.price * favoriteForm.quantity
     }

    const calculateTotalFee = () => {
      if (!productToAdd.value || !favoriteForm.quantity) return 0
      return calculateTotalAmount() * productToAdd.value.feeRate
         }

    const submitAddToFavorites = async () => {
      try {
        await favoriteFormRef.value.validate()
        
        submitting.value = true
        const userEmail = localStorage.getItem('userEmail')

        const favoriteData = {
          userId: localStorage.getItem('userId'),
          productNo: productToAdd.value.no,
          quantity: favoriteForm.quantity,
          account: favoriteForm.account,
          orderName: favoriteForm.orderName || productToAdd.value.productName
        }

        const response = await fetch('/api/like-list', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'User-Email': userEmail,
          },
          body: JSON.stringify(favoriteData)
        })

        if (response.ok) {
          const result = await response.json()
          ElMessage.success('成功加入喜好清單')

          const product = products.value.find(p => p.no === productToAdd.value.no)
          if (product) {
            product.isInFavorites = true
          }
          userFavorites.value.push(result)

          showAddToFavoritesDialog.value = false
        } else {
          const errorData = await response.json()
          throw new Error(errorData.message || '加入喜好清單失敗')
        }
      } catch (error) {
        if (error.message !== '驗證失敗') {
          ElMessage.error('加入喜好清單失敗：' + error.message)
        }
      } finally {
        submitting.value = false
      }
    }


     const addProduct = () => {
       editingProduct.value = null
       productForm.productName = ''
       productForm.productPrice = null
       productForm.feeRate = null
       showAddProductDialog.value = true
     }

     const editProduct = (product) => {
       // 直接打開編輯對話框
       editingProduct.value = product
       productForm.productName = product.productName
       productForm.productPrice = product.price
       productForm.feeRate = product.feeRate
       showEditProductDialog.value = true
     }

      const saveProduct = async () => {
        try {
          await productFormRef.value.validate()
          savingProduct.value = true

          if (editingProduct.value) {
            await updateFavoriteProduct(editingProduct.value.no, productForm)
            ElMessage.success('商品更新成功')
            showEditProductDialog.value = false
          } else {
            await createFavoriteProduct(productForm)
            ElMessage.success('商品新增成功')
            showAddProductDialog.value = false
          }

          await loadProducts()
        } catch (error) {
          ElMessage.error('保存失敗：' + error.message)
        } finally {
          savingProduct.value = false
        }
      }

      const deleteProduct = async (product) => {
        try {
          await ElMessageBox.confirm(
            `確定要刪除商品「${product.productName}」嗎？\n\n注意：刪除商品會一併刪除所有相關的喜好清單資料！`,
            '確認刪除',
            {
              confirmButtonText: '確定刪除',
              cancelButtonText: '取消',
              type: 'warning',
              dangerouslyUseHTMLString: false
            }
          )

          deletingProduct.value = true
          
          await deleteProductAPI(product.no)
          
          ElMessage.success('商品刪除成功')
          
          await loadProducts()
          await loadUserFavorites()
        } catch (error) {
          if (error !== 'cancel') {
            ElMessage.error('刪除失敗：' + error.message)
          }
        } finally {
          deletingProduct.value = false
        }
      }

    

    

     const formatNumber = (num) => {
       if (num === null || num === undefined) return '0'
       return new Intl.NumberFormat('en-US', {
         minimumFractionDigits: 2,
         maximumFractionDigits: 2
       }).format(num)
     }

    onMounted(async () => {
      await loadProducts()
      await loadUserFavorites()
    })

                   return {
                 loading,
         products,
                 userFavorites,
         filteredProducts,
         addToFavorites,
       
       formatNumber,
       showAddToFavoritesDialog,
       productToAdd,
       submitting,
       favoriteFormRef,
       favoriteForm,
       favoriteRules,
       calculateTotalAmount,
       calculateTotalFee,
       submitAddToFavorites,
        showAddProductDialog,
        showEditProductDialog,
        editingProduct,
        savingProduct,
        deletingProduct,
        productFormRef,
        productForm,
        productRules,
        addProduct,
        editProduct,
        saveProduct,
        deleteProduct,
 
     }
  }
}
</script>

<style scoped>
.home {
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

 .page-header {
   display: flex;
   justify-content: space-between;
   align-items: center;
   margin-bottom: 30px;
 }

 .add-product-btn {
   margin-left: 20px;
 }

 .product-list-card {
   margin-bottom: 20px;
 }



.add-to-favorites-form {
  padding: 20px 0;
}

.product-info-card {
  margin-bottom: 20px;
  background-color: #f8f9fa;
}

.product-info h3 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 1.2rem;
}

.product-info p {
  margin: 5px 0;
  color: #606266;
  font-size: 0.9rem;
}

.favorite-form {
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
