<template>
  <PageContainer>
    <div class="plaza-toolbar card">
      <DemandSearchBar v-model="keyword" @search="handleSearch" />
      <DemandFilterChips v-model="activeType" />
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

    <div class="plaza-layout">
      <section class="list-column">
        <div class="list-head">
          <h1 class="page-title">需求广场</h1>
          <span>{{ total }} 条需求</span>
        </div>

        <div v-if="demands.length" class="demand-list">
          <DemandListItem
            v-for="demand in demands"
            :key="demand.id"
            :demand="demand"
            :selected="selectedDemand?.id === demand.id"
            @select="selectDemand"
          />
        </div>
        <EmptyState v-else description="暂无匹配的需求" />

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
import { onMounted, ref, watch } from 'vue'

import { fetchDemands } from '@/api/demand'
import EmptyState from '@/components/common/EmptyState.vue'
import PageContainer from '@/components/common/PageContainer.vue'
import DemandFilterChips from '@/components/demand/DemandFilterChips.vue'
import DemandListItem from '@/components/demand/DemandListItem.vue'
import DemandPreviewPanel from '@/components/demand/DemandPreviewPanel.vue'
import DemandSearchBar from '@/components/demand/DemandSearchBar.vue'
import { useDemandStore } from '@/stores/demand'
import type { Demand, DemandStatus, DemandType } from '@/types/demand'

type FilterValue = DemandType | 'ALL'

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
let searchTimer: ReturnType<typeof setTimeout> | null = null

watch(demands, (items) => {
  if (!items.some((item) => item.id === selectedDemand.value?.id)) {
    selectedDemand.value = items[0] ?? null
  }
})

watch([activeType, activeStatus, sort, pageSize], () => {
  page.value = 1
  loadDemands()
})

watch(page, () => {
  loadDemands()
})

watch(keyword, () => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }

  searchTimer = setTimeout(() => {
    page.value = 1
    loadDemands()
  }, 300)
})

onMounted(() => {
  loadDemands()
})

function selectDemand(demand: Demand) {
  selectedDemand.value = demand
  demandStore.selectDemand(demand)
}

function handleFavorite(demand: Demand) {
  ElMessage.success(`已收藏「${demand.title}」`)
}

function handleSearch() {
  page.value = 1
  loadDemands()
}

async function loadDemands() {
  try {
    const response = await fetchDemands({
      keyword: keyword.value.trim() || undefined,
      type: activeType.value === 'ALL' ? undefined : activeType.value,
      status: activeStatus.value || undefined,
      sort: sort.value,
      page: page.value,
      pageSize: pageSize.value,
    })

    demands.value = response.data.data.items
    total.value = response.data.data.total
  } catch {
    demands.value = []
    total.value = 0
  }
}
</script>

<style scoped>
.plaza-toolbar {
  display: grid;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-md);
}

.plaza-controls {
  display: grid;
  grid-template-columns: repeat(2, minmax(160px, 220px));
  gap: 12px;
}

.plaza-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 420px;
  gap: var(--spacing-lg);
}

.list-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-md);
}

.list-head span {
  color: var(--color-text-secondary);
  font-size: 14px;
}

.demand-list {
  display: grid;
  gap: 12px;
}

.plaza-pagination {
  margin-top: var(--spacing-md);
  justify-content: flex-end;
}

@media (max-width: 980px) {
  .plaza-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .plaza-controls {
    grid-template-columns: 1fr;
  }
}
</style>
