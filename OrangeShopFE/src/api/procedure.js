import request from '@/utils/request-be-app'

export function getAllCountStatus(params) {
  return request({
    url: 'admin/procedure/get-count-all-status-by-date',
    method: 'get',
    params: params
  })
}

export function getTotalAmountByMonth() {
  return request({
    url: 'admin/procedure/get-total-amount-by-month',
    method: 'get'
  })
}

export function getAmountToDay(param) {
  return request({
    url: 'admin/procedure/total-amount-by-stt',
    method: 'get',
    params: param
  })
}
export function getCountOrdersByWeek() {
  return request({
    url: 'admin/procedure/get-count-orders-by-week',
    method: 'get'
  })
}

export function getTopProductDetailsByDateStatus(param) {
  return request({
    url: 'admin/procedure/get-top-productdetail',
    method: 'get',
    params: param
  })
}

export function getOrderByStatusAndUserId() {
  return request({
    url: 'user/procedure/get-order-status-by-id',
    method: 'get'
  })
}
