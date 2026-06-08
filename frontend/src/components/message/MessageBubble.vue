<template>
  <div class="bubble-row" :class="{ mine }">
    <div class="bubble">
      <p>{{ message.content }}</p>
      <span>{{ message.createdAt }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Message } from '@/types/message'

defineProps<{
  message: Message
  mine?: boolean
}>()
</script>

<style scoped>
.bubble-row {
  display: flex;
  justify-content: flex-start;
}

.bubble-row.mine {
  justify-content: flex-end;
}

.bubble {
  max-width: min(68%, 560px);
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-card);
  background: #ffffff;
}

.mine .bubble {
  border-color: #bfd7ff;
  background: #eef4ff;
}

p {
  margin: 0;
  line-height: 1.55;
}

span {
  display: block;
  margin-top: 6px;
  color: var(--color-text-secondary);
  font-size: 12px;
}
</style>
