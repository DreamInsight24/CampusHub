import http from './http'

import type { ApiResult, LoginPayload, LoginResult, RegisterPayload, RegisterResult } from '@/types/auth'

export function login(payload: LoginPayload) {
  return http.post<ApiResult<LoginResult>>('/auth/login', payload)
}

export function register(payload: RegisterPayload) {
  return http.post<ApiResult<RegisterResult>>('/auth/register', {
    username: payload.username,
    password: payload.password,
  })
}

export function logout() {
  return http.post<void>('/auth/logout')
}
