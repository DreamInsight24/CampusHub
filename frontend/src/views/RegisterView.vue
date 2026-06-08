<template>
  <div class="auth-page">
    <section class="auth-panel card">
      <div class="auth-head">
        <RouterLink class="brand" to="/demands">CampusHub</RouterLink>
        <h1>注册</h1>
        <p>创建校园互助账号</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" autocomplete="email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            show-password
            type="password"
            placeholder="请输入密码"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            show-password
            type="password"
            placeholder="请再次输入密码"
          />
        </el-form-item>
        <el-button class="submit-button" type="primary" :loading="loading" @click="handleRegister">
          注册
        </el-button>
      </el-form>

      <p class="switch-link">
        已有账号？
        <RouterLink to="/login">去登录</RouterLink>
      </p>
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

import { useAuthStore } from '@/stores/auth'
import type { RegisterPayload } from '@/types/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive<RegisterPayload>({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
})

const rules: FormRules<RegisterPayload> = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效邮箱', trigger: 'blur' },
  ],
  password: [{ required: true, min: 6, message: '密码至少 6 位', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入的密码不一致'))
          return
        }
        callback()
      },
      trigger: 'blur',
    },
  ],
}

async function handleRegister() {
  await formRef.value?.validate()
  loading.value = true

  try {
    await authStore.register(form)
    router.push('/demands')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '注册失败')
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
  width: min(440px, 100%);
  padding: 28px;
}

.auth-head {
  margin-bottom: var(--spacing-lg);
}

.brand,
.switch-link a {
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
</style>
