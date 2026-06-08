import { defineStore } from 'pinia'

import { login as loginApi, register as registerApi } from '@/api/auth'
import type { AuthState, LoginPayload, RegisterPayload } from '@/types/auth'
import type { UserProfile } from '@/types/user'
import {
  getStoredJson,
  getToken,
  removeToken,
  setStoredJson,
  setToken,
  USER_KEY,
} from '@/utils/storage'

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: getToken(),
    user: getStoredJson<UserProfile>(USER_KEY),
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.token),
  },
  actions: {
    async login(payload: LoginPayload) {
      const response = await loginApi(payload)
      const result = response.data

      if (result.code !== 200) {
        throw new Error(result.message)
      }

      const user: UserProfile = {
        id: result.data.userUuid,
        username: payload.username,
        email: '',
      }

      this.token = result.data.token
      this.user = user
      setToken(result.data.token)
      setStoredJson(USER_KEY, user)
    },
    async register(payload: RegisterPayload) {
      const response = await registerApi(payload)
      const result = response.data

      if (result.code !== 200) {
        throw new Error(result.message)
      }

      const user: UserProfile = {
        id: result.data.userUuid,
        username: payload.username,
        email: payload.email || '',
      }

      this.token = null
      this.user = user
      setStoredJson(USER_KEY, user)

      await this.login({
        username: payload.username,
        password: payload.password,
      })
    },
    logout() {
      this.token = null
      this.user = null
      removeToken()
      localStorage.removeItem(USER_KEY)
    },
  },
})
