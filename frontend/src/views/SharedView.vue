<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '../api'
import MediaTimeline from '../components/MediaTimeline.vue'

const route = useRoute()
const view = ref(null)
const error = ref('')

onMounted(async () => {
  try {
    view.value = await api.publicShare(route.params.token)
  } catch {
    error.value = '链接不存在、已过期或已被撤销'
  }
})
</script>

<template>
  <div class="hero">
    <h1>{{ view?.babyName || '成长时光轴' }}</h1>
    <p v-if="view?.albumName">{{ view.albumName }}</p>
  </div>
  <div class="page">
    <div v-if="error" class="empty">{{ error }}</div>
    <div v-else-if="!view" class="empty">加载中…</div>
    <MediaTimeline v-else :items="view.media" :birthday="view.birthday" readonly />
  </div>
</template>

<style scoped>
.hero { background: url('/background.jpg') center / cover no-repeat; color: #fff; text-align: center; padding: 72px 16px; text-shadow: 0 2px 12px rgba(0,0,0,.5); }
.hero h1 { margin: 0; font-size: 40px; }
.hero p { margin: 8px 0 0; font-size: 18px; }
</style>
