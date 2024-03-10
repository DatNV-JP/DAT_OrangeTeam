import request from '@/utils/request-be-app'

// export function getListProduct(params) {
//   return request({
//     url: 'product',
//     method: 'get',
//     params: params
//   })
// }

export function getProductDetail(id) {
  return request({
    url: `test/product-detail/find-by-id/${id}`,
    method: 'get'
  })
}

