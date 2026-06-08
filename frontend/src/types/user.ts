export interface UserProfile {
  id: string
  username: string
  nickname?: string
  phone?: string
  email?: string
  avatarUrl?: string
  avatar?: string
  creditScore?: number
  bio?: string
  school?: string
  major?: string
  grade?: string
  interests?: string[]
  tags?: string[]
  campus?: string
}
