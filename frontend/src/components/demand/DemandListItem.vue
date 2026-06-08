<template>
  <article
    class="demand-item"
    :class="[{ selected }, typeClass]"
    role="button"
    tabindex="0"
    @click="$emit('select', demand)"
    @keydown.enter="$emit('select', demand)"
  >
    <div class="type-mark">
      <el-icon>
        <component :is="typeIcon" />
      </el-icon>
    </div>

    <div class="item-main">
      <div class="item-top">
        <div>
          <span class="type-label">{{ typeLabel }}</span>
          <h3>{{ demand.title }}</h3>
        </div>
        <DemandStatusTag :status="demand.status" />
      </div>

      <p class="description">{{ demand.description || '暂无说明' }}</p>

      <div class="meta-grid">
        <span>
          <el-icon><Location /></el-icon>
          {{ demand.location || '未填写地点' }}
        </span>
        <span class="reward">
          <el-icon><Money /></el-icon>
          {{ rewardText }}
        </span>
        <span>
          <el-icon><Clock /></el-icon>
          {{ deadlineText }}
        </span>
      </div>

      <div class="publisher-row">
        <span>{{ demand.publisherName || '匿名用户' }} · {{ createdTime }}</span>
        <span v-if="demand.responseCount != null">{{ demand.responseCount }} 人响应</span>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts">
import { Box, Clock, Collection, Location, Money, School, Van } from '@element-plus/icons-vue'
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

const iconMap = {
  EXPRESS: Van,
  SECONDHAND: Box,
  TUTORING: School,
  TEAM: Collection,
  TEAMUP: Collection,
  TYPE1: Collection,
  TYPE2: Collection,
}

const typeLabel = computed(() => typeMap[props.demand.type])
const typeIcon = computed(() => iconMap[props.demand.type])
const typeClass = computed(() => `type-${props.demand.type.toLowerCase()}`)
const rewardText = computed(() =>
  props.demand.reward == null ? '酬劳面议' : `￥${props.demand.reward}`,
)
const createdTime = computed(() => formatTime(props.demand.createdAt))
const deadlineText = computed(() => {
  if (!props.demand.deadline) {
    return '未设置截止'
  }

  const deadline = new Date(props.demand.deadline)
  if (Number.isNaN(deadline.getTime())) {
    return formatTime(props.demand.deadline)
  }

  const diff = deadline.getTime() - Date.now()
  if (diff < 0) {
    return '已过截止'
  }

  const hours = Math.ceil(diff / 1000 / 60 / 60)
  if (hours < 24) {
    return `${hours} 小时内截止`
  }

  return `${Math.ceil(hours / 24)} 天内截止`
})

function formatTime(value?: string) {
  return value ? value.replace('T', ' ').slice(0, 16) : '未知时间'
}
</script>

<style scoped>
.demand-item {
  position: relative;
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
  background: var(--color-card);
  cursor: pointer;
  transition:
    border-color 0.18s ease,
    box-shadow 0.18s ease,
    transform 0.18s ease;
}

.demand-item::before {
  position: absolute;
  inset: 12px auto 12px 0;
  width: 3px;
  border-radius: 0 999px 999px 0;
  background: transparent;
  content: '';
}

.demand-item:hover,
.demand-item.selected {
  border-color: var(--color-primary);
  box-shadow: 0 12px 32px rgba(24, 34, 48, 0.08);
}

.demand-item:hover {
  transform: translateY(-1px);
}

.demand-item.selected {
  background: #fbfdff;
}

.demand-item.selected::before {
  background: var(--color-primary);
}

.type-mark {
  display: grid;
  width: 42px;
  height: 42px;
  place-items: center;
  border-radius: var(--radius-card);
  background: #eef4ff;
  color: var(--color-primary);
  font-size: 20px;
}

.type-secondhand .type-mark {
  background: #fff7ed;
  color: #c2410c;
}

.type-tutoring .type-mark {
  background: #ecfdf3;
  color: #067647;
}

.type-team .type-mark,
.type-teamup .type-mark {
  background: #f4f3ff;
  color: #6938ef;
}

.item-main {
  min-width: 0;
}

.item-top,
.publisher-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.item-top {
  justify-content: space-between;
}

.type-label {
  display: inline-flex;
  margin-bottom: 4px;
  color: var(--color-primary);
  font-size: 12px;
  font-weight: 700;
}

h3 {
  margin: 0;
  font-size: 17px;
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

.meta-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  margin-top: 12px;
}

.meta-grid span {
  display: inline-flex;
  min-width: 0;
  align-items: center;
  gap: 6px;
  color: var(--color-text-secondary);
  font-size: 13px;
}

.meta-grid .el-icon {
  flex: 0 0 auto;
  color: #98a2b3;
}

.meta-grid .reward {
  color: var(--color-text-main);
  font-weight: 700;
}

.publisher-row {
  justify-content: space-between;
  margin-top: 12px;
  color: var(--color-text-secondary);
  font-size: 12px;
}

@media (max-width: 720px) {
  .demand-item {
    grid-template-columns: 1fr;
  }

  .type-mark {
    display: none;
  }

  .meta-grid {
    grid-template-columns: 1fr;
  }
}
</style>
