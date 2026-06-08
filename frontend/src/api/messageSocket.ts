import { Client, type IMessage, type StompSubscription } from '@stomp/stompjs'

import type { BackendMessage } from '@/types/message'

function getSocketBaseUrl() {
  const apiBase = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return apiBase.replace(/\/api\/?$/, '')
}

export function createConversationSocket(
  conversationId: string,
  onMessage: (message: BackendMessage) => void,
) {
  let subscription: StompSubscription | null = null

  const client = new Client({
    webSocketFactory: () => {
      throw new Error('SockJS factory is initialized asynchronously')
    },
    reconnectDelay: 5000,
    onConnect: () => {
      subscription = client.subscribe(`/topic/conversation/${conversationId}`, (frame: IMessage) => {
        onMessage(JSON.parse(frame.body) as BackendMessage)
      })
    },
  })

  return {
    async activate() {
      const SockJS = (await import('sockjs-client')).default
      client.webSocketFactory = () => new SockJS(`${getSocketBaseUrl()}/ws`)
      client.activate()
    },
    deactivate() {
      subscription?.unsubscribe()
      return client.deactivate()
    },
  }
}
