<template>
  <PageContainer>
    <section class="chat-shell card">
      <header class="chat-head">
        <el-button :icon="ArrowLeft" text @click="router.push('/messages')" />
        <el-avatar :size="40">{{ peerInitial }}</el-avatar>
        <div>
          <h1>{{ conversationTitle }}</h1>
          <RouterLink v-if="conversation?.demandId" :to="`/demands/${conversation.demandId}`">
            {{ conversation.demandTitle }}
          </RouterLink>
        </div>
      </header>

      <div ref="messageListRef" class="message-list">
        <div v-if="loading" class="state-line">正在加载消息...</div>
        <EmptyState v-else-if="!messages.length" description="暂无消息" />
        <MessageBubble
          v-for="message in messages"
          v-else
          :key="message.id"
          :message="message"
          :mine="message.senderId === currentUserId"
        />
      </div>

      <MessageInput :disabled="!conversation || conversation.status !== 'ACTIVE'" @send="handleSend" />
    </section>
  </PageContainer>
</template>

<script setup lang="ts">
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import {
  fetchConversations,
  fetchMessages,
  markConversationRead,
  normalizeConversation,
  normalizeMessage,
  sendMessage,
} from '@/api/message'
import { createConversationSocket } from '@/api/messageSocket'
import EmptyState from '@/components/common/EmptyState.vue'
import PageContainer from '@/components/common/PageContainer.vue'
import MessageBubble from '@/components/message/MessageBubble.vue'
import MessageInput from '@/components/message/MessageInput.vue'
import { useAuthStore } from '@/stores/auth'
import type { Conversation, Message } from '@/types/message'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const currentUserId = computed(() => authStore.user?.id || '')
const conversationId = computed(() => String(route.params.conversationId || ''))
const conversation = ref<Conversation | null>(null)
const messages = ref<Message[]>([])
const loading = ref(false)
const socket = ref<ReturnType<typeof createConversationSocket> | null>(null)
const messageListRef = ref<HTMLElement | null>(null)

const conversationTitle = computed(() => conversation.value?.peerName || '会话')
const peerInitial = computed(() => conversation.value?.peerName.slice(0, 1) || '聊')

watch(conversationId, async () => {
  await resetSocket()
  await loadMessages()
  await activateSocket()
})

onMounted(async () => {
  await loadMessages()
  await activateSocket()
})

onBeforeUnmount(() => {
  socket.value?.deactivate()
})

async function loadConversationMeta() {
  const response = await fetchConversations()
  const result = response.data

  if (result.code !== 200) {
    throw new Error(result.message)
  }

  conversation.value =
    (result.data.conversations || [])
      .map((item) => normalizeConversation(item, currentUserId.value))
      .find((item) => item.id === conversationId.value) || null
}

async function loadMessages() {
  if (!conversationId.value) {
    return
  }

  loading.value = true

  try {
    await loadConversationMeta()

    const response = await fetchMessages(conversationId.value)
    const result = response.data

    if (result.code !== 200) {
      throw new Error(result.message)
    }

    messages.value = (result.data.messages || []).map((item) =>
      normalizeMessage(item, conversationId.value),
    )

    await markConversationRead(conversationId.value)
    await scrollToBottom()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载消息失败')
  } finally {
    loading.value = false
  }
}

async function handleSend(content: string) {
  try {
    const response = await sendMessage(conversationId.value, content)
    const result = response.data

    if (result.code !== 200) {
      throw new Error(result.message)
    }

    addMessage(normalizeMessage(result.data, conversationId.value))
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '发送失败')
  }
}

function addMessage(message: Message) {
  if (messages.value.some((item) => item.id === message.id)) {
    return
  }

  messages.value.push(message)
  scrollToBottom()
}

async function activateSocket() {
  if (!conversationId.value) {
    return
  }

  socket.value = createConversationSocket(conversationId.value, (message) => {
    addMessage(normalizeMessage(message, conversationId.value))
  })
  try {
    await socket.value.activate()
  } catch (error) {
    ElMessage.warning(error instanceof Error ? error.message : '实时消息连接失败')
  }
}

async function resetSocket() {
  await socket.value?.deactivate()
  socket.value = null
}

async function scrollToBottom() {
  await nextTick()
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}
</script>

<style scoped>
.chat-shell {
  display: grid;
  min-height: calc(100vh - 136px);
  grid-template-rows: auto 1fr auto;
  overflow: hidden;
}

.chat-head {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--color-border);
  background: #ffffff;
}

.chat-head h1 {
  margin: 0 0 4px;
  font-size: 18px;
}

.chat-head a {
  color: var(--color-primary);
  font-size: 14px;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: var(--spacing-lg);
  overflow: auto;
  background: #f8fafc;
}

.state-line {
  color: var(--color-text-secondary);
}
</style>
