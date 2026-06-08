<template>
  <PageContainer>
    <section class="my-demands card">
      <div class="page-head">
        <div>
          <h1 class="page-title">我的订单</h1>
          <p class="muted">管理自己发布和接下的需求，在这里选择接单人并处理结束流程</p>
        </div>
        <el-button type="primary" :icon="Plus" @click="router.push('/demands/create')">
          发布需求
        </el-button>
      </div>

      <el-tabs v-model="activeTab" @tab-change="loadDemands">
        <el-tab-pane label="我发布的" name="published" />
        <el-tab-pane label="我接取的" name="accepted" />
      </el-tabs>

      <div v-loading="loading" class="demand-area">
        <div v-if="demands.length" class="demand-list">
          <article v-for="demand in demands" :key="demand.id" class="demand-row">
            <div class="demand-row-main">
              <div class="row-main">
                <div class="row-title">
                  <h2>{{ demand.title }}</h2>
                  <DemandStatusTag :status="demand.status" />
                </div>
                <p>{{ demand.description || '暂无说明' }}</p>
                <div class="meta-row">
                  <span>{{ typeLabel(demand.type) }}</span>
                  <span>发布时间：{{ formatTime(demand.createdAt) }}</span>
                  <span>{{ demand.location || '未填写地点' }}</span>
                  <span v-if="isApplicationDemand(demand)">申请：{{ applicationCount(demand.id) }} 人</span>
                </div>
              </div>

              <div
                v-if="activeTab === 'published' && isApplicationDemand(demand)"
                class="application-panel"
              >
                <div class="application-head">
                  <strong>申请人</strong>
                  <span class="muted">{{ applicationCount(demand.id) }} 人申请</span>
                </div>
                <div v-if="applicationsByDemand[demand.id]?.length" class="application-list">
                  <div
                    v-for="application in applicationsByDemand[demand.id]"
                    :key="application.id"
                    class="application-row"
                  >
                    <el-avatar :size="36" :src="application.applicantAvatar">
                      {{ application.applicantName?.slice(0, 1) || '用' }}
                    </el-avatar>
                    <div>
                      <div class="application-title">
                        <strong>{{ application.applicantName || shortId(application.applicantId) }}</strong>
                        <el-tag size="small" :type="applicationStatusType(application.status)">
                          {{ applicationStatusLabel(application.status) }}
                        </el-tag>
                      </div>
                      <p>{{ application.statement || '暂无申请说明' }}</p>
                    </div>
                    <div v-if="canReviewApplications(demand, application.status)" class="application-actions">
                      <el-button :icon="ChatDotRound" @click="contactApplicant(demand.id, application.applicantId)">
                        聊一聊
                      </el-button>
                      <el-button
                        type="primary"
                        :loading="handlingApplicationId === application.id"
                        @click="acceptApplication(demand.id, application.id)"
                      >
                        选为接单人
                      </el-button>
                      <el-button
                        type="danger"
                        plain
                        :loading="handlingApplicationId === application.id"
                        @click="rejectApplication(demand.id, application.id)"
                      >
                        拒绝
                      </el-button>
                    </div>
                  </div>
                </div>
                <p v-else class="application-empty">暂无申请</p>
              </div>
            </div>

            <div class="row-actions">
              <el-button :icon="View" @click="router.push(`/demands/${demand.id}`)">查看详情</el-button>
              <template v-if="activeTab === 'published' && canOperate(demand)">
                <el-button
                  type="danger"
                  plain
                  :icon="CircleClose"
                  :loading="handlingDemandId === demand.id && handlingAction === 'end'"
                  @click="confirmEndDemand(demand)"
                >
                  结束需求
                </el-button>
                <el-button
                  plain
                  :loading="handlingDemandId === demand.id && handlingAction === 'cancel'"
                  @click="confirmCancelDemand(demand)"
                >
                  取消
                </el-button>
              </template>
            </div>
          </article>
        </div>

        <EmptyState v-else-if="!loading" :description="emptyText">
          <el-button v-if="activeTab === 'published'" type="primary" @click="router.push('/demands/create')">
            发布需求
          </el-button>
          <el-button v-else type="primary" @click="router.push('/demands')">去需求广场</el-button>
        </EmptyState>
      </div>
    </section>
  </PageContainer>
</template>

<script setup lang="ts">
import { ChatDotRound, CircleClose, Plus, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import {
  acceptDemandApplication,
  cancelDemand as cancelDemandApi,
  endDemand as endDemandApi,
  fetchDemandApplications,
  fetchMyAcceptedDemands,
  fetchMyPublishedDemands,
  rejectDemandApplication,
} from '@/api/demand'
import { createConversation } from '@/api/message'
import EmptyState from '@/components/common/EmptyState.vue'
import PageContainer from '@/components/common/PageContainer.vue'
import DemandStatusTag from '@/components/demand/DemandStatusTag.vue'
import type { Demand, DemandApplication, DemandApplicationStatus, DemandType } from '@/types/demand'

const router = useRouter()
const activeTab = ref<'published' | 'accepted'>('published')
const loading = ref(false)
const demands = ref<Demand[]>([])
const applicationsByDemand = reactive<Record<string, DemandApplication[]>>({})
const handlingDemandId = ref('')
const handlingAction = ref<'end' | 'cancel' | ''>('')
const handlingApplicationId = ref('')

const emptyText = computed(() =>
  activeTab.value === 'published' ? '还没有发布过需求' : '还没有接取过需求',
)

onMounted(loadDemands)

async function loadDemands() {
  loading.value = true
  try {
    const response =
      activeTab.value === 'published'
        ? await fetchMyPublishedDemands()
        : await fetchMyAcceptedDemands()

    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    demands.value = response.data.data
    await loadApplicationsForPublishedDemands()
  } catch (error) {
    demands.value = []
    ElMessage.error(error instanceof Error ? error.message : '我的需求加载失败')
  } finally {
    loading.value = false
  }
}

async function loadApplicationsForPublishedDemands() {
  Object.keys(applicationsByDemand).forEach((id) => {
    delete applicationsByDemand[id]
  })

  if (activeTab.value !== 'published') {
    return
  }

  await Promise.all(
    demands.value
      .filter(isApplicationDemand)
      .map(async (demand) => {
        const response = await fetchDemandApplications(demand.id)
        if (response.data.code === 200) {
          applicationsByDemand[demand.id] = response.data.data || []
        }
      }),
  )
}

async function confirmEndDemand(demand: Demand) {
  try {
    await ElMessageBox.confirm('结束后该需求将不再接受新的申请或接取。', '确认结束需求？', {
      confirmButtonText: '确认结束',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await updateDemandStatus(demand.id, 'end')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '操作失败')
    }
  }
}

async function confirmCancelDemand(demand: Demand) {
  try {
    await ElMessageBox.confirm('取消后该需求将不再接受新的申请或接取。', '确认取消需求？', {
      confirmButtonText: '确认取消',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await updateDemandStatus(demand.id, 'cancel')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '操作失败')
    }
  }
}

async function updateDemandStatus(id: string, action: 'end' | 'cancel') {
  handlingDemandId.value = id
  handlingAction.value = action
  try {
    const response = action === 'end' ? await endDemandApi(id) : await cancelDemandApi(id)
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    ElMessage.success(action === 'end' ? '需求已结束' : '需求已取消')
    await loadDemands()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '操作失败')
    }
  } finally {
    handlingDemandId.value = ''
    handlingAction.value = ''
  }
}

function canOperate(demand: Demand) {
  return !['CLOSED', 'CANCELLED'].includes(demand.status)
}

function isApplicationDemand(demand: Demand) {
  return demand.type !== 'SECONDHAND'
}

function applicationCount(demandId: string) {
  return applicationsByDemand[demandId]?.length || 0
}

function canReviewApplications(demand: Demand, status: DemandApplicationStatus) {
  return demand.status === 'OPEN' && status === 'PENDING'
}

async function acceptApplication(demandId: string, applicationId: string) {
  handlingApplicationId.value = applicationId
  try {
    const response = await acceptDemandApplication(demandId, applicationId)
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    ElMessage.success('已选择接单人')
    await loadDemands()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '接受申请失败')
  } finally {
    handlingApplicationId.value = ''
  }
}

async function rejectApplication(demandId: string, applicationId: string) {
  handlingApplicationId.value = applicationId
  try {
    const response = await rejectDemandApplication(demandId, applicationId)
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    ElMessage.success('已拒绝申请')
    await loadApplicationsForPublishedDemands()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '拒绝申请失败')
  } finally {
    handlingApplicationId.value = ''
  }
}

async function contactApplicant(demandId: string, applicantId: string) {
  try {
    const response = await createConversation(demandId, applicantId)
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    await router.push({ name: 'chat', params: { conversationId: response.data.data.conversationUuid } })
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '会话创建失败')
  }
}

function applicationStatusLabel(status: DemandApplicationStatus) {
  const map: Record<DemandApplicationStatus, string> = {
    PENDING: '待确认',
    ACCEPTED: '已接受',
    REJECTED: '已拒绝',
    EXPIRED: '已失效',
  }
  return map[status]
}

function applicationStatusType(status: DemandApplicationStatus) {
  const map: Record<DemandApplicationStatus, 'success' | 'warning' | 'info' | 'danger'> = {
    PENDING: 'warning',
    ACCEPTED: 'success',
    REJECTED: 'danger',
    EXPIRED: 'info',
  }
  return map[status]
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

function shortId(id: string) {
  return `用户 ${id.slice(0, 8)}`
}
</script>

<style scoped>
.my-demands {
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

.demand-area {
  min-height: 260px;
}

.demand-list {
  display: grid;
  gap: 12px;
}

.demand-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: start;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
}

.demand-row-main {
  min-width: 0;
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

.meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  color: var(--color-text-secondary);
  font-size: 13px;
}

.row-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.application-panel {
  margin-top: var(--spacing-md);
  padding: 12px;
  border-radius: var(--radius-card);
  background: #f8fafc;
}

.application-head,
.application-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.application-head {
  justify-content: space-between;
  margin-bottom: 10px;
}

.application-list {
  display: grid;
  gap: 10px;
}

.application-row {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
  background: #ffffff;
}

.application-row p,
.application-empty {
  margin: 4px 0 0;
  color: var(--color-text-secondary);
  font-size: 13px;
  line-height: 1.5;
}

.application-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

@media (max-width: 720px) {
  .page-head,
  .demand-row,
  .row-title,
  .application-row {
    grid-template-columns: 1fr;
    display: grid;
  }

  .row-actions,
  .application-actions {
    justify-content: flex-start;
  }
}
</style>
