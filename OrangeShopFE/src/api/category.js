import request from '@/utils/request-be-app'

export function getAllCategory() {
  return request({
    url: 'category',
    method: 'get'
  })
}

export function addCategory(data) {
  return request({
    url: 'admin/category/create',
    method: 'post',
    data: data
  })
}

export function updateCategory(data) {
  return request({
    url: 'admin/category/update',
    method: 'put',
    data: data
  })
}

export function removeCategory(id) {
  return request({
    url: 'admin/category/delete/' + id,
    method: 'delete'
  })
}

export function getAllCategoryUseParam(param) {
  return request({
    url: 'category',
    method: 'get',
    params: param
  })
}

