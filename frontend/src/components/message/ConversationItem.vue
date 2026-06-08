<template>
  <RouterLink
    class="conversation-item card"
    :to="{ name: 'chat', params: { conversationId: conversation.id } }"
  >
    <el-avatar :size="42" :src="conversation.peerAvatar">
      {{ conversation.peerName.slice(0, 1) }}
    </el-avatar>
    <div class="conversation-main">
      <div class="top-row">
        <strong>{{ conversation.peerName }}</strong>
        <span>{{ conversation.lastMessageTime }}</span>
      </div>
      <p class="demand-title">{{ conversation.demandTitle }}</p>
      <p class="last-message">{{ conversation.lastMessage }}</p>
    </div>
    <el-badge v-if="conversation.unreadCount" :value="conversation.unreadCount" />
  </RouterLink>
</template>

<script setup lang="ts">
import type { Conversation } from '@/types/message'

defineProps<{
  conversation: Conversation
}>()
</script>

<style scoped>
.conversation-item {
  display: grid;
  width: 100%;
  grid-template-columns: auto 1fr auto;
  gap: var(--spacing-md);
  align-items: center;
  padding: var(--spacing-md);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
  background: var(--color-card);
}

.conversation-item:hover {
  border-color: var(--color-primary);
}

.conversation-main {
  min-width: 0;
}

.top-row {
  display: flex;
  justify-content: space-between;
  gap: var(--spacing-md);
  font-size: 14px;
}

.top-row span,
.demand-title,
.last-message {
  color: var(--color-text-secondary);
}

.demand-title,
.last-message {
  margin: 6px 0 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.demand-title {
  font-size: 13px;
}

.last-message {
  font-size: 14px;
}
</style>
