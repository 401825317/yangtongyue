<script setup>
import { onMounted, ref } from 'vue'
import { api } from '../api'

const shares = ref([])
const albums = ref([])
const albumId = ref('')
const expires = ref('30')
const error = ref('')
const copied = ref(null)

onMounted(async () => {
  ;[shares.value, albums.value] = await Promise.all([api.shares(), api.albums()])
})

const linkOf = (s) => `${location.origin}${location.pathname}#/s/${s.token}`

async function create() {
  error.value = ''
  try {
    const s = await api.createShare({
      albumId: albumId.value ? Number(albumId.value) : null,
      expiresInDays: expires.value ? Number(expires.value) : null,
    })
    shares.value.unshift(s)
    copy(s)
  } catch (e) {
    error.value = e.message
  }
}
async function copy(s) {
  try {
    await navigator.clipboard.writeText(linkOf(s))
    copied.value = s.id
    setTimeout(() => (copied.value = null), 2000)
  } catch {
    prompt('复制分享链接', linkOf(s))
  }
}
async function revoke(s) {
  if (!confirm('撤销后这个链接将无法再访问，确定吗？')) return
  await api.revokeShare(s.id)
  shares.value = shares.value.filter((x) => x.id !== s.id)
}
const fmt = (ms) => (ms ? new Date(ms).toLocaleDateString('zh-CN') : '永久有效')
</script>

<template>
  <div class="page">
    <div class="page-head"><h2>分享给亲友</h2></div>
    <form class="card row create" @submit.prevent="create">
      <div>
        <label for="share-album">分享内容</label>
        <select id="share-album" v-model="albumId">
          <option value="">整个时光轴</option>
          <option v-for="a in albums" :key="a.id" :value="a.id">相册：{{ a.name }}</option>
        </select>
      </div>
      <div>
        <label for="share-exp">有效期</label>
        <select id="share-exp" v-model="expires">
          <option value="1">1 天</option>
          <option value="7">7 天</option>
          <option value="30">30 天</option>
          <option value="">永久</option>
        </select>
      </div>
      <button type="submit">生成链接</button>
    </form>
    <p class="muted">拿到链接的人无需登录即可查看（只读）。</p>
    <p v-if="error" class="error">{{ error }}</p>
    <div v-if="!shares.length" class="empty">还没有分享链接</div>
    <table v-else class="card">
      <thead><tr><th>内容</th><th>有效期至</th><th>状态</th><th></th></tr></thead>
      <tbody>
        <tr v-for="s in shares" :key="s.id">
          <td>{{ s.albumName ? `相册：${s.albumName}` : '整个时光轴' }}</td>
          <td>{{ fmt(s.expiresAt) }}</td>
          <td>{{ s.active ? '有效' : '已过期' }}</td>
          <td class="row actions">
            <a :href="linkOf(s)" target="_blank">打开</a>
            <button class="ghost" @click="copy(s)">{{ copied === s.id ? '已复制' : '复制链接' }}</button>
            <button class="danger" @click="revoke(s)">撤销</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.create { align-items: flex-end; margin-bottom: 8px; }
.create > div { min-width: 180px; }
.actions { justify-content: flex-end; }
.actions button { padding: 4px 10px; font-size: 13px; }
table.card { padding: 0; }
</style>
