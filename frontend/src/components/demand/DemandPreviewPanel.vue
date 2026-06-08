<template>
  <aside class="preview card" :class="demand ? typeClass : ''">
    <template v-if="demand">
      <div class="preview-head">
        <div class="preview-mark">
          <el-icon>
            <component :is="typeIcon" />
          </el-icon>
        </div>
        <div>
          <span>{{ typeLabel }}</span>
          <h2>{{ demand.title }}</h2>
          <p>{{ demand.publisherName }} · {{ demand.location || '未填写地点' }}</p>
        </div>
        <DemandStatusTag :status="demand.status" />
      </div>

      <div class="quick-facts">
        <div>
          <span>酬劳</span>
          <strong>{{ rewardText }}</strong>
        </div>
        <div>
          <span>截止</span>
          <strong>{{ deadlineLabel }}</strong>
        </div>
        <div>
          <span>响应</span>
          <strong>{{ demand.responseCount ?? 0 }} 人</strong>
        </div>
      </div>

      <div class="action-row">
        <RouterLink class="detail-button" :to="`/demands/${demand.id}`">
          <el-icon><View /></el-icon>
          查看完整详情
        </RouterLink>
        <el-button :icon="Star" @click="$emit('favorite', demand)">收藏</el-button>
      </div>

      <section class="description-block">
        <h3>需求说明</h3>
        <p>{{ demand.description || '暂无说明' }}</p>
      </section>

      <section v-if="detailFields.length" class="detail-fields">
        <h3>关键信息</h3>
        <div class="field-list">
          <div v-for="field in detailFields" :key="field.label">
            <span>{{ field.label }}</span>
            <strong>{{ field.value }}</strong>
          </div>
        </div>
      </section>
    </template>
    <EmptyState v-else description="请选择一个需求查看预览" />
  </aside>
</template>

<script setup lang="ts">
import { Box, Collection, School, Star, Van, View } from '@element-plus/icons-vue'
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

const iconMap = {
  EXPRESS: Van,
  SECONDHAND: Box,
  TUTORING: School,
  TEAM: Collection,
  TEAMUP: Collection,
  TYPE1: Collection,
  TYPE2: Collection,
}

const typeLabel = computed(() => (props.demand ? typeMap[props.demand.type] : ''))
const typeIcon = computed(() => (props.demand ? iconMap[props.demand.type] : Collection))
const typeClass = computed(() => (props.demand ? `type-${props.demand.type.toLowerCase()}` : ''))
const rewardText = computed(() => {
  if (!props.demand) {
    return ''
  }

  return props.demand.reward == null ? '酬劳面议' : `¥${props.demand.reward}`
})
const deadlineLabel = computed(() => {
  if (!props.demand?.deadline) {
    return '未设置'
  }

  const deadline = new Date(props.demand.deadline)
  if (Number.isNaN(deadline.getTime())) {
    return formatTime(props.demand.deadline)
  }

  const diff = deadline.getTime() - Date.now()
  if (diff < 0) {
    return '已截止'
  }

  const hours = Math.ceil(diff / 1000 / 60 / 60)
  return hours < 24 ? `${hours} 小时` : `${Math.ceil(hours / 24)} 天`
})

const detailFields = computed(() => {
  if (!props.demand) {
    return []
  }

  const item = props.demand
  if (item.type === 'EXPRESS') {
    return compactFields([
      ['取件地点', item.pickupLocation],
      ['送达地点', item.deliveryLocation],
      ['期望送达', formatTime(item.expectedDeliveryTime)],
    ])
  }

  if (item.type === 'SECONDHAND') {
    return compactFields([
      ['物品名称', item.itemName],
      ['成色', item.conditionLevel],
      ['交易地点', item.tradeLocation],
    ])
  }

  if (item.type === 'TUTORING') {
    return compactFields([
      ['科目', item.subject],
      ['辅导方式', item.tutoringMode],
      ['期望时间', formatTime(item.expectedTime)],
    ])
  }

  return compactFields([
    ['组队目标', item.teamGoal],
    ['成员进度', memberProgress(item.currentMembers, item.expectedMembers)],
    ['联系方式', item.contactMethod],
  ])
})

function compactFields(fields: Array<[string, string | number | undefined | null]>) {
  return fields.filter(([, value]) => value).map(([label, value]) => ({ label, value }))
}

function memberProgress(current?: number | null, expected?: number | null) {
  if (current == null && expected == null) {
    return ''
  }
  return `${current ?? 0}/${expected ?? '?'} 人`
}

function formatTime(value?: string) {
  return value ? value.replace('T', ' ').slice(0, 16) : ''
}
</script>

<style scoped>
.preview {
  position: sticky;
  top: 88px;
  align-self: start;
  padding: var(--spacing-lg);
  overflow: hidden;
  box-shadow: 0 16px 40px rgba(24, 34, 48, 0.06);
}

.preview::before {
  display: block;
  height: 3px;
  margin: calc(var(--spacing-lg) * -1) calc(var(--spacing-lg) * -1) var(--spacing-lg);
  background: var(--color-primary);
  content: '';
}

.type-secondhand::before {
  background: #f97316;
}

.type-tutoring::before {
  background: #12b76a;
}

.type-team::before,
.type-teamup::before {
  background: #7a5af8;
}

.preview-head {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: flex-start;
  gap: var(--spacing-md);
}

.preview-mark {
  display: grid;
  width: 44px;
  height: 44px;
  place-items: center;
  border-radius: var(--radius-card);
  background: #eef4ff;
  color: var(--color-primary);
  font-size: 22px;
}

.type-secondhand .preview-mark {
  background: #fff7ed;
  color: #c2410c;
}

.type-tutoring .preview-mark {
  background: #ecfdf3;
  color: #067647;
}

.type-team .preview-mark,
.type-teamup .preview-mark {
  background: #f4f3ff;
  color: #6938ef;
}

.preview-head span {
  display: inline-flex;
  margin-bottom: 4px;
  color: var(--color-primary);
  font-size: 12px;
  font-weight: 700;
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

.quick-facts {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin: var(--spacing-lg) 0;
}

.quick-facts div {
  display: grid;
  gap: 6px;
  padding: 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
  background: #f8fafc;
}

.quick-facts span,
.field-list span {
  color: var(--color-text-secondary);
  font-size: 12px;
}

.quick-facts strong {
  font-size: 15px;
  line-height: 1.3;
}

.action-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 10px;
}

.detail-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  min-height: 32px;
  padding: 8px 12px;
  border-radius: var(--radius-button);
  background: var(--color-primary);
  color: #ffffff;
  font-size: 14px;
  font-weight: 650;
}

.description-block,
.detail-fields {
  margin-top: var(--spacing-lg);
}

h3 {
  margin: 0 0 8px;
  font-size: 15px;
}

.field-list {
  display: grid;
  gap: 8px;
}

.field-list div {
  display: grid;
  gap: 4px;
  padding: 10px 12px;
  border-radius: var(--radius-card);
  background: #f8fafc;
}

.field-list strong {
  font-size: 14px;
  line-height: 1.45;
}

@media (max-width: 640px) {
  .preview-head,
  .quick-facts,
  .action-row {
    grid-template-columns: 1fr;
  }

  .preview-mark {
    display: none;
  }
}
</style>
