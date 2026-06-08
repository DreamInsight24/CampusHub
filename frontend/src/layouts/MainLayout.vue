<template>
  <div class="main-layout">
    <AppHeader />
    <RouterView />
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/components/common/AppHeader.vue'
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
}
</style>
