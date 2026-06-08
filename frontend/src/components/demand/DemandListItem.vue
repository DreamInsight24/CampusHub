<template>
  <article class="demand-item" :class="{ selected }" @click="$emit('select', demand)">
    <div class="item-top">
      <h3>{{ demand.title }}</h3>
      <DemandStatusTag :status="demand.status" />
    </div>
    <p class="description">{{ demand.description }}</p>
    <div class="meta-row">
      <span>{{ typeLabel }}</span>
      <span>{{ demand.location }}</span>
      <span>{{ rewardText }}</span>
    </div>
    <div class="publisher-row">
      <span>{{ demand.publisherName }}</span>
      <span>{{ demand.createdAt }}</span>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'

import DemandStatusTag from './DemandStatusTag.vue'

import type { Demand, DemandType } from '@/types/demand'

const props = defineProps<{
  demand: Demand
  selected?: boolean
}>()

defineEmits<{
  select: [demand: Demand]
}>()

const typeMap: Record<DemandType, string> = {
  EXPRESS: '快递代取',
  SECONDHAND: '二手交易',
  TUTORING: '学习辅导',
  TEAM: '组队匹配',
  TEAMUP: '组队匹配',
  TYPE1: '类型一',
  TYPE2: '类型二',
}

const typeLabel = computed(() => typeMap[props.demand.type])
const rewardText = computed(() =>
  props.demand.reward == null ? '酬劳面议' : `￥${props.demand.reward}`,
)
</script>

<style scoped>
.demand-item {
  padding: var(--spacing-md);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
  background: var(--color-card);
  cursor: pointer;
}

.demand-item:hover,
.demand-item.selected {
  border-color: var(--color-primary);
}

.item-top,
.publisher-row,
.meta-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.item-top {
  justify-content: space-between;
}

h3 {
  margin: 0;
  font-size: 16px;
  line-height: 1.35;
}

.description {
  display: -webkit-box;
  margin: 10px 0;
  overflow: hidden;
  color: var(--color-text-secondary);
  font-size: 14px;
  line-height: 1.55;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.meta-row {
  flex-wrap: wrap;
  color: var(--color-text-main);
  font-size: 13px;
}

.publisher-row {
  justify-content: space-between;
  margin-top: 12px;
  color: var(--color-text-secondary);
  font-size: 12px;
}
</style>
