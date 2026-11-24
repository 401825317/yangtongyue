import { reactive } from 'vue'
import { api } from './api'

export const session = reactive({ user: null, profile: null, loaded: false })

export async function loadSession() {
  try {
    session.user = await api.me()
    session.profile = await api.profile()
  } catch {
    session.user = null
  }
  session.loaded = true
  return session.user
}
