import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { productsApi } from '../api/products'

export const useProductsStore = defineStore('products', () => {
  // 狀態
  const products = ref([])
  const loading = ref(false)
  const error = ref(null)

  const totalProducts = computed(() => products.value.length)
  
  const totalInvestment = computed(() => {
    return products.value.reduce((sum, product) => 
      sum + (product.productPrice * product.purchaseAmount), 0
    )
  })

  const totalFees = computed(() => {
    return products.value.reduce((sum, product) => 
      sum + (product.productPrice * product.purchaseAmount * product.feeRate), 0
    )
  })

  const fetchProducts = async () => {
    try {
      loading.value = true
      error.value = null
      
      const response = await productsApi.getAll()
      products.value = response.data || []
    } catch (err) {
      error.value = err.message || '獲取產品列表失敗'
      console.error('獲取產品失敗:', err)
    } finally {
      loading.value = false
    }
  }

  const addProduct = async (productData) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await productsApi.create(productData)
      const newProduct = response.data
      
      products.value.push(newProduct)
      return newProduct
    } catch (err) {
      error.value = err.message || '添加產品失敗'
      console.error('添加產品失敗:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateProduct = async (id, productData) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await productsApi.update(id, productData)
      const updatedProduct = response.data
      
      const index = products.value.findIndex(p => p.id === id)
      if (index !== -1) {
        products.value[index] = updatedProduct
      }
      
      return updatedProduct
    } catch (err) {
      error.value = err.message || '更新產品失敗'
      console.error('更新產品失敗:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteProduct = async (id) => {
    try {
      loading.value = true
      error.value = null
      
      await productsApi.delete(id)
      
      const index = products.value.findIndex(p => p.id === id)
      if (index !== -1) {
        products.value.splice(index, 1)
      }
    } catch (err) {
      error.value = err.message || '刪除產品失敗'
      console.error('刪除產品失敗:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const searchProducts = async (searchParams) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await productsApi.search(searchParams)
      products.value = response.data || []
      
      return response.data
    } catch (err) {
      error.value = err.message || '搜索產品失敗'
      console.error('搜索產品失敗:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const getProductById = async (id) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await productsApi.getById(id)
      return response.data
    } catch (err) {
      error.value = err.message || '獲取產品詳情失敗'
      console.error('獲取產品詳情失敗:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const getProductsByUser = async (userEmail) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await productsApi.getByUserEmail(userEmail)
      products.value = response.data || []
      
      return response.data
    } catch (err) {
      error.value = err.message || '獲取用戶產品失敗'
      console.error('獲取用戶產品失敗:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const clearError = () => {
    error.value = null
  }

  const reset = () => {
    products.value = []
    loading.value = false
    error.value = null
  }

  return {
    products,
    loading,
    error,
    
    totalProducts,
    totalInvestment,
    totalFees,
    
    fetchProducts,
    addProduct,
    updateProduct,
    deleteProduct,
    searchProducts,
    getProductById,
    getProductsByUser,
    clearError,
    reset
  }
})
