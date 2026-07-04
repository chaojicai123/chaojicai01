import request from './request'

export function getNotifications(params) {
  return request.get('/notifications', { params })
}

export function getUnreadCount(targetType, targetId) {
  return request.get('/notifications/unread-count', { params: { targetType, targetId } })
}

export function createNotification(data) {
  return request.post('/notifications', data)
}

export function markRead(id) {
  return request.patch(`/notifications/${id}/read`)
}
