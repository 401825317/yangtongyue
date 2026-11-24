<script setup>
import { computed, onMounted, ref } from 'vue'
import { api } from '../api'

const columns = [
  { author: 'mama', title: '妈妈想对你说的' },
  { author: 'baba', title: '爸爸想对你说的' },
]
const notes = ref([])
const drafts = ref({ mama: '', baba: '' })
const error = ref('')

const byAuthor = computed(() => {
  const map = { mama: [], baba: [] }
  for (const n of notes.value) (map[n.author] ||= []).push(n)
  return map
})

onMounted(async () => (notes.value = await api.notes()))

async function add(author) {
  error.value = ''
  try {
    notes.value.unshift(await api.createNote({ author, content: drafts.value[author] }))
    drafts.value[author] = ''
  } catch (e) {
    error.value = e.message
  }
}
async function remove(n) {
  if (!confirm('删除这条留言？')) return
  await api.deleteNote(n.id)
  notes.value = notes.value.filter((x) => x.id !== n.id)
}
const fmt = (ms) => new Date(ms).toLocaleString('zh-CN', { dateStyle: 'medium', timeStyle: 'short' })
</script>

<template>
  <div class="page">
    <div class="page-head"><h2>爸妈留言</h2></div>
    <p v-if="error" class="error">{{ error }}</p>
    <div class="cols">
      <section v-for="c in columns" :key="c.author" class="card">
        <h3>{{ c.title }}</h3>
        <form @submit.prevent="add(c.author)">
          <textarea v-model="drafts[c.author]" :aria-label="c.title" placeholder="写下想对宝贝说的话…" required maxlength="10000"></textarea>
          <div class="row" style="justify-content: flex-end; margin-top: 8px">
            <button type="submit">留言</button>
          </div>
        </form>
        <div v-if="!byAuthor[c.author].length" class="muted none">还没有留言</div>
        <article v-for="n in byAuthor[c.author]" :key="n.id" class="note">
          <p>{{ n.content }}</p>
          <div class="meta">
            <span class="muted">{{ fmt(n.createdAt) }}</span>
            <button class="danger" @click="remove(n)">删除</button>
          </div>
        </article>
      </section>
    </div>
  </div>
</template>

<style scoped>
.cols { display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 20px; }
h3 { margin-top: 0; }
.none { padding: 20px 0; text-align: center; }
.note { border-top: 1px solid var(--line); padding: 14px 0 6px; }
.note p { margin: 0 0 8px; white-space: pre-wrap; line-height: 1.7; }
.meta { display: flex; justify-content: space-between; align-items: center; font-size: 13px; }
.meta button { padding: 2px 10px; font-size: 13px; }
</style>
