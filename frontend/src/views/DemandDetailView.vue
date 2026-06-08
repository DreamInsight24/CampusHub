<template>
  <PageContainer>
    <section v-loading="loading" class="detail-panel card">
      <template v-if="demand">
        <div class="detail-head">
          <div>
            <div class="eyebrow">{{ typeLabel }}</div>
            <h1 class="page-title">{{ demand.title }}</h1>
            <p class="muted">{{ demand.publisherName }} · {{ demand.location || '未填写地点' }}</p>
          </div>
          <DemandStatusTag :status="demand.status" />
        </div>

        <div class="action-row">
          <template v-if="isPublisher">
            <el-button
              v-if="canOperateDemand"
              type="danger"
              plain
              :icon="CircleClose"
              :loading="ending"
              @click="confirmEndDemand"
            >
              结束需求
            </el-button>
          </template>

          <template v-else>
            <el-button
              v-if="applicationButtonVisible"
              type="primary"
              :icon="Check"
              :loading="responding"
              :disabled="!canApply"
              @click="respond"
            >
              {{ applicationButtonText }}
            </el-button>
            <el-button
              v-else-if="demand.status === 'CLOSED'"
              disabled
            >
              已结束
            </el-button>
            <el-button
              v-else-if="!isApplicationDemand"
              type="primary"
              :icon="Check"
              :loading="responding"
              :disabled="demand.status !== 'OPEN'"
              @click="respond"
            >
              接取需求
            </el-button>
            <el-button
              :icon="ChatDotRound"
              :loading="creatingConversation"
              :disabled="!demand.publisherId"
              @click="contactPublisher"
            >
              联系发布者
            </el-button>
          </template>

          <el-button :icon="Back" @click="router.push('/demands')">返回广场</el-button>
        </div>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="需求类型">{{ typeLabel }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ statusLabel }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ formatTime(demand.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="截止时间">{{ formatTime(demand.deadline) || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="发布者">{{ demand.publisherName }}</el-descriptions-item>
          <el-descriptions-item label="接取人">{{ demand.takerId ? shortId(demand.takerId) : '暂无' }}</el-descriptions-item>
        </el-descriptions>

        <section class="section-block">
          <h2 class="section-title">需求说明</h2>
          <p class="description">{{ demand.description || '暂无说明' }}</p>
        </section>

        <section class="section-block">
          <h2 class="section-title">{{ typeLabel }}信息</h2>
          <div class="field-grid">
            <div v-for="field in typeFields" :key="field.label" class="field-row">
              <span>{{ field.label }}</span>
              <strong>{{ field.value }}</strong>
            </div>
          </div>
        </section>

        <section v-if="isPublisher && isApplicationDemand" class="section-block">
          <div class="section-head">
            <h2 class="section-title">申请列表</h2>
            <span class="muted">{{ applications.length }} 人申请</span>
          </div>

          <div v-if="applications.length" class="application-list">
            <article v-for="application in applications" :key="application.id" class="application-row">
              <el-avatar :size="44" :src="application.applicantAvatar">
                {{ application.applicantName?.slice(0, 1) || '用' }}
              </el-avatar>
              <div class="application-main">
                <div class="application-title">
                  <strong>{{ application.applicantName || shortId(application.applicantId) }}</strong>
                  <el-tag size="small" :type="applicationStatusType(application.status)">
                    {{ applicationStatusLabel(application.status) }}
                  </el-tag>
                </div>
                <p>{{ application.statement || '暂无申请说明' }}</p>
                <span class="muted">申请时间：{{ formatTime(application.appliedAt) }}</span>
              </div>
              <div v-if="canReviewApplications && application.status === 'PENDING'" class="application-actions">
                <el-button :icon="ChatDotRound" @click="contactApplicant(application.applicantId)">聊一聊</el-button>
                <el-button
                  type="primary"
                  :loading="handlingApplicationId === application.id"
                  @click="acceptApplication(application.id)"
                >
                  接受
                </el-button>
                <el-button
                  type="danger"
                  plain
                  :loading="handlingApplicationId === application.id"
                  @click="rejectApplication(application.id)"
                >
                  拒绝
                </el-button>
              </div>
            </article>
          </div>

          <EmptyState v-else description="暂无申请" />
        </section>

        <section v-if="demand.imageUrls?.length" class="section-block">
          <h2 class="section-title">图片</h2>
          <div class="image-grid">
            <img v-for="url in demand.imageUrls" :key="url" :src="url" :alt="demand.title" />
          </div>
        </section>
      </template>

      <EmptyState v-else-if="!loading" description="未找到该需求">
        <el-button type="primary" @click="router.push('/demands')">返回需求广场</el-button>
      </EmptyState>
    </section>
  </PageContainer>
</template>

<script setup lang="ts">
import { Back, ChatDotRound, Check, CircleClose } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import {
  acceptDemandApplication,
  endDemand as endDemandApi,
  fetchDemandApplications,
  fetchDemandById,
  fetchMyDemandApplication,
  rejectDemandApplication,
  respondDemand,
} from '@/api/demand'
import { createConversation } from '@/api/message'
import EmptyState from '@/components/common/EmptyState.vue'
import PageContainer from '@/components/common/PageContainer.vue'
import DemandStatusTag from '@/components/demand/DemandStatusTag.vue'
import { useAuthStore } from '@/stores/auth'
import type {
  Demand,
  DemandApplication,
  DemandApplicationStatus,
  DemandStatus,
  DemandType,
} from '@/types/demand'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const demand = ref<Demand | null>(null)
const applications = ref<DemandApplication[]>([])
const myApplication = ref<DemandApplication | null>(null)
const loading = ref(false)
const responding = ref(false)
const ending = ref(false)
const creatingConversation = ref(false)
const handlingApplicationId = ref('')

const typeMap: Record<DemandType, string> = {
  EXPRESS: '跑腿',
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

const typeLabel = computed(() => (demand.value ? typeMap[demand.value.type] : ''))
const statusLabel = computed(() => (demand.value ? statusMap[demand.value.status] : ''))
const isPublisher = computed(() => demand.value?.publisherId === authStore.user?.id)
const normalizedType = computed(() => (demand.value?.type === 'TEAM' ? 'TEAMUP' : demand.value?.type))
const isApplicationDemand = computed(() => normalizedType.value === 'EXPRESS' || normalizedType.value === 'TEAMUP')
const canOperateDemand = computed(() => Boolean(demand.value && !['CLOSED', 'CANCELLED'].includes(demand.value.status)))
const canReviewApplications = computed(() => Boolean(demand.value?.status === 'OPEN'))
const canApply = computed(() =>
  Boolean(
    demand.value &&
      demand.value.status === 'OPEN' &&
      !isPublisher.value &&
      isApplicationDemand.value &&
      !myApplication.value,
  ),
)
const applicationButtonVisible = computed(() =>
  Boolean(isApplicationDemand.value && demand.value?.status === 'OPEN' && (canApply.value || myApplication.value)),
)
const applicationButtonText = computed(() => {
  if (myApplication.value?.status === 'PENDING') {
    return '已申请，等待确认'
  }
  if (myApplication.value?.status === 'ACCEPTED') {
    return normalizedType.value === 'EXPRESS' ? '已接受' : '已加入'
  }
  if (myApplication.value?.status === 'REJECTED') {
    return '已拒绝'
  }
  if (myApplication.value?.status === 'EXPIRED') {
    return '已失效'
  }
  return normalizedType.value === 'EXPRESS' ? '申请接取' : '申请加入'
})

const typeFields = computed(() => {
  if (!demand.value) {
    return []
  }

  const item = demand.value
  if (item.type === 'EXPRESS') {
    return compactFields([
      ['取件地点', item.pickupLocation],
      ['送达地点', item.deliveryLocation],
      ['取件码', item.pickupCode],
      ['期望送达时间', formatTime(item.expectedDeliveryTime)],
    ])
  }

  if (item.type === 'SECONDHAND') {
    return compactFields([
      ['物品名称', item.itemName],
      ['分类', item.category],
      ['价格', money(item.price)],
      ['原价', money(item.originalPrice)],
      ['成色', item.conditionLevel],
      ['交易地点', item.tradeLocation],
    ])
  }

  if (item.type === 'TUTORING') {
    return compactFields([
      ['科目', item.subject],
      ['辅导方式', item.tutoringMode],
      ['期望时间', formatTime(item.expectedTime)],
      ['时长', item.duration ? `${item.duration} 分钟` : ''],
      ['水平要求', item.levelRequirement],
    ])
  }

  return compactFields([
    ['组队目标', item.teamGoal],
    ['当前人数', numberText(item.currentMembers)],
    ['目标人数', numberText(item.expectedMembers)],
    ['所需技能', item.requiredSkills?.join('、')],
    ['联系方式', item.contactMethod],
  ])
})

onMounted(loadDemand)

async function loadDemand() {
  loading.value = true
  try {
    const response = await fetchDemandById(String(route.params.id))
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    demand.value = response.data.data
    await loadApplicationContext()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '需求详情加载失败')
    demand.value = null
  } finally {
    loading.value = false
  }
}

async function loadApplicationContext() {
  applications.value = []
  myApplication.value = null
  if (!demand.value || !isApplicationDemand.value || !authStore.user?.id) {
    return
  }

  if (isPublisher.value) {
    const response = await fetchDemandApplications(demand.value.id)
    if (response.data.code === 200) {
      applications.value = response.data.data || []
    }
    return
  }

  const response = await fetchMyDemandApplication(demand.value.id)
  if (response.data.code === 200) {
    myApplication.value = response.data.data
  }
}

async function respond() {
  if (!demand.value) {
    return
  }

  responding.value = true
  try {
    const response = await respondDemand(demand.value.id)
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    demand.value = response.data.data
    await loadApplicationContext()
    ElMessage.success(isApplicationDemand.value ? '申请已提交' : '已接取需求')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '操作失败')
  } finally {
    responding.value = false
  }
}

async function confirmEndDemand() {
  if (!demand.value) {
    return
  }

  await ElMessageBox.confirm('结束后该需求将不再接受新的申请或接取。', '确认结束需求？', {
    confirmButtonText: '确认结束',
    cancelButtonText: '取消',
    type: 'warning',
  })

  ending.value = true
  try {
    const response = await endDemandApi(demand.value.id)
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    demand.value = response.data.data
    await loadApplicationContext()
    ElMessage.success('需求已结束')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '结束需求失败')
    }
  } finally {
    ending.value = false
  }
}

async function acceptApplication(applicationId: string) {
  if (!demand.value) {
    return
  }

  handlingApplicationId.value = applicationId
  try {
    const response = await acceptDemandApplication(demand.value.id, applicationId)
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    demand.value = response.data.data
    await loadApplicationContext()
    ElMessage.success('已接受申请')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '接受申请失败')
  } finally {
    handlingApplicationId.value = ''
  }
}

async function rejectApplication(applicationId: string) {
  if (!demand.value) {
    return
  }

  handlingApplicationId.value = applicationId
  try {
    const response = await rejectDemandApplication(demand.value.id, applicationId)
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    await loadApplicationContext()
    ElMessage.success('已拒绝申请')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '拒绝申请失败')
  } finally {
    handlingApplicationId.value = ''
  }
}

async function contactPublisher() {
  if (!demand.value || !demand.value.publisherId || !authStore.user?.id) {
    ElMessage.warning('请先登录')
    return
  }

  await openConversation(demand.value.id)
}

async function contactApplicant(applicantId: string) {
  if (!demand.value) {
    return
  }

  await openConversation(demand.value.id, applicantId)
}

async function openConversation(demandId: string, participantId?: string) {
  creatingConversation.value = true
  try {
    const response = await createConversation(demandId, participantId)
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    const conversationId = response.data.data.conversationUuid
    if (!conversationId) {
      throw new Error('后端未返回会话ID')
    }
    await router.push({ name: 'chat', params: { conversationId } })
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '会话创建失败')
  } finally {
    creatingConversation.value = false
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

function compactFields(fields: Array<[string, string | number | null | undefined]>) {
  return fields.map(([label, value]) => ({ label, value: value || '未填写' }))
}

function formatTime(value?: string) {
  return value ? value.replace('T', ' ').slice(0, 19) : ''
}

function money(value?: number | null) {
  return value == null ? '' : `¥${value}`
}

function numberText(value?: number | null) {
  return value == null ? '' : String(value)
}

function shortId(id: string) {
  return `用户 ${id.slice(0, 8)}`
}
</script>

<style scoped>
.detail-panel {
  min-height: 360px;
  padding: var(--spacing-lg);
}

.detail-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--spacing-md);
}

.eyebrow {
  margin-bottom: 8px;
  color: var(--color-primary);
  font-size: 13px;
  font-weight: 700;
}

.action-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin: var(--spacing-lg) 0;
}

.section-block {
  margin-top: var(--spacing-lg);
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.description {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.75;
  white-space: pre-wrap;
}

.field-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.field-row {
  display: grid;
  gap: 6px;
  padding: 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
  background: #ffffff;
}

.field-row span {
  color: var(--color-text-secondary);
  font-size: 13px;
}

.field-row strong {
  font-size: 15px;
  line-height: 1.45;
}

.application-list {
  display: grid;
  gap: 12px;
}

.application-row {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
}

.application-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.application-main p {
  margin: 6px 0;
  color: var(--color-text-secondary);
  line-height: 1.55;
}

.application-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: flex-end;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 12px;
}

.image-grid img {
  width: 100%;
  aspect-ratio: 4 / 3;
  border-radius: var(--radius-card);
  object-fit: cover;
}

@media (max-width: 720px) {
  .detail-head,
  .field-grid,
  .application-row {
    grid-template-columns: 1fr;
    display: grid;
  }

  .application-actions {
    justify-content: flex-start;
  }
}
</style>
