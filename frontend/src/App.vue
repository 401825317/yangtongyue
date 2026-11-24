<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { session } from './session'
import { api } from './api'

const route = useRoute()
const router = useRouter()
const showNav = computed(() => !route.meta.public && session.user)

async function logout() {
  await api.logout()
  session.user = null
  router.push('/login')
}
</script>

<template>
  <header v-if="showNav" class="nav">
    <div class="nav-inner">
      <router-link to="/" class="brand">{{ session.profile?.babyName || '成长时光轴' }}</router-link>
      <nav>
        <router-link to="/" exact-active-class="active">时光轴</router-link>
        <router-link to="/albums" active-class="active">相册</router-link>
        <router-link to="/notes" active-class="active">爸妈留言</router-link>
        <router-link to="/shares" active-class="active">分享</router-link>
        <router-link v-if="session.user.role === 'ADMIN'" to="/family" active-class="active">家人</router-link>
      </nav>
      <div class="user">
        <span class="muted">{{ session.user.displayName }}</span>
        <button class="ghost" @click="logout">退出</button>
      </div>
    </div>
  </header>
  <router-view />
</template>

<style scoped>
.nav { background: #fff; border-bottom: 1px solid var(--line); position: sticky; top: 0; z-index: 10; }
.nav-inner { max-width: 1100px; margin: 0 auto; padding: 10px 16px; display: flex; align-items: center; gap: 24px; flex-wrap: wrap; }
.brand { font-weight: 700; font-size: 18px; color: var(--text); }
nav { display: flex; gap: 4px; flex: 1; flex-wrap: wrap; }
nav a { color: var(--muted); padding: 6px 12px; border-radius: 8px; }
nav a.active, nav a:hover { color: var(--text); background: var(--bg); }
.user { display: flex; align-items: center; gap: 10px; }
.user button { padding: 4px 12px; }
</style>
