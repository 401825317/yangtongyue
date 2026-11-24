<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '../api'
import { loadSession } from '../session'

const route = useRoute()
const router = useRouter()
const username = ref('')
const password = ref('')
const error = ref('')
const busy = ref(false)

async function submit() {
  error.value = ''
  busy.value = true
  try {
    await api.login(username.value, password.value)
    await loadSession()
    router.replace(route.query.next || '/')
  } catch (e) {
    error.value = e.message
  } finally {
    busy.value = false
  }
}
</script>

<template>
  <div class="login">
    <form class="card" @submit.prevent="submit">
      <h1>成长时光轴</h1>
      <p class="muted">记录宝贝成长的每一刻</p>
      <div class="field">
        <label for="username">用户名</label>
        <input id="username" v-model="username" autocomplete="username" required />
      </div>
      <div class="field">
        <label for="password">密码</label>
        <input id="password" v-model="password" type="password" autocomplete="current-password" required />
      </div>
      <p v-if="error" class="error">{{ error }}</p>
      <button type="submit" :disabled="busy" style="width: 100%">{{ busy ? '登录中…' : '登录' }}</button>
    </form>
  </div>
</template>

<style scoped>
.login {
  min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 16px;
  background: url('/background.jpg') center / cover no-repeat;
}
form { width: 100%; max-width: 360px; box-shadow: 0 10px 40px rgba(0, 0, 0, .15); }
h1 { margin: 0 0 4px; }
p.muted { margin: 0 0 20px; }
</style>
