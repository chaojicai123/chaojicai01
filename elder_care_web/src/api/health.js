import request from './request'

export function getHealthByElder(elderId, params) {
  return request.get(`/health/elder/${elderId}`, { params })
}

export function getHealthByDateRange(elderId, start, end) {
  return request.get(`/health/elder/${elderId}/range`, { params: { start, end } })
}

/** 管理员获取所有老人的健康数据 */
export function getAllHealthRecords(params) {
  return request.get('/health/all', { params })
}

export function createHealthRecord(data) {
  return request.post('/health', data)
}

export function deleteHealthRecord(id) {
  return request.delete(`/health/${id}`)
}