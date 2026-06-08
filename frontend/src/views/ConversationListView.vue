<template>
  <PageContainer>
    <div class="message-layout">
      <section>
        <div class="list-head">
          <div>
            <h1 class="page-title">消息</h1>
            <p class="muted">查看你和需求相关用户之间的会话</p>
          </div>
          <el-button :loading="loading" @click="loadConversations">刷新</el-button>
        </div>

        <div v-if="loading" class="loading-card card">正在加载会话...</div>

        <div v-else-if="conversations.length" class="conversation-list">
          <ConversationItem
            v-for="conversation in conversations"
            :key="conversation.id"
            :conversation="conversation"
          />
        </div>

        <EmptyState v-else description="暂无会话">
          <el-button type="primary" @click="router.push('/demands')">去需求广场</el-button>
        </EmptyState>
      </section>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { fetchConversations, normalizeConversation } from '@/api/message'
import EmptyState from '@/components/common/EmptyState.vue'
import PageContainer from '@/components/common/PageContainer.vue'
import ConversationItem from '@/components/message/ConversationItem.vue'
import { useAuthStore } from '@/stores/auth'
import type { Conversation } from '@/types/message'

const authStore = useAuthStore()
const router = useRouter()
const conversations = ref<Conversation[]>([])
const loading = ref(false)

async function loadConversations() {
  loading.value = true

  try {
    const response = await fetchConversations()
    const result = response.data

    if (result.code !== 200) {
      throw new Error(result.message)
    }

    conversations.value = (result.data.conversations || []).map((item) =>
      normalizeConversation(item, authStore.user?.id),
    )
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载会话失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadConversations)
</script>

<style scoped>
.message-layout {
  max-width: 820px;
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

.conversation-list {
  display: grid;
  gap: 12px;
}

.loading-card {
  padding: var(--spacing-lg);
  color: var(--color-text-secondary);
}
</style>
