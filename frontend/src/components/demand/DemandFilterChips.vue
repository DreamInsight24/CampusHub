<template>
  <div class="filter-chips" aria-label="需求类型筛选">
    <button
      v-for="option in options"
      :key="option.value"
      class="chip"
      :class="{ active: modelValue === option.value }"
      type="button"
      @click="$emit('update:modelValue', option.value)"
    >
      {{ option.label }}
    </button>
  </div>
</template>

<script setup lang="ts">
import type { DemandType } from '@/types/demand'

type FilterValue = DemandType | 'ALL'

defineProps<{
  modelValue: FilterValue
}>()

defineEmits<{
  'update:modelValue': [value: FilterValue]
}>()

const options: Array<{ label: string; value: FilterValue }> = [
  { label: '全部', value: 'ALL' },
  { label: '快递代取', value: 'EXPRESS' },
  { label: '二手交易', value: 'SECONDHAND' },
  { label: '学习辅导', value: 'TUTORING' },
  { label: '组队匹配', value: 'TEAM' },
]
</script>

<style scoped>
.filter-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.chip {
  min-height: 32px;
  padding: 0 12px;
  border: 1px solid var(--color-border);
  border-radius: 999px;
  background: #ffffff;
  color: var(--color-text-secondary);
  cursor: pointer;
}

.chip.active {
  border-color: var(--color-primary);
  background: #eef4ff;
  color: var(--color-primary);
}
</style>
