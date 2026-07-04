import request from './request'

export function getElders(params) {
  return request.get('/elders', { params })
}

export function getElder(id) {
  return request.get(`/elders/${id}`)
}

export function createElder(data) {
  return request.post('/elders', data)
}

export function updateElder(id, data) {
  return request.put(`/elders/${id}`, data)
}

export function deleteElder(id) {
  return request.delete(`/elders/${id}`)
}
