import http from './http'

import type { ApiResult } from '@/types/auth'
import type { BackendConversation, BackendMessage, Conversation, Message } from '@/types/message'

interface ConversationQueryData {
  conversations: BackendConversation[]
}

interface MessageQueryData {
  conversationUuid: string
  messages: BackendMessage[]
}

export function fetchConversations() {
  return http.get<ApiResult<ConversationQueryData>>('/conversations/query')
}

export function createConversation(demandId: string, participantId?: string) {
  return http.post<ApiResult<{ conversationUuid: string }>>(`/conversations/demands/${demandId}`, {
    participantId,
  })
}

export function fetchMessages(conversationId: string) {
  return http.get<ApiResult<MessageQueryData>>(`/conversations/${conversationId}/messages`)
}

export function sendMessage(conversationId: string, content: string) {
  return http.post<ApiResult<BackendMessage>>(`/conversations/${conversationId}/messages`, {
    message: content,
  })
}

export function markConversationRead(conversationId: string) {
  return http.put<ApiResult<{ conversationUuid: string; readCount: number }>>(
    `/conversations/${conversationId}/read`,
    {},
  )
}

export function normalizeConversation(
  conversation: BackendConversation,
  currentUserId?: string,
): Conversation {
  const demandId = conversation.demandUuid || conversation.demand_uuid || ''
  const ownerId = conversation.ownerId || conversation.owner_id || ''
  const participantId = conversation.participantId || conversation.participant_id || ''
  const isOwner = currentUserId === ownerId
  const peerId = isOwner ? participantId : ownerId
  const peerName = isOwner
    ? conversation.participantName || conversation.participant_name
    : conversation.ownerName || conversation.owner_name

  return {
    id: conversation.uuid,
    demandId,
    demandTitle: conversation.demandTitle || conversation.demand_title || '关联需求',
    peerName: peerName || `用户 ${shortId(peerId)}`,
    lastMessage:
      conversation.lastMessage ||
      conversation.last_message ||
      (conversation.status === 'ACTIVE' ? '暂无消息，点击开始聊天' : '会话已关闭'),
    lastMessageTime: formatTime(
      conversation.lastMessageTime ||
        conversation.last_message_time ||
        conversation.updatedAt ||
        conversation.updated_at ||
        conversation.createdAt ||
        conversation.created_at,
    ),
    unreadCount: 0,
    ownerId,
    participantId,
    status: conversation.status,
  }
}

export function normalizeMessage(message: BackendMessage, fallbackConversationId = ''): Message {
  const conversationId = message.conversationUuid || message.conversation_uuid || fallbackConversationId
  const senderId = message.senderUuid || message.user_uuid || ''
  const createdAt = message.sendTime || message.time || ''
  const content = message.content || message.message || ''

  return {
    id: String(message.id || `${conversationId}-${senderId}-${createdAt}-${content}`),
    conversationId,
    senderId,
    senderName: `用户 ${shortId(senderId)}`,
    content,
    createdAt: formatTime(createdAt),
    read: Boolean(message.read),
    img: Boolean(message.img),
  }
}

function shortId(id?: string) {
  if (!id) {
    return '未知'
  }

  return id.slice(0, 8)
}

function formatTime(value?: string) {
  if (!value) {
    return ''
  }

  return value.replace('T', ' ').slice(0, 19)
}
