import request from '@/utils/request-be-app'

export function deleteFileUpload(folder, filename) {
  return request({
    url: `files/${folder}/${filename}`,
    method: 'delete'
  })
}
