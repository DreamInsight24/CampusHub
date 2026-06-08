import { defineStore } from 'pinia'

import type { Message } from '@/types/message'

interface MessageStoreState {
  draftByConversation: Record<string, string>
}

export const useMessageStore = defineStore('message', {
  state: (): MessageStoreState => ({
    draftByConversation: {},
  }),
  actions: {
    setDraft(conversationId: string, value: string) {
      this.draftByConversation[conversationId] = value
    },
    createLocalMessage(conversationId: string, content: string): Message {
      return {
        id: `local-${Date.now()}`,
        conversationId,
        senderId: 'u-001',
        senderName: '我',
        content,
        createdAt: new Date().toISOString(),
        read: true,
      }
    },
  },
})
