<template>
  <main class="page-container">
    <slot />
  </main>
</template>

<script setup lang="ts"></script>

<style scoped>
.page-container {
  width: min(1180px, calc(100% - 32px));
  margin: 0 auto;
  padding: var(--spacing-lg) 0 40px;
}
</style>
