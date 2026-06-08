<template>
  <div class="search-bar">
    <el-input
      v-model="keyword"
      clearable
      placeholder="搜索需求标题、地点或描述"
      size="large"
      @keyup.enter="emitSearch"
    />
    <el-button type="primary" size="large" :icon="Search" @click="emitSearch">搜索</el-button>
  </div>
</template>

<script setup lang="ts">
import { Search } from '@element-plus/icons-vue'
import { ref, watch } from 'vue'

const props = defineProps<{
  modelValue: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
  search: [value: string]
}>()

const keyword = ref(props.modelValue)

watch(
  () => props.modelValue,
  (value) => {
    keyword.value = value
  },
)

watch(keyword, (value) => emit('update:modelValue', value))

function emitSearch() {
  emit('search', keyword.value)
}
</script>

<style scoped>
.search-bar {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
}

@media (max-width: 640px) {
  .search-bar {
    grid-template-columns: 1fr;
  }
}
</style>
