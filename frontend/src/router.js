import { createRouter, createWebHashHistory } from 'vue-router'
import { session, loadSession } from './session'
import LoginView from './views/LoginView.vue'
import TimelineView from './views/TimelineView.vue'
import AlbumsView from './views/AlbumsView.vue'
import NotesView from './views/NotesView.vue'
import SharesView from './views/SharesView.vue'
import FamilyView from './views/FamilyView.vue'
import SharedView from './views/SharedView.vue'

export const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/login', component: LoginView, meta: { public: true } },
    { path: '/s/:token', component: SharedView, meta: { public: true } },
    { path: '/', component: TimelineView },
    { path: '/albums', component: AlbumsView },
    { path: '/albums/:id', component: TimelineView, props: true },
    { path: '/notes', component: NotesView },
    { path: '/shares', component: SharesView },
    { path: '/family', component: FamilyView, meta: { admin: true } },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

router.beforeEach(async (to) => {
  if (to.meta.public) return true
  if (!session.loaded || !session.user) await loadSession()
  if (!session.user) return { path: '/login', query: { next: to.fullPath } }
  if (to.meta.admin && session.user.role !== 'ADMIN') return '/'
  return true
})
