<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { api } from '../api'
import { ageAt, formatDate } from '../time'

const props = defineProps({
  items: { type: Array, required: true },
  index: { type: Number, required: true },
  birthday: String,
  albums: { type: Array, default: () => [] },
  readonly: Boolean,
})
const emit = defineEmits(['update:index', 'updated', 'deleted'])

const item = computed(() => props.items[props.index])
const caption = ref('')
const albumId = ref('')
const saving = ref(false)
const error = ref('')

watch(item, (m) => {
  if (!m) return emit('update:index', -1)
  caption.value = m.caption || ''
  albumId.value = m.albumId ?? ''
  error.value = ''
}, { immediate: true })

const close = () => emit('update:index', -1)
const go = (d) => {
  const i = props.index + d
  if (i >= 0 && i < props.items.length) emit('update:index', i)
}
const onKey = (e) => {
  if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA' || e.target.tagName === 'SELECT') return
  if (e.key === 'Escape') close()
  if (e.key === 'ArrowLeft') go(-1)
  if (e.key === 'ArrowRight') go(1)
}
onMounted(() => window.addEventListener('keydown', onKey))
onUnmounted(() => window.removeEventListener('keydown', onKey))

async function save() {
  saving.value = true
  error.value = ''
  try {
    const patch = { caption: caption.value }
    if (albumId.value === '') patch.clearAlbum = true
    else patch.albumId = Number(albumId.value)
    emit('updated', await api.updateMedia(item.value.id, patch))
  } catch (e) {
    error.value = e.message
  } finally {
    saving.value = false
  }
}

async function remove() {
  if (!confirm('确定删除这项吗？删除后无法恢复。')) return
  try {
    const m = item.value
    await api.deleteMedia(m.id)
    emit('deleted', m)
  } catch (e) {
    error.value = e.message
  }
}
</script>

<template>
  <div v-if="item" class="overlay" @click.self="close">
    <button class="nav-btn prev" :disabled="index === 0" @click="go(-1)">‹</button>
    <div class="stage" @click.self="close">
      <img v-if="item.type === 'IMAGE'" :src="item.url" :alt="item.caption || item.name" />
      <video v-else-if="item.type === 'VIDEO'" :src="item.url" controls autoplay></video>
      <audio v-else-if="item.type === 'AUDIO'" :src="item.url" controls autoplay></audio>
      <a v-else :href="item.url" target="_blank" class="btn">下载 {{ item.name }}</a>
    </div>
    <button class="nav-btn next" :disabled="index === items.length - 1" @click="go(1)">›</button>
    <aside class="panel">
      <button class="ghost close" @click="close">✕</button>
      <p class="date">{{ formatDate(item.time) }}</p>
      <p v-if="ageAt(birthday, item.time)" class="muted">宝贝 {{ ageAt(birthday, item.time) }}</p>
      <template v-if="readonly">
        <p v-if="item.caption" class="caption">{{ item.caption }}</p>
      </template>
      <template v-else>
        <div class="field">
          <label for="caption">描述</label>
          <textarea id="caption" v-model="caption" placeholder="写点什么…" maxlength="1000"></textarea>
        </div>
        <div class="field">
          <label for="album">相册</label>
          <select id="album" v-model="albumId">
            <option value="">不放入相册</option>
            <option v-for="a in albums" :key="a.id" :value="a.id">{{ a.name }}</option>
          </select>
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <div class="row">
          <button :disabled="saving" @click="save">{{ saving ? '保存中…' : '保存' }}</button>
          <button class="danger" @click="remove">删除</button>
        </div>
      </template>
      <p class="muted small">{{ item.name }}</p>
    </aside>
  </div>
</template>

<style scoped>
.overlay { position: fixed; inset: 0; background: rgba(20, 18, 16, .92); z-index: 100; display: flex; }
.stage { flex: 1; display: flex; align-items: center; justify-content: center; padding: 24px; min-width: 0; }
.stage img, .stage video { max-width: 100%; max-height: calc(100vh - 48px); object-fit: contain; border-radius: 6px; }
.nav-btn { background: transparent; color: #fff; font-size: 48px; padding: 0 16px; align-self: center; }
.nav-btn:hover { background: rgba(255,255,255,.08); }
.nav-btn:disabled { opacity: .2; }
.panel { width: 300px; background: #fff; padding: 20px; overflow-y: auto; position: relative; }
.close { position: absolute; top: 12px; right: 12px; padding: 2px 10px; }
.date { font-size: 18px; font-weight: 600; margin: 8px 0 4px; }
.caption { white-space: pre-wrap; }
.small { font-size: 12px; margin-top: 24px; word-break: break-all; }
@media (max-width: 760px) {
  .overlay { flex-direction: column; }
  .panel { width: 100%; max-height: 45vh; }
  .nav-btn { display: none; }
}
</style>
