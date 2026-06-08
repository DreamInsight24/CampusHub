import type { UserProfile } from './user'

export interface LoginPayload {
  username: string
  password: string
}

export interface RegisterPayload {
  username: string
  password: string
  email?: string
  confirmPassword?: string
}

export interface AuthState {
  token: string | null
  user: UserProfile | null
}

export interface LoginResult {
  token: string
  userUuid: string
}

export interface RegisterResult {
  userUuid: string
  username: string
}

export interface ApiResult<T> {
  code: number
  message: string
  data: T
}
