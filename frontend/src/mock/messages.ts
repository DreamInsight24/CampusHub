import type { Conversation, Message } from '@/types/message'

export const mockConversations: Conversation[] = [
  {
    id: 'c-001',
    demandId: 'd-1001',
    demandTitle: '今晚帮取东门快递柜两个小件',
    peerName: '林同学',
    lastMessage: '我把取件码发你，麻烦啦。',
    lastMessageTime: '2026-05-28 10:12',
    unreadCount: 2,
  },
  {
    id: 'c-002',
    demandId: 'd-1003',
    demandTitle: '高数期末复习答疑',
    peerName: '周同学',
    lastMessage: '今晚七点二教可以吗？',
    lastMessageTime: '2026-05-27 20:36',
    unreadCount: 0,
  },
]

export const mockMessages: Message[] = [
  {
    id: 'm-001',
    conversationId: 'c-001',
    senderId: 'u-002',
    senderName: '林同学',
    content: '你好，我看到你响应了快递代取。',
    createdAt: '2026-05-28 10:08',
    read: true,
  },
  {
    id: 'm-002',
    conversationId: 'c-001',
    senderId: 'u-001',
    senderName: '我',
    content: '可以的，我今晚路过东门。',
    createdAt: '2026-05-28 10:10',
    read: true,
  },
  {
    id: 'm-003',
    conversationId: 'c-001',
    senderId: 'u-002',
    senderName: '林同学',
    content: '我把取件码发你，麻烦啦。',
    createdAt: '2026-05-28 10:12',
    read: false,
  },
  {
    id: 'm-004',
    conversationId: 'c-002',
    senderId: 'u-003',
    senderName: '周同学',
    content: '今晚七点二教可以吗？',
    createdAt: '2026-05-27 20:36',
    read: true,
  },
]
