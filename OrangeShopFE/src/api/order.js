import request from '@/utils/request-be-app'

export function getOrderDetail(id) {
  return request({
    url: 'order/order-detail',
    method: 'get',
    params: id
  })
}

export function getOrderDetailAdmin(id) {
  return request({
    url: 'admin/order/order-detail',
    method: 'get',
    params: id
  })
}

export function addOrder(data) {
  return request({
    url: 'order/create-order',
    method: 'post',
    data: data
  })
}

export function addOrderAdmin(data) {
  return request({
    url: 'admin/order',
    method: 'post',
    data: data
  })
}

export function getListOrderUser(params) {
  return request({
    url: 'order',
    method: 'get',
    params: params
  })
}

export function getListOrderAdmin(params) {
  return request({
    url: 'admin/order',
    method: 'get',
    params: params
  })
}

export function cancelOrder(data) {
  return request({
    url: 'order/cancel-order',
    method: 'put',
    data: data
  })
}

export function undoCancelOrder(id) {
  return request({
    url: `order/undo-cancel/${id}`,
    method: 'put'
  })
}

export function updateOrder(data) {
  return request({
    url: 'admin/order/update-status',
    method: 'put',
    data: data
  })
}

export function confirmOrder(id, data, choose) {
  let url = `admin/order/confirm-And-Shipping/${id}`
  if (!choose) {
    url = `admin/order/approve-cancel-order/${id}`
  }
  return request({
    url: url,
    method: 'put',
    data: data
  })
}

