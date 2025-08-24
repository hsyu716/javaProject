import axios from 'axios'

// 創建 axios 實例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use(
  (config) => {
    const userEmail = localStorage.getItem('userEmail')
    if (userEmail) {
      config.headers['User-Email'] = userEmail
    }
    
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response) {
      const message = error.response.data?.message || error.response.data || '請求失敗'
      return Promise.reject(new Error(message))
    } else if (error.request) {
      return Promise.reject(new Error('網絡錯誤，請檢查網絡連接'))
    } else {
      return Promise.reject(new Error(error.message || '未知錯誤'))
    }
  }
)

/**
 * 獲取所有喜好金融商品
 */
export const getFavoriteProducts = async () => {
  try {
    const response = await api.get('/favorites')
    return response
  } catch (error) {
    throw error
  }
}

/**
 * 獲取所有可用商品
 */
export const getAvailableProducts = async () => {
  try {
    const response = await api.get('/products')
    return response
  } catch (error) {
    throw error
  }
}

/**
 * 根據ID獲取喜好金融商品
 */
export const getFavoriteProductById = async (id) => {
  try {
    const response = await api.get(`/favorites/${id}`)
    return response
  } catch (error) {
    throw error
  }
}

/**
 * 根據用戶郵箱獲取喜好金融商品
 */
export const getFavoriteProductsByUser = async (email) => {
  try {
    const response = await api.get(`/favorites/user/${email}`)
    return response
  } catch (error) {
    throw error
  }
}

/**
 * 創建新的商品
 */
export const createFavoriteProduct = async (productData) => {
  try {
    // 轉換字段名稱以匹配後端實體
    const requestData = {
      productName: productData.productName,
      price: productData.productPrice,
      feeRate: productData.feeRate
    }
    const response = await api.post('/products', requestData)
    return response
  } catch (error) {
    throw error
  }
}

/**
 * 更新商品
 */
export const updateFavoriteProduct = async (id, productData) => {
  try {
    const requestData = {
      productName: productData.productName,
      price: productData.productPrice,
      feeRate: productData.feeRate
    }
    const response = await api.put(`/products/${id}`, requestData)
    return response
  } catch (error) {
    throw error
  }
}

/**
 * 刪除商品
 */
export const deleteProduct = async (id) => {
  try {
    await api.delete(`/products/${id}`)
    return true
  } catch (error) {
    throw error
  }
}

/**
 * 批量刪除商品
 */
export const batchDeleteProducts = async (ids) => {
  try {
    const deletePromises = ids.map(id => deleteProduct(id))
    await Promise.all(deletePromises)
    return true
  } catch (error) {
    throw error
  }
}

/**
 * 搜尋喜好金融商品
 */
export const searchFavoriteProducts = async (searchParams) => {
  try {
    const response = await api.get('/favorites/search', { params: searchParams })
    return response
  } catch (error) {
    throw error
  }
}


// 創建 productsApi 對象，包含所有需要的 API 方法
export const productsApi = {
  // 獲取所有產品
  getAll: getFavoriteProducts,
  
  // 根據 ID 獲取產品
  getById: getFavoriteProductById,
  
  // 根據用戶郵箱獲取產品
  getByUserEmail: getFavoriteProductsByUser,
  
  // 創建產品
  create: createFavoriteProduct,
  
  // 更新產品
  update: updateFavoriteProduct,
  
  // 刪除產品
  delete: deleteProduct,
  
  // 搜索產品
  search: searchFavoriteProducts,
  
}
export default api

