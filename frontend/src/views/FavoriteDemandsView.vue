<template>
  <PageContainer>
    <section class="favorite-page card">
      <div class="page-head">
        <div>
          <h1 class="page-title">我的收藏</h1>
          <p class="muted">集中查看你标记过的需求，方便之后继续沟通或申请</p>
        </div>
        <el-button type="primary" plain @click="router.push('/demands')">去需求广场</el-button>
      </div>

      <div v-loading="loading" class="favorite-area">
        <div v-if="demands.length" class="favorite-list">
          <article v-for="demand in demands" :key="demand.id" class="favorite-row">
            <div class="row-main">
              <div class="row-title">
                <h2>{{ demand.title }}</h2>
                <DemandStatusTag :status="demand.status" />
              </div>
              <p>{{ demand.description || '暂无说明' }}</p>
              <div class="meta-row">
                <span>{{ typeLabel(demand.type) }}</span>
                <span>{{ demand.location || '未填写地点' }}</span>
                <span>发布时间：{{ formatTime(demand.createdAt) }}</span>
              </div>
            </div>
            <div class="row-actions">
              <el-button :icon="View" @click="router.push(`/demands/${demand.id}`)">查看详情</el-button>
              <el-button
                type="danger"
                plain
                :loading="removingId === demand.id"
                @click="removeFavorite(demand.id)"
              >
                取消收藏
              </el-button>
            </div>
          </article>
        </div>

        <EmptyState v-else-if="!loading" description="还没有收藏需求">
          <el-button type="primary" @click="router.push('/demands')">去需求广场</el-button>
        </EmptyState>
      </div>
    </section>
  </PageContainer>
</template>

<script setup lang="ts">
import { View } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { fetchMyFavoriteDemands, unfavoriteDemand } from '@/api/demand'
import EmptyState from '@/components/common/EmptyState.vue'
import PageContainer from '@/components/common/PageContainer.vue'
import DemandStatusTag from '@/components/demand/DemandStatusTag.vue'
import type { Demand, DemandType } from '@/types/demand'

const router = useRouter()
const loading = ref(false)
const removingId = ref('')
const demands = ref<Demand[]>([])

onMounted(loadFavorites)

async function loadFavorites() {
  loading.value = true
  try {
    const response = await fetchMyFavoriteDemands()
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    demands.value = response.data.data
  } catch (error) {
    demands.value = []
    ElMessage.error(error instanceof Error ? error.message : '收藏加载失败')
  } finally {
    loading.value = false
  }
}

async function removeFavorite(id: string) {
  removingId.value = id
  try {
    const response = await unfavoriteDemand(id)
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    demands.value = demands.value.filter((demand) => demand.id !== id)
    ElMessage.success('已取消收藏')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '取消收藏失败')
  } finally {
    removingId.value = ''
  }
}

function typeLabel(type: DemandType) {
  const map: Record<DemandType, string> = {
    EXPRESS: '跑腿',
    SECONDHAND: '二手交易',
    TUTORING: '学习辅导',
    TEAM: '组队匹配',
    TEAMUP: '组队匹配',
    TYPE1: '类型一',
    TYPE2: '类型二',
  }
  return map[type]
}

function formatTime(value?: string) {
  return value ? value.replace('T', ' ').slice(0, 19) : ''
}
</script>

<style scoped>
.favorite-page {
  padding: var(--spacing-lg);
}

.page-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.page-head p {
  margin: -8px 0 0;
}

.favorite-area {
  min-height: 260px;
}

.favorite-list {
  display: grid;
  gap: 12px;
}

.favorite-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
}

.row-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

h2 {
  margin: 0;
  font-size: 17px;
}

.row-main p {
  display: -webkit-box;
  margin: 10px 0;
  overflow: hidden;
  color: var(--color-text-secondary);
  line-height: 1.55;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.meta-row,
.row-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.meta-row {
  color: var(--color-text-secondary);
  font-size: 13px;
}

.row-actions {
  justify-content: flex-end;
}

@media (max-width: 720px) {
  .page-head,
  .favorite-row,
  .row-title {
    grid-template-columns: 1fr;
    display: grid;
  }

  .row-actions {
    justify-content: flex-start;
  }
}
</style>
