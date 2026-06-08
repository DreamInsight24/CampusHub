import http from './http'

import type { ApiResult } from '@/types/auth'
import type { UserProfile } from '@/types/user'

export function fetchProfile() {
  return http.get<ApiResult<UserProfile>>('/users/me')
}

export function updateProfile(payload: Partial<UserProfile>) {
  return http.patch<ApiResult<UserProfile>>('/users/me', payload)
}

export function uploadAvatar(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return http.post<ApiResult<{ url: string }>>('/users/me/avatar', formData)
}
