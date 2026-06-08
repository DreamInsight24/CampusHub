<template>
  <aside class="preview card">
    <template v-if="demand">
      <div class="preview-head">
        <div>
          <h2>{{ demand.title }}</h2>
          <p>{{ demand.publisherName }} · {{ demand.location || '未填写地点' }}</p>
        </div>
        <DemandStatusTag :status="demand.status" />
      </div>

      <div class="action-row">
        <el-button :icon="Star" @click="$emit('favorite', demand)">收藏</el-button>
      </div>

      <el-descriptions :column="1" border>
        <el-descriptions-item label="类型">{{ typeLabel }}</el-descriptions-item>
        <el-descriptions-item label="酬劳">{{ rewardText }}</el-descriptions-item>
        <el-descriptions-item label="截止时间">
          {{ formatTime(demand.deadline) || '未设置' }}
        </el-descriptions-item>
      </el-descriptions>

      <section>
        <h3>需求说明</h3>
        <p>{{ demand.description || '暂无说明' }}</p>
      </section>

      <RouterLink class="detail-link" :to="`/demands/${demand.id}`">查看完整详情</RouterLink>
    </template>
    <EmptyState v-else description="请选择一个需求查看预览" />
  </aside>
</template>

<script setup lang="ts">
import { Star } from '@element-plus/icons-vue'
import { computed } from 'vue'

import DemandStatusTag from './DemandStatusTag.vue'
import EmptyState from '@/components/common/EmptyState.vue'

import type { Demand, DemandType } from '@/types/demand'

const props = defineProps<{
  demand: Demand | null
}>()

defineEmits<{
  favorite: [demand: Demand]
}>()

const typeMap: Record<DemandType, string> = {
  EXPRESS: '跑腿',
  SECONDHAND: '二手交易',
  TUTORING: '学习辅导',
  TEAM: '组队匹配',
  TEAMUP: '组队匹配',
  TYPE1: '类型一',
  TYPE2: '类型二',
}

const typeLabel = computed(() => (props.demand ? typeMap[props.demand.type] : ''))
const rewardText = computed(() => {
  if (!props.demand) {
    return ''
  }

  return props.demand.reward == null ? '酬劳面议' : `¥${props.demand.reward}`
})

function formatTime(value?: string) {
  return value ? value.replace('T', ' ').slice(0, 19) : ''
}
</script>

<style scoped>
.preview {
  position: sticky;
  top: 88px;
  align-self: start;
  padding: var(--spacing-lg);
}

.preview-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--spacing-md);
}

h2 {
  margin: 0 0 8px;
  font-size: 20px;
  line-height: 1.35;
}

.preview-head p,
section p {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.65;
}

.action-row {
  display: flex;
  gap: 10px;
  margin: var(--spacing-lg) 0;
}

section {
  margin-top: var(--spacing-lg);
}

h3 {
  margin: 0 0 8px;
  font-size: 15px;
}

.detail-link {
  display: inline-flex;
  margin-top: var(--spacing-lg);
  color: var(--color-primary);
  font-weight: 650;
}
</style>
