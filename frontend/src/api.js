import axios from 'axios'

// Spring Security puts the CSRF token in the XSRF-TOKEN cookie; axios echoes it as X-XSRF-TOKEN.
export const http = axios.create({ withXSRFToken: true })

http.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401 && !err.config.url.includes('/api/auth/')) {
      window.location.hash = '#/login'
    }
    err.message = err.response?.data?.message || err.message
    return Promise.reject(err)
  },
)

const data = (p) => p.then((r) => r.data)

export const api = {
  me: () => data(http.get('/api/auth/me')),
  login: (username, password) => data(http.post('/api/auth/login', { username, password })),
  logout: () => http.post('/api/auth/logout'),
  profile: () => data(http.get('/api/profile')),

  media: (albumId) => data(http.get('/api/media', { params: { albumId } })),
  upload: (file, { albumId, caption } = {}, onProgress) => {
    const form = new FormData()
    form.append('file', file)
    form.append('lastModified', String(file.lastModified))
    if (albumId) form.append('albumId', albumId)
    if (caption) form.append('caption', caption)
    return data(http.post('/api/media', form, {
      onUploadProgress: (e) => onProgress?.(e.total ? e.loaded / e.total : 0),
    }))
  },
  updateMedia: (id, patch) => data(http.patch(`/api/media/${id}`, patch)),
  deleteMedia: (id) => http.delete(`/api/media/${id}`),

  albums: () => data(http.get('/api/albums')),
  album: (id) => data(http.get(`/api/albums/${id}`)),
  createAlbum: (body) => data(http.post('/api/albums', body)),
  updateAlbum: (id, body) => data(http.put(`/api/albums/${id}`, body)),
  deleteAlbum: (id) => http.delete(`/api/albums/${id}`),

  notes: () => data(http.get('/api/notes')),
  createNote: (body) => data(http.post('/api/notes', body)),
  deleteNote: (id) => http.delete(`/api/notes/${id}`),

  shares: () => data(http.get('/api/shares')),
  createShare: (body) => data(http.post('/api/shares', body)),
  revokeShare: (id) => http.delete(`/api/shares/${id}`),
  publicShare: (token) => data(http.get(`/api/public/shares/${token}`)),

  users: () => data(http.get('/api/users')),
  createUser: (body) => data(http.post('/api/users', body)),
  deleteUser: (id) => http.delete(`/api/users/${id}`),
}
