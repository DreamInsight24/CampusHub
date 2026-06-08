<template>
  <div class="loading-state">
    <el-skeleton :rows="rows" animated />
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    rows?: number
  }>(),
  {
    rows: 4,
  },
)
</script>

<style scoped>
.loading-state {
  padding: var(--spacing-md);
}
</style>
