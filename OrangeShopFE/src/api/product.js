import request from '@/utils/request-be-app'

export function getListProduct(params) {
  return request({
    url: 'product',
    method: 'get',
    params: params
  })
}

export function getListProductByCategory(params) {
  return request({
    url: 'product/by-category',
    method: 'get',
    params: params
  })
}

export function getProductDetail(params) {
  return request({
    url: 'product/product-detail',
    method: 'get',
    params: params
  })
}

export function getProductByFilter(params) {
  return request({
    url: 'product/get-product-by-filter',
    method: 'get',
    params: params
  })
}

export function addProduct(data) {
  return request({
    url: 'admin/product/create-product',
    method: 'post',
    data: data
  })
}

export function deleteProduct(data) {
  return request({
    url: 'admin/product/delete-product',
    method: 'put',
    data: data
  })
}

export function deleteProductDetail(data) {
  return request({
    url: 'admin/product/delete-product-detail',
    method: 'put',
    data: data
  })
}

export function updateProduct(data) {
  return request({
    url: 'admin/product/update-product',
    method: 'put',
    data: data
  })
}

export function searchProduct(params) {
  return request({
    url: 'product/search',
    method: 'get',
    params: params
  })
}

