import request from './request'

export function getStaffList(params) {
  return request.get('/staff', { params })
}

export function getAvailableStaff() {
  return request.get('/staff/available')
}

export function getStaff(id) {
  return request.get(`/staff/${id}`)
}

export function createStaff(data) {
  return request.post('/staff', data)
}

export function updateStaff(id, data) {
  return request.put(`/staff/${id}`, data)
}

export function deleteStaff(id) {
  return request.delete(`/staff/${id}`)
}
