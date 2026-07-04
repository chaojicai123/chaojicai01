import request from './request'

export function getAppointments(params) {
  return request.get('/appointments', { params })
}

export function getAppointment(id) {
  return request.get(`/appointments/${id}`)
}

export function createAppointment(data) {
  return request.post('/appointments', data)
}

export function updateAppointmentStatus(id, status) {
  return request.patch(`/appointments/${id}/status`, null, { params: { status } })
}

export function assignStaff(id, staffId) {
  return request.patch(`/appointments/${id}/assign`, null, { params: { staffId } })
}
