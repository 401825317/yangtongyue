<script setup>
import { onMounted, ref } from 'vue'
import { api } from '../api'

const albums = ref([])
const name = ref('')
const description = ref('')
const error = ref('')
const creating = ref(false)

const load = async () => (albums.value = await api.albums())
onMounted(load)

async function create() {
  error.value = ''
  try {
    albums.value.push(await api.createAlbum({ name: name.value, description: description.value }))
    name.value = ''
    description.value = ''
    creating.value = false
  } catch (e) {
    error.value = e.message
  }
}
</script>

<template>
  <div class="page">
    <div class="page-head">
      <h2>相册</h2>
      <button v-if="!creating" @click="creating = true">新建相册</button>
    </div>
    <form v-if="creating" class="card new" @submit.prevent="create">
      <div class="field">
        <label for="album-name">名称</label>
        <input id="album-name" v-model="name" placeholder="例如：一岁生日" required maxlength="100" />
      </div>
      <div class="field">
        <label for="album-desc">描述（可选）</label>
        <input id="album-desc" v-model="description" maxlength="1000" />
      </div>
      <p v-if="error" class="error">{{ error }}</p>
      <div class="row">
        <button type="submit">创建</button>
        <button type="button" class="ghost" @click="creating = false">取消</button>
      </div>
    </form>
    <div v-if="!albums.length" class="empty">还没有相册，新建一个来整理照片吧</div>
    <div class="grid">
      <router-link v-for="a in albums" :key="a.id" :to="`/albums/${a.id}`" class="album">
        <div class="cover" :style="a.coverUrl ? { backgroundImage: `url('${a.coverUrl}')` } : null">
          <span v-if="!a.coverUrl">暂无照片</span>
        </div>
        <div class="info">
          <strong>{{ a.name }}</strong>
          <span class="muted">{{ a.count }} 项</span>
        </div>
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.new { margin-bottom: 24px; max-width: 480px; }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; }
.album { background: #fff; border: 1px solid var(--line); border-radius: var(--radius); overflow: hidden; color: var(--text); transition: box-shadow .2s; }
.album:hover { box-shadow: 0 6px 20px rgba(0,0,0,.08); }
.cover { aspect-ratio: 4 / 3; background: #f1ece5 center / cover no-repeat; display: flex; align-items: center; justify-content: center; color: var(--muted); }
.info { padding: 12px 14px; display: flex; justify-content: space-between; gap: 8px; }
</style>
