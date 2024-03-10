import request from '@/utils/request-be-app'

export function login(data) {
  return request({
    url: 'authenticate',
    method: 'post',
    data
  })
}

export function getInfo(token) {
  return request({
    url: 'user/info',
    method: 'get'
  })
}

export function createAccount(data) {
  return request({
    url: 'user/create',
    method: 'post',
    data
  })
}

export function getOTPCode(param) {
  return request({
    url: 'user/forgot-pass',
    method: 'get',
    params: param
  })
}

export function findUserByUsername(param) {
  return request({
    url: 'test/admin/user/find-username',
    method: 'get',
    params: param
  })
}

export function findUserById(id) {
  return request({
    url: `user/find-by-id/${id}`,
    method: 'get'
  })
}
export function confirmOTP(param) {
  return request({
    url: 'user/confirm-otp',
    method: 'get',
    params: param
  })
}

export function changePass(data) {
  return request({
    url: 'user/change-password',
    method: 'put',
    data
  })
}
export function updateUserProfile(data) {
  return request({
    url: 'user/update-user',
    method: 'put',
    data
  })
}

export function getAllUserStatus(param) {
  return request({
    url: 'user',
    method: 'get',
    params: param
  })
}

export function deleteByid(param) {
  return request({
    url: 'test/admin/user/dis-or-act-user',
    method: 'get',
    params: param
  })
}

export function adminUpdateUser(data) {
  return request({
    url: 'test/admin/user/update-user',
    method: 'put',
    data
  })
}
