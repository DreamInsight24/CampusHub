const TOKEN_KEY = 'campushub_token'
const USER_KEY = 'campushub_user'

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}

export function getStoredJson<T>(key: string): T | null {
  const raw = localStorage.getItem(key)

  if (!raw) {
    return null
  }

  try {
    return JSON.parse(raw) as T
  } catch {
    localStorage.removeItem(key)
    return null
  }
}

export function setStoredJson<T>(key: string, value: T): void {
  localStorage.setItem(key, JSON.stringify(value))
}

export { USER_KEY }
