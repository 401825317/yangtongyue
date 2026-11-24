<script setup>
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'
import { session } from '../session'
import { ageAt } from '../time'
import MediaTimeline from '../components/MediaTimeline.vue'
import UploadPanel from '../components/UploadPanel.vue'

const props = defineProps({ id: String })
const router = useRouter()
const items = ref([])
const albums = ref([])
const album = ref(null)
const loading = ref(true)
const error = ref('')
const showUpload = ref(false)

const albumId = computed(() => (props.id ? Number(props.id) : null))
const birthday = computed(() => session.profile?.birthday)
const today = computed(() => ageAt(birthday.value, Date.now()))

async function load() {
  loading.value = true
  error.value = ''
  try {
    const [media, list] = await Promise.all([api.media(albumId.value ?? undefined), api.albums()])
    items.value = media
    albums.value = list
    album.value = albumId.value ? list.find((a) => a.id === albumId.value) ?? null : null
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}
watch(albumId, load, { immediate: true })

function sortItems() {
  items.value.sort((a, b) => b.time - a.time || b.id - a.id)
}
function onUploaded(m) {
  if (albumId.value && m.albumId !== albumId.value) return
  items.value.push(m)
  sortItems()
}
function onUpdated(m) {
  const i = items.value.findIndex((x) => x.id === m.id)
  if (i < 0) return
  if (albumId.value && m.albumId !== albumId.value) items.value.splice(i, 1)
  else items.value[i] = m
}
function onDeleted(m) {
  items.value = items.value.filter((x) => x.id !== m.id)
}
async function removeAlbum() {
  if (!confirm(`删除相册「${album.value.name}」？相册里的照片会保留在时光轴上。`)) return
  await api.deleteAlbum(album.value.id)
  router.push('/albums')
}
</script>

<template>
  <div class="page">
    <div class="page-head">
      <div v-if="album">
        <router-link to="/albums" class="muted">← 全部相册</router-link>
        <h2>{{ album.name }}</h2>
        <p v-if="album.description" class="muted" style="margin: 4px 0 0">{{ album.description }}</p>
      </div>
      <div v-else-if="!albumId">
        <h2>{{ session.profile?.babyName }} 的时光轴</h2>
        <p v-if="today" class="muted" style="margin: 4px 0 0">今天 {{ today }} 啦</p>
      </div>
      <div class="row">
        <button v-if="album" class="danger" @click="removeAlbum">删除相册</button>
        <button v-if="!showUpload" @click="showUpload = true">上传</button>
      </div>
    </div>
    <UploadPanel v-if="showUpload" :albums="albums" :album-id="albumId ?? ''" @uploaded="onUploaded" @close="showUpload = false" />
    <p v-if="error" class="error">{{ error }}</p>
    <div v-if="loading" class="empty">加载中…</div>
    <MediaTimeline v-else :items="items" :birthday="birthday" :albums="albums" @updated="onUpdated" @deleted="onDeleted" />
  </div>
</template>
