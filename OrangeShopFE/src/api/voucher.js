import request from '@/utils/request-be-app'

export function getListVoucher(params) {
  return request({
    url: 'admin/voucher',
    method: 'get',
    params: params
  })
}
export function searchVoucher(params) {
  return request({
    url: 'admin/voucher/search',
    method: 'get',
    params: params
  })
}

export function getListVoucherStatus(params) {
  return request({
    url: 'admin/voucher',
    method: 'get',
    params: params
  })
}

export function getListVoucherUser(params) {
  return request({
    url: 'voucher',
    method: 'get',
    params: params
  })
}

export function addVoucher(data) {
  return request({
    url: 'admin/voucher/create',
    method: 'post',
    data: data
  })
}

export function checkVoucher(data) {
  return request({
    url: 'voucher/check-voucher',
    method: 'post',
    data: data
  })
}

export function getListGroup() {
  return request({
    url: 'admin/voucher/get-groups',
    method: 'get'
  })
}
export function getListCustomerByGroup(params) {
  return request({
    url: 'admin/voucher/users-by-group',
    method: 'get',
    params: params
  })
}

export function getVoucherById(params) {
  return request({
    url: 'admin/voucher/by-id',
    method: 'get',
    params: params
  })
}

export function deleteVoucher(id) {
  return request({
    url: `admin/voucher/delete/${id}`,
    method: 'delete'
  })
}
export function reactivateVoucher(id) {
  return request({
    url: `admin/voucher/reactivate/${id}`,
    method: 'put'
  })
}
