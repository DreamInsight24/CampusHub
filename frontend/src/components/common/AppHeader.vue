<template>
  <header class="app-header">
    <RouterLink class="brand" to="/demands">
      <span class="brand-mark">C</span>
      <span>CampusHub</span>
    </RouterLink>

    <nav class="nav-links" aria-label="主导航">
      <RouterLink to="/demands">需求广场</RouterLink>
      <RouterLink to="/demands/mine">我的订单</RouterLink>
      <RouterLink to="/demands/favorites">我的收藏</RouterLink>
      <RouterLink to="/messages">消息</RouterLink>
    </nav>

    <div class="header-actions">
      <el-button type="primary" :icon="Plus" @click="router.push('/demands/create')">
        发布需求
      </el-button>
      <UserAvatarMenu />
    </div>
  </header>
</template>

<script setup lang="ts">
import { Plus } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

import UserAvatarMenu from '@/components/user/UserAvatarMenu.vue'

const router = useRouter()
</script>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 10;
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: var(--spacing-lg);
  min-height: 64px;
  padding: 0 32px;
  border-bottom: 1px solid var(--color-border);
  background: rgba(255, 255, 255, 0.96);
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 750;
}

.brand-mark {
  display: inline-grid;
  width: 32px;
  height: 32px;
  place-items: center;
  border-radius: 8px;
  background: var(--color-primary);
  color: #ffffff;
  font-weight: 800;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.nav-links a {
  padding: 8px 10px;
  border-radius: var(--radius-button);
  color: var(--color-text-secondary);
  font-size: 14px;
}

.nav-links a.router-link-active {
  color: var(--color-primary);
  background: #eef4ff;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

@media (max-width: 720px) {
  .app-header {
    grid-template-columns: 1fr auto;
    padding: 0 16px;
  }

  .nav-links {
    grid-column: 1 / -1;
    order: 3;
    padding-bottom: 10px;
  }
}
</style>
