import request from '@/utils/request-be-app'

export function getListPaymentMethod() {
  return request({
    url: 'payment-type',
    method: 'get'
  })
}

export function getListShippingMethod() {
  return request({
    url: 'shipping-method',
    method: 'get'
  })
}
export function redirectPayment(data) {
  return request({
    url: 'payment/create-url',
    method: 'post',
    data: data
  })
}

export function getPaymentInfo(data) {
  return request({
    url: 'payment/payment-info',
    method: 'get',
    params: data
  })
}

export function getCalculateShip(data) {
  return request({
    url: 'shipping/calculate-fee',
    method: 'post',
    data: data
  })
}
// export function getListProductByCategory(params) {
//   return request({
//     url: 'product/by-category',
//     method: 'get',
//     params: params
//   })
// }
//
// export function getProductDetail(params) {
//   return request({
//     url: 'product/product-detail',
//     method: 'get',
//     params: params
//   })
// }

