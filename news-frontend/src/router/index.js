import { createRouter, createWebHistory } from 'vue-router'
import Menu from '../views/Menu.vue'
import DataScreen from '../views/DataScreen.vue'
import Topics from '../views/Topics.vue'
import Authors from '../views/Authors.vue'
import Duplicates from '../views/Duplicates.vue'
import Bursts from '../views/Bursts.vue'
import Chat from '../views/Chat.vue'

const routes = [
  {
    path: '/',
    name: 'Menu',
    component: Menu
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: DataScreen
  },
  {
    path: '/topics',
    name: 'Topics',
    component: Topics
  },
  {
    path: '/authors',
    name: 'Authors',
    component: Authors
  },
  {
    path: '/duplicates',
    name: 'Duplicates',
    component: Duplicates
  },
  {
    path: '/bursts',
    name: 'Bursts',
    component: Bursts
  },
  {
    path: '/chat',
    name: 'Chat',
    component: Chat
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
