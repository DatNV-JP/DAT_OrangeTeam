import request from '@/utils/request-be-app'

export function getListPromotion(params) {
  return request({
    url: 'promotion/promotion-list-active',
    method: 'get',
    params: params
  })
}

export function getPromotionDetail(id) {
  return request({
    url: 'promotion/promotion',
    method: 'get',
    params: id
  })
}

export function searchPromotion(params) {
  return request({
    url: 'promotion/search',
    method: 'get',
    params: params
  })
}

export function addPromotion(data) {
  return request({
    url: 'promotion/promotion-create',
    method: 'post',
    data: data
  })
}
export function addAndOverwritePromotion(data) {
  return request({
    url: 'promotion/promotion-create/promotion-duplicate',
    method: 'post',
    data: data
  })
}
export function checkPromotionExit(data) {
  return request({
    url: 'promotion/check-exit-promotion',
    method: 'post',
    data: data
  })
}
export function deletePromotion(params) {
  return request({
    url: 'promotion/promotion-delete',
    method: 'delete',
    params: params
  })
}

export function reactivationPromotion(data) {
  return request({
    url: 'promotion/promotion-reset',
    method: 'put',
    data: data
  })
}
