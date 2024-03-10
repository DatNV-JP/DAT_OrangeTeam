import request from '@/utils/request-be-app'

export function getListAddress(params) {
  return request({
    url: 'address/by-user',
    method: 'get',
    params: params
  })
}

export function addAddress(data) {
  return request({
    url: 'address/add-user-address',
    method: 'post',
    data: data
  })
}

export function updateAddress(data) {
  return request({
    url: 'address/update',
    method: 'put',
    data: data
  })
}

// export function updateQuantity(data) {
//   return request({
//     url: 'cart/update',
//     method: 'put',
//     data: data
//   })
// }

export function removeAddress(id) {
  return request({
    url: `address/delete/${id}`,
    method: 'delete'
  })
}

