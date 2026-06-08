export interface Message {
  id: string
  conversationId: string
  senderId: string
  senderName: string
  content: string
  createdAt: string
  read: boolean
  img?: boolean
}

export interface Conversation {
  id: string
  demandId: string
  demandTitle: string
  peerName: string
  peerAvatar?: string
  lastMessage: string
  lastMessageTime: string
  unreadCount: number
  ownerId?: string
  participantId?: string
  status?: string
}

export interface BackendConversation {
  uuid: string
  demandUuid?: string
  demand_uuid?: string
  demandTitle?: string
  demand_title?: string
  ownerId?: string
  owner_id?: string
  ownerName?: string
  owner_name?: string
  participantId?: string
  participant_id?: string
  participantName?: string
  participant_name?: string
  status: string
  lastMessage?: string
  last_message?: string
  lastMessageTime?: string
  last_message_time?: string
  createdAt?: string
  created_at?: string
  updatedAt?: string
  updated_at?: string
}

export interface BackendMessage {
  id?: number | string
  conversation_uuid?: string
  conversationUuid?: string
  user_uuid?: string
  senderUuid?: string
  message?: string
  content?: string
  time?: string
  sendTime?: string
  read?: boolean
  img?: boolean
}
