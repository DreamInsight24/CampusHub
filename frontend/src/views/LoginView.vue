<template>
  <div class="auth-page">
    <section class="auth-panel card">
      <div class="auth-head">
        <RouterLink class="brand" to="/demands">CampusHub</RouterLink>
        <h1>登录</h1>
        <p>进入校园互助需求广场</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" autocomplete="username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            autocomplete="current-password"
            placeholder="请输入密码"
            show-password
            type="password"
          />
        </el-form-item>
        <el-button class="submit-button" type="primary" :loading="loading" @click="handleLogin">
          登录
        </el-button>
      </el-form>

      <p class="switch-link">
        还没有账号？
        <RouterLink to="/register">去注册</RouterLink>
      </p>
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

import { useAuthStore } from '@/stores/auth'
import type { LoginPayload } from '@/types/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive<LoginPayload>({
  username: '',
  password: '',
})

const rules: FormRules<LoginPayload> = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  await formRef.value?.validate()
  loading.value = true

  try {
    await authStore.login(form)
    router.push((route.query.redirect as string) || '/demands')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  display: grid;
  min-height: 100vh;
  place-items: center;
  padding: var(--spacing-lg);
}

.auth-panel {
  width: min(420px, 100%);
  padding: 28px;
}

.auth-head {
  margin-bottom: var(--spacing-lg);
}

.brand {
  color: var(--color-primary);
  font-weight: 800;
}

h1 {
  margin: 18px 0 8px;
  font-size: 26px;
}

p {
  margin: 0;
  color: var(--color-text-secondary);
}

.submit-button {
  width: 100%;
}

.switch-link {
  margin-top: var(--spacing-md);
  text-align: center;
}

.switch-link a {
  color: var(--color-primary);
  font-weight: 650;
}
</style>
