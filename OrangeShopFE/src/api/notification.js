import request from '@/utils/request-be-app'
export function deleteNotification(data) {
  return request({
    url: 'notification/websocket',
    method: 'post',
    data: data
  })
}

export function getNotification(param) {
  return request({
    url: 'notification/websocket',
    method: 'get',
    params: { hashKey: param }
  })
}

export function readNotification(data) {
  return request({
    url: 'notification/websocket/read',
    method: 'post',
    data: data
  })
}

export function readAllNotification(param) {
  return request({
    url: 'notification/websocket/read',
    method: 'get',
    params: { hashKey: param }
  })
}

