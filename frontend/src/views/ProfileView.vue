<template>
  <PageContainer>
    <section class="profile-head card">
      <el-avatar :size="72" :src="form.avatarUrl">
        {{ displayName.slice(0, 1) || 'U' }}
      </el-avatar>
      <div class="profile-title">
        <h1 class="page-title">{{ displayName }}</h1>
        <p class="muted">{{ form.email || '未填写邮箱' }}</p>
        <p class="muted">{{ form.school || '未填写学校' }}</p>
      </div>
      <el-upload
        :auto-upload="false"
        :show-file-list="false"
        accept="image/*"
        :on-change="handleAvatarChange"
      >
        <el-button :loading="uploading">更换头像</el-button>
      </el-upload>
    </section>

    <section class="profile-editor card">
      <div class="section-head">
        <h2>个人信息</h2>
        <el-button type="primary" :loading="saving" @click="saveProfile">保存</el-button>
      </div>

      <el-form label-position="top">
        <div class="form-grid">
          <el-form-item label="用户名">
            <el-input v-model="form.username" disabled />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="form.nickname" placeholder="请输入昵称" />
          </el-form-item>
        </div>

        <div class="form-grid">
          <el-form-item label="邮箱">
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
        </div>

        <div class="form-grid">
          <el-form-item label="学校">
            <el-input v-model="form.school" placeholder="请输入学校" />
          </el-form-item>
          <el-form-item label="专业">
            <el-input v-model="form.major" placeholder="请输入专业" />
          </el-form-item>
        </div>

        <div class="form-grid">
          <el-form-item label="年级">
            <el-input v-model="form.grade" placeholder="请输入年级" />
          </el-form-item>
          <el-form-item label="信用分">
            <el-input :model-value="form.creditScore ?? 100" disabled />
          </el-form-item>
        </div>

        <el-form-item label="个人简介">
          <el-input v-model="form.bio" type="textarea" :rows="4" placeholder="介绍一下自己" />
        </el-form-item>

        <div class="form-grid">
          <el-form-item label="兴趣">
            <el-select
              v-model="form.interests"
              multiple
              allow-create
              filterable
              default-first-option
              placeholder="输入兴趣后回车"
            />
          </el-form-item>
          <el-form-item label="标签">
            <el-select
              v-model="form.tags"
              multiple
              allow-create
              filterable
              default-first-option
              placeholder="输入标签后回车"
            />
          </el-form-item>
        </div>
      </el-form>
    </section>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, type UploadFile } from 'element-plus'

import { fetchProfile, updateProfile, uploadAvatar } from '@/api/user'
import PageContainer from '@/components/common/PageContainer.vue'
import { useAuthStore } from '@/stores/auth'
import type { UserProfile } from '@/types/user'

const authStore = useAuthStore()
const saving = ref(false)
const uploading = ref(false)

const form = reactive<UserProfile>({
  id: '',
  username: '',
  nickname: '',
  phone: '',
  email: '',
  avatarUrl: '',
  creditScore: 100,
  bio: '',
  school: '',
  major: '',
  grade: '',
  interests: [],
  tags: [],
})

const displayName = computed(() => form.nickname || form.username || 'CampusHub 用户')

onMounted(() => {
  loadProfile()
})

async function loadProfile() {
  try {
    const response = await fetchProfile()
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    applyProfile(response.data.data)
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载资料失败')
  }
}

async function saveProfile() {
  saving.value = true
  try {
    const response = await updateProfile(form)
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    applyProfile(response.data.data)
    ElMessage.success('资料已保存')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

async function handleAvatarChange(uploadFile: UploadFile) {
  if (!uploadFile.raw) {
    return
  }

  uploading.value = true
  try {
    const response = await uploadAvatar(uploadFile.raw)
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    form.avatarUrl = response.data.data.url
    await saveProfile()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '头像上传失败')
  } finally {
    uploading.value = false
  }
}

function applyProfile(profile: UserProfile) {
  Object.assign(form, {
    ...profile,
    interests: profile.interests || [],
    tags: profile.tags || [],
  })
  authStore.user = {
    ...profile,
    avatar: profile.avatarUrl,
    campus: profile.school,
  }
}
</script>

<style scoped>
.profile-head {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-lg);
}

.profile-title {
  flex: 1;
}

.profile-editor {
  padding: var(--spacing-lg);
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.section-head h2 {
  margin: 0;
  font-size: 18px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--spacing-md);
}

@media (max-width: 720px) {
  .profile-head {
    align-items: flex-start;
    flex-direction: column;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
