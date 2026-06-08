<template>
  <el-dropdown trigger="click" @command="handleCommand">
    <button class="avatar-button" type="button">
      <el-avatar :size="34" :src="authStore.user?.avatar">
        {{ initial }}
      </el-avatar>
    </button>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="profile">个人中心</el-dropdown-item>
        <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const initial = computed(() => authStore.user?.username?.slice(0, 1) || 'U')

function handleCommand(command: string) {
  if (command === 'profile') {
    router.push('/profile')
    return
  }

  if (command === 'logout') {
    authStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.avatar-button {
  display: inline-flex;
  align-items: center;
  padding: 0;
  border: 0;
  background: transparent;
  cursor: pointer;
}
</style>
