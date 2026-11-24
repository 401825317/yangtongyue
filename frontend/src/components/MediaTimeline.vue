<script setup>
import { computed, ref } from 'vue'
import { ageAt, groupByMonth } from '../time'
import MediaViewer from './MediaViewer.vue'

const props = defineProps({
  items: { type: Array, required: true },
  birthday: String,
  albums: { type: Array, default: () => [] },
  readonly: Boolean,
})
const emit = defineEmits(['updated', 'deleted'])

const groups = computed(() => groupByMonth(props.items))
const openIndex = ref(-1)

function open(item) {
  openIndex.value = props.items.indexOf(item)
}
</script>

<template>
  <div v-if="!items.length" class="empty">还没有照片或视频</div>
  <section v-for="g in groups" :key="g.key" class="month">
    <div class="month-head">
      <span class="dot"></span>
      <h3>{{ g.label }}</h3>
      <span v-if="ageAt(birthday, g.time)" class="age">{{ ageAt(birthday, g.time) }}</span>
      <span class="muted count">{{ g.items.length }} 项</span>
    </div>
    <div class="grid">
      <button v-for="m in g.items" :key="m.id" class="tile" :title="m.caption || m.name" @click="open(m)">
        <img v-if="m.type === 'IMAGE'" :src="m.url" :alt="m.caption || m.name" loading="lazy" />
        <video v-else-if="m.type === 'VIDEO'" :src="m.url" preload="metadata" muted></video>
        <div v-else class="placeholder">{{ m.type === 'AUDIO' ? '♪ 音频' : '文件' }}</div>
        <span v-if="m.type === 'VIDEO'" class="badge">▶</span>
        <span v-if="m.caption" class="caption">{{ m.caption }}</span>
      </button>
    </div>
  </section>
  <MediaViewer
    v-if="openIndex >= 0"
    v-model:index="openIndex"
    :items="items"
    :birthday="birthday"
    :albums="albums"
    :readonly="readonly"
    @updated="emit('updated', $event)"
    @deleted="emit('deleted', $event)"
  />
</template>

<style scoped>
.month { position: relative; padding-left: 24px; border-left: 2px solid var(--line); margin-left: 6px; padding-bottom: 28px; }
.month-head { display: flex; align-items: baseline; gap: 10px; margin-bottom: 12px; position: relative; }
.dot { position: absolute; left: -32px; top: 6px; width: 14px; height: 14px; border-radius: 50%; background: var(--accent); border: 3px solid var(--bg); }
h3 { margin: 0; font-size: 18px; }
.age { background: #fde8e1; color: var(--accent-dark); padding: 2px 10px; border-radius: 999px; font-size: 13px; }
.count { font-size: 13px; }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); gap: 8px; }
.tile { position: relative; padding: 0; aspect-ratio: 1; border-radius: 10px; overflow: hidden; background: #eee; border: none; }
.tile:hover { background: #eee; }
.tile img, .tile video { width: 100%; height: 100%; object-fit: cover; display: block; transition: transform .2s; }
.tile:hover img, .tile:hover video { transform: scale(1.04); }
.placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; color: var(--muted); }
.badge { position: absolute; top: 8px; right: 8px; background: rgba(0,0,0,.55); color: #fff; border-radius: 50%; width: 26px; height: 26px; font-size: 12px; display: flex; align-items: center; justify-content: center; }
.caption { position: absolute; left: 0; right: 0; bottom: 0; padding: 18px 8px 6px; font-size: 12px; color: #fff; text-align: left;
  background: linear-gradient(transparent, rgba(0,0,0,.6)); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
</style>
