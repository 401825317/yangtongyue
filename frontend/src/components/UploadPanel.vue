<script setup>
import { computed, ref } from 'vue'
import { api } from '../api'

const props = defineProps({
  albums: { type: Array, default: () => [] },
  albumId: [Number, String],
})
const emit = defineEmits(['uploaded', 'close'])

const target = ref(props.albumId ?? '')
const queue = ref([])
const running = ref(false)
const dragging = ref(false)
const done = computed(() => queue.value.filter((q) => q.status === 'done').length)

function add(files) {
  for (const f of files) queue.value.push({ file: f, progress: 0, status: 'pending', error: '' })
}
function onDrop(e) {
  dragging.value = false
  add(e.dataTransfer.files)
}

async function start() {
  running.value = true
  for (const q of queue.value) {
    if (q.status === 'done') continue
    q.status = 'uploading'
    try {
      const media = await api.upload(q.file, { albumId: target.value || undefined }, (p) => (q.progress = p))
      q.status = 'done'
      q.progress = 1
      emit('uploaded', media)
    } catch (e) {
      q.status = 'error'
      q.error = e.message
    }
  }
  running.value = false
}
</script>

<template>
  <div class="card upload">
    <div class="row head">
      <strong>上传照片 / 视频</strong>
      <button class="ghost" @click="emit('close')">收起</button>
    </div>
    <label
      class="drop"
      :class="{ dragging }"
      @dragover.prevent="dragging = true"
      @dragleave="dragging = false"
      @drop.prevent="onDrop"
    >
      <input type="file" multiple accept="image/*,video/*,audio/*" hidden @change="add($event.target.files); $event.target.value = ''" />
      点击选择或拖拽文件到这里
      <span class="muted">照片会按拍摄时间自动排进时光轴</span>
    </label>
    <div class="row">
      <label for="target" style="margin: 0">放入相册</label>
      <select id="target" v-model="target" style="width: auto">
        <option value="">不放入相册</option>
        <option v-for="a in albums" :key="a.id" :value="a.id">{{ a.name }}</option>
      </select>
      <span style="flex: 1"></span>
      <span v-if="queue.length" class="muted">{{ done }}/{{ queue.length }}</span>
      <button :disabled="running || !queue.length || done === queue.length" @click="start">
        {{ running ? '上传中…' : '开始上传' }}
      </button>
    </div>
    <ul v-if="queue.length" class="queue">
      <li v-for="(q, i) in queue" :key="i">
        <span class="name">{{ q.file.name }}</span>
        <span v-if="q.status === 'error'" class="error">{{ q.error }}</span>
        <span v-else-if="q.status === 'done'" class="ok">✓</span>
        <progress v-else :value="q.progress" max="1"></progress>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.upload { margin-bottom: 24px; }
.head { justify-content: space-between; margin-bottom: 12px; }
.head button { padding: 4px 12px; }
.drop { display: flex; flex-direction: column; gap: 4px; align-items: center; justify-content: center; border: 2px dashed var(--line);
  border-radius: var(--radius); padding: 28px; margin-bottom: 14px; cursor: pointer; color: var(--text); font-size: 15px; }
.drop.dragging, .drop:hover { border-color: var(--accent); background: #fff7f3; }
.drop .muted { font-size: 13px; }
.queue { list-style: none; padding: 0; margin: 14px 0 0; max-height: 200px; overflow-y: auto; }
.queue li { display: flex; gap: 10px; align-items: center; padding: 4px 0; font-size: 14px; }
.name { flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
progress { width: 120px; }
.ok { color: #2e8b57; }
</style>
