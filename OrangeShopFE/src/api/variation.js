import request from '@/utils/request-be-app'

export function getListVariation(params) {
  return request({
    url: 'admin/variation/by-category',
    method: 'get',
    params: params
  })
}
