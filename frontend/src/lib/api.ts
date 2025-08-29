import axios from 'axios'

// const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export const api = axios.create({
  baseURL: 'http://localhost:80808/api/v1',
  headers: {
    'Content-Type': 'application/json'
  },
})

// Ajuste os endpoints conforme o seu backend.
/*
export const ParticipantsAPI = {
  list: (params = {}) => api.get('/participants', { params }).then(r => r.data),
  create: (payload) => api.post('/participants', payload).then(r => r.data),
  remove: (id) => api.delete(`/participants/${id}`).then(r => r.data),
}

export const ItemsAPI = {
  toggleDelivered: (itemId, delivered) =>
    api.patch(`/items/${itemId}/delivered`, { delivered }).then(r => r.data),
}
*/
