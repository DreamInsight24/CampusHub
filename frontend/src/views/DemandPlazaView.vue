<template>
  <PageContainer>
    <div class="plaza-toolbar card">
      <div class="toolbar-main">
        <DemandSearchBar v-model="keyword" @search="handleSearch" />
        <div class="plaza-controls">
          <el-select v-model="activeStatus" placeholder="状态" clearable>
            <el-option label="开放中" value="OPEN" />
            <el-option label="进行中" value="IN_PROGRESS" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已结束" value="CLOSED" />
          </el-select>
          <el-select v-model="sort" placeholder="排序">
            <el-option label="最新发布" value="latest" />
            <el-option label="截止时间" value="deadline" />
          </el-select>
        </div>
      </div>
      <DemandFilterChips v-model="activeType" />
      <div class="filter-summary">
        <div>
          <span class="summary-label">当前筛选</span>
          <strong>{{ filterSummary }}</strong>
        </div>
        <el-button v-if="hasActiveFilters" text @click="clearFilters">清空筛选</el-button>
      </div>
    </div>

    <div class="plaza-stats">
      <div class="stat-card">
        <span>匹配需求</span>
        <strong>{{ total }}</strong>
      </div>
      <div class="stat-card">
        <span>当前类型</span>
        <strong>{{ typeLabel(activeType) }}</strong>
      </div>
      <div class="stat-card">
        <span>排序方式</span>
        <strong>{{ sort === 'latest' ? '最新发布' : '截止时间' }}</strong>
      </div>
    </div>

    <div class="plaza-layout">
      <section class="list-column">
        <div class="list-head">
          <div>
            <h1 class="page-title">需求广场</h1>
            <p class="muted">发现校园里的跑腿、交易、辅导和组队需求</p>
          </div>
          <span v-if="loading">正在更新...</span>
          <span v-else>{{ total }} 条需求</span>
        </div>

        <div v-loading="loading" class="list-surface">
          <div v-if="demands.length" class="demand-list">
            <DemandListItem
              v-for="demand in demands"
              :key="demand.id"
              :demand="demand"
              :selected="selectedDemand?.id === demand.id"
              @select="selectDemand"
            />
          </div>
          <EmptyState v-else-if="!loading" description="暂无匹配的需求">
            <el-button v-if="hasActiveFilters" type="primary" plain @click="clearFilters">
              清空筛选
            </el-button>
          </EmptyState>
        </div>

        <div v-if="loading && !demands.length" class="skeleton-list">
          <div v-for="item in 3" :key="item" class="skeleton-card" />
        </div>

        <el-pagination
          v-if="total > pageSize"
          v-model:current-page="page"
          v-model:page-size="pageSize"
          class="plaza-pagination"
          layout="prev, pager, next, sizes"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
        />
      </section>

      <DemandPreviewPanel
        :demand="selectedDemand"
        @favorite="handleFavorite"
      />
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { favoriteDemand, fetchDemands } from '@/api/demand'
import EmptyState from '@/components/common/EmptyState.vue'
import PageContainer from '@/components/common/PageContainer.vue'
import DemandFilterChips from '@/components/demand/DemandFilterChips.vue'
import DemandListItem from '@/components/demand/DemandListItem.vue'
import DemandPreviewPanel from '@/components/demand/DemandPreviewPanel.vue'
import DemandSearchBar from '@/components/demand/DemandSearchBar.vue'
import { useDemandStore } from '@/stores/demand'
import type { Demand, DemandStatus, DemandType } from '@/types/demand'

type FilterValue = DemandType | 'ALL'

const route = useRoute()
const router = useRouter()
const demandStore = useDemandStore()
const keyword = ref('')
const activeType = ref<FilterValue>('ALL')
const activeStatus = ref<DemandStatus | ''>('')
const sort = ref<'latest' | 'deadline'>('latest')
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const demands = ref<Demand[]>([])
const selectedDemand = ref<Demand | null>(null)
const loading = ref(false)
let searchTimer: ReturnType<typeof setTimeout> | null = null
let requestId = 0
let hydratingFromRoute = false

const typeMap: Record<FilterValue, string> = {
  ALL: '全部类型',
  EXPRESS: '快递代取',
  SECONDHAND: '二手交易',
  TUTORING: '学习辅导',
  TEAM: '组队匹配',
  TEAMUP: '组队匹配',
  TYPE1: '类型一',
  TYPE2: '类型二',
}

const statusMap: Record<DemandStatus, string> = {
  OPEN: '开放中',
  IN_PROGRESS: '进行中',
  COMPLETED: '已完成',
  CLOSED: '已结束',
  CANCELLED: '已取消',
  NO: '未开始',
  YES: '进行中',
  DONE: '已完成',
  DEL: '已删除',
}

const hasActiveFilters = computed(
  () =>
    Boolean(keyword.value.trim()) ||
    activeType.value !== 'ALL' ||
    Boolean(activeStatus.value) ||
    sort.value !== 'latest',
)

const filterSummary = computed(() => {
  const parts = [typeLabel(activeType.value)]
  if (activeStatus.value) {
    parts.push(statusMap[activeStatus.value])
  }
  if (keyword.value.trim()) {
    parts.push(`关键词「${keyword.value.trim()}」`)
  }
  parts.push(sort.value === 'latest' ? '最新发布' : '按截止时间')
  return parts.join(' · ')
})

watch(demands, (items) => {
  if (!items.some((item) => item.id === selectedDemand.value?.id)) {
    selectedDemand.value = items[0] ?? null
  }
})

watch([activeType, activeStatus, sort, pageSize], () => {
  if (hydratingFromRoute) {
    return
  }
  if (page.value !== 1) {
    page.value = 1
    return
  }
  refreshDemands()
})

watch(page, () => {
  if (!hydratingFromRoute) {
    refreshDemands()
  }
})

watch(keyword, () => {
  if (hydratingFromRoute) {
    return
  }

  if (searchTimer) {
    clearTimeout(searchTimer)
  }

  searchTimer = setTimeout(() => {
    if (page.value !== 1) {
      page.value = 1
      return
    }
    refreshDemands()
  }, 300)
})

watch(
  () => route.query,
  () => {
    if (routeStateKey() === queryStateKey()) {
      return
    }
    hydrateStateFromQuery()
    loadDemands()
  },
)

onMounted(() => {
  hydrateStateFromQuery()
  loadDemands()
})

function selectDemand(demand: Demand) {
  selectedDemand.value = demand
  demandStore.selectDemand(demand)
}

async function handleFavorite(demand: Demand) {
  try {
    const response = await favoriteDemand(demand.id)
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    ElMessage.success(`已收藏「${demand.title}」`)
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '收藏失败')
  }
}

function handleSearch() {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  if (page.value !== 1) {
    page.value = 1
    return
  }
  refreshDemands()
}

function clearFilters() {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  hydratingFromRoute = true
  keyword.value = ''
  activeType.value = 'ALL'
  activeStatus.value = ''
  sort.value = 'latest'
  page.value = 1
  hydratingFromRoute = false
  refreshDemands()
}

async function refreshDemands() {
  syncQuery()
  await loadDemands()
}

async function loadDemands() {
  const currentRequestId = ++requestId
  loading.value = true
  try {
    const response = await fetchDemands({
      keyword: keyword.value.trim() || undefined,
      type: activeType.value === 'ALL' ? undefined : activeType.value,
      status: activeStatus.value || undefined,
      sort: sort.value,
      page: page.value,
      pageSize: pageSize.value,
    })

    if (currentRequestId !== requestId) {
      return
    }

    demands.value = response.data.data.items
    total.value = response.data.data.total
  } catch {
    if (currentRequestId !== requestId) {
      return
    }
    demands.value = []
    total.value = 0
    ElMessage.error('需求加载失败，请稍后重试')
  } finally {
    if (currentRequestId === requestId) {
      loading.value = false
    }
  }
}

function typeLabel(type: FilterValue) {
  return typeMap[type]
}

function syncQuery() {
  const nextQuery = {
    keyword: keyword.value.trim() || undefined,
    type: activeType.value === 'ALL' ? undefined : activeType.value,
    status: activeStatus.value || undefined,
    sort: sort.value === 'latest' ? undefined : sort.value,
    page: page.value > 1 ? String(page.value) : undefined,
    pageSize: pageSize.value === 20 ? undefined : String(pageSize.value),
  }

  if (routeStateKey() === queryKey(nextQuery)) {
    return
  }

  router.replace({ name: 'demands', query: nextQuery })
}

function hydrateStateFromQuery() {
  hydratingFromRoute = true
  keyword.value = asString(route.query.keyword)
  activeType.value = asDemandType(route.query.type)
  activeStatus.value = asDemandStatus(route.query.status)
  sort.value = route.query.sort === 'deadline' ? 'deadline' : 'latest'
  page.value = positiveNumber(route.query.page, 1)
  pageSize.value = positiveNumber(route.query.pageSize, 20)
  hydratingFromRoute = false
}

function queryStateKey() {
  return queryKey({
    keyword: keyword.value.trim() || undefined,
    type: activeType.value === 'ALL' ? undefined : activeType.value,
    status: activeStatus.value || undefined,
    sort: sort.value === 'latest' ? undefined : sort.value,
    page: page.value > 1 ? String(page.value) : undefined,
    pageSize: pageSize.value === 20 ? undefined : String(pageSize.value),
  })
}

function routeStateKey() {
  return queryKey({
    keyword: asString(route.query.keyword) || undefined,
    type: asDemandType(route.query.type) === 'ALL' ? undefined : asDemandType(route.query.type),
    status: asDemandStatus(route.query.status) || undefined,
    sort: route.query.sort === 'deadline' ? 'deadline' : undefined,
    page:
      positiveNumber(route.query.page, 1) > 1
        ? String(positiveNumber(route.query.page, 1))
        : undefined,
    pageSize:
      positiveNumber(route.query.pageSize, 20) === 20
        ? undefined
        : String(positiveNumber(route.query.pageSize, 20)),
  })
}

function queryKey(query: Record<string, string | undefined>) {
  return ['keyword', 'type', 'status', 'sort', 'page', 'pageSize']
    .map((key) => `${key}:${query[key] || ''}`)
    .join('|')
}

function asString(value: unknown) {
  return Array.isArray(value) ? String(value[0] || '') : String(value || '')
}

function asDemandType(value: unknown): FilterValue {
  const type = asString(value)
  return Object.keys(typeMap).includes(type) ? (type as FilterValue) : 'ALL'
}

function asDemandStatus(value: unknown): DemandStatus | '' {
  const status = asString(value)
  return Object.keys(statusMap).includes(status) ? (status as DemandStatus) : ''
}

function positiveNumber(value: unknown, fallback: number) {
  const number = Number(asString(value))
  return Number.isFinite(number) && number > 0 ? number : fallback
}
</script>

<style scoped>
.plaza-toolbar {
  display: grid;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
  padding: var(--spacing-md);
  box-shadow: 0 16px 40px rgba(24, 34, 48, 0.06);
}

.toolbar-main {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: var(--spacing-md);
  align-items: start;
}

.plaza-controls {
  display: grid;
  grid-template-columns: repeat(2, minmax(150px, 180px));
  gap: 12px;
}

.filter-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
  min-height: 40px;
  padding: 10px 12px;
  border-radius: var(--radius-card);
  background: #f8fafc;
}

.filter-summary > div {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.summary-label {
  color: var(--color-text-secondary);
  font-size: 13px;
}

.filter-summary strong {
  color: var(--color-text-main);
  font-size: 14px;
}

.plaza-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: var(--spacing-lg);
}

.stat-card {
  display: grid;
  gap: 6px;
  padding: 14px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
}

.stat-card span {
  color: var(--color-text-secondary);
  font-size: 12px;
}

.stat-card strong {
  font-size: 20px;
  line-height: 1.2;
}

.plaza-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 420px;
  gap: var(--spacing-lg);
}

.list-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.list-head p {
  margin: -8px 0 0;
}

.list-head span {
  flex: 0 0 auto;
  color: var(--color-text-secondary);
  font-size: 14px;
}

.list-surface {
  min-height: 260px;
}

.demand-list {
  display: grid;
  gap: 12px;
}

.skeleton-list {
  display: grid;
  gap: 12px;
}

.skeleton-card {
  height: 142px;
  border-radius: var(--radius-card);
  background:
    linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.7), transparent),
    #e8eef6;
  background-size: 200% 100%;
  animation: shimmer 1.2s infinite;
}

.plaza-pagination {
  margin-top: var(--spacing-md);
  justify-content: flex-end;
}

@keyframes shimmer {
  from {
    background-position: 200% 0;
  }

  to {
    background-position: -200% 0;
  }
}

@media (max-width: 980px) {
  .toolbar-main,
  .plaza-layout {
    grid-template-columns: 1fr;
  }

  .plaza-controls {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .plaza-controls,
  .plaza-stats {
    grid-template-columns: 1fr;
  }

  .filter-summary,
  .list-head {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
