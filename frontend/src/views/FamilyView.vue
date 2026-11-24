<script setup>
import { onMounted, ref } from 'vue'
import { api } from '../api'
import { session } from '../session'

const users = ref([])
const form = ref({ username: '', displayName: '', password: '', role: 'MEMBER' })
const error = ref('')

onMounted(async () => (users.value = await api.users()))

async function create() {
  error.value = ''
  try {
    users.value.push(await api.createUser(form.value))
    form.value = { username: '', displayName: '', password: '', role: 'MEMBER' }
  } catch (e) {
    error.value = e.message
  }
}
async function remove(u) {
  if (!confirm(`删除账号 ${u.username}？`)) return
  try {
    await api.deleteUser(u.id)
    users.value = users.value.filter((x) => x.id !== u.id)
  } catch (e) {
    error.value = e.message
  }
}
</script>

<template>
  <div class="page">
    <div class="page-head"><h2>家人账号</h2></div>
    <form class="card form" @submit.prevent="create">
      <div class="row">
        <div><label for="u-name">用户名</label><input id="u-name" v-model="form.username" required maxlength="64" /></div>
        <div><label for="u-display">称呼</label><input id="u-display" v-model="form.displayName" placeholder="例如：奶奶" maxlength="64" /></div>
        <div><label for="u-pass">密码</label><input id="u-pass" v-model="form.password" type="password" required minlength="6" autocomplete="new-password" /></div>
        <div>
          <label for="u-role">角色</label>
          <select id="u-role" v-model="form.role"><option value="MEMBER">家人</option><option value="ADMIN">管理员</option></select>
        </div>
        <button type="submit">添加</button>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
    </form>
    <table class="card">
      <thead><tr><th>用户名</th><th>称呼</th><th>角色</th><th></th></tr></thead>
      <tbody>
        <tr v-for="u in users" :key="u.id">
          <td>{{ u.username }}</td>
          <td>{{ u.displayName }}</td>
          <td>{{ u.role === 'ADMIN' ? '管理员' : '家人' }}</td>
          <td style="text-align: right">
            <button v-if="u.username !== session.user.username" class="danger" @click="remove(u)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.form { margin-bottom: 20px; }
.form .row { align-items: flex-end; }
.form .row > div { flex: 1; min-width: 140px; }
table.card { padding: 0; }
td button { padding: 4px 10px; font-size: 13px; }
</style>
