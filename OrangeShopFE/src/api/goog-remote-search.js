import request from '@/utils/request-no-url'
const tokenGHN = '845cb13c-b64c-11ed-9a6e-422f22df4aa9'
const baseUrlGHN = 'https://dev-online-gateway.ghn.vn/shiip/public-api'
const shopIdGHN = 121789
export function searchAddress(queryString) {
  return request({
    url: `https://rsapi.goong.io/Place/AutoComplete`,
    method: 'get',
    params: {
      input: queryString,
      limit: 5,
      more_compound: true,
      api_key: 'I0m6nfyan0h6Asfd0V7YGycRKedCWxihrGXYk4fX'
    }
  })
}
export function getCities() {
  return request({
    url: `${baseUrlGHN}/master-data/province`,
    method: 'get',
    headers: {
      'Content-Type': 'application/json',
      'token': tokenGHN
    }
  })
}

export function getDistricts(province_id) {
  return request({
    url: `${baseUrlGHN}/master-data/district`,
    method: 'get',
    headers: {
      'Content-Type': 'application/json',
      'token': tokenGHN
    },
    params: {
      'province_id': province_id
    }
  })
}

export function getVillages(district_id) {
  return request({
    url: `${baseUrlGHN}/master-data/ward`,
    method: 'get',
    headers: {
      'Content-Type': 'application/json',
      'token': tokenGHN
    },
    params: {
      'district_id': district_id
    }
  })
}

export function getDetailOrderGHN(orderCode) {
  return request({
    url: `${baseUrlGHN}/v2/shipping-order/detail`,
    method: 'post',
    headers: {
      'Content-Type': 'application/json',
      'token': tokenGHN
    },
    data: orderCode
  })
}
export function getCalculateLeadTime(data) {
  return request({
    url: `${baseUrlGHN}/v2/shipping-order/leadtime`,
    method: 'post',
    headers: {
      'Content-Type': 'application/json',
      'token': tokenGHN,
      'ShopId': shopIdGHN
    },
    data: {
      'from_district_id': 3440,
      'from_ward_code': '13004',
      'to_district_id': data.to_district_id,
      'to_ward_code': data.to_ward_code,
      'service_id': 53321
    }
  })
}
