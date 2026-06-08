<template>
  <form class="message-input" @submit.prevent="submit">
    <el-input
      v-model="content"
      type="textarea"
      :rows="2"
      resize="none"
      placeholder="输入消息"
      :disabled="disabled"
    />
    <el-button type="primary" native-type="submit" :icon="Promotion" :disabled="disabled">
      发送
    </el-button>
  </form>
</template>

<script setup lang="ts">
import { Promotion } from '@element-plus/icons-vue'
import { ref } from 'vue'

const props = defineProps<{
  disabled?: boolean
}>()

const emit = defineEmits<{
  send: [content: string]
}>()

const content = ref('')

function submit() {
  const value = content.value.trim()

  if (!value || props.disabled) {
    return
  }

  emit('send', value)
  content.value = ''
}
</script>

<style scoped>
.message-input {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
  align-items: end;
  padding: var(--spacing-md);
  border-top: 1px solid var(--color-border);
  background: #ffffff;
}

@media (max-width: 640px) {
  .message-input {
    grid-template-columns: 1fr;
  }
}
</style>
