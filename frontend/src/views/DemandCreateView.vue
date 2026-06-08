<template>
  <PageContainer>
    <section class="create-panel card">
      <h1 class="page-title">发布需求</h1>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item label="需求类型" prop="type">
            <el-select v-model="form.type" placeholder="请选择需求类型">
              <el-option label="快递代取" value="EXPRESS" />
              <el-option label="二手交易" value="SECONDHAND" />
              <el-option label="学习辅导" value="TUTORING" />
              <el-option label="组队匹配" value="TEAMUP" />
            </el-select>
          </el-form-item>
          <el-form-item label="截止时间">
            <el-date-picker
              v-model="form.deadline"
              type="datetime"
              value-format="YYYY-MM-DD HH:mm"
              placeholder="请选择截止时间"
            />
          </el-form-item>
        </div>

        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="用一句话描述你的需求" />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="补充时间、物品、要求等关键信息"
          />
        </el-form-item>

        <el-form-item label="地点" prop="location">
          <el-input v-model="form.location" placeholder="例如：东门快递柜 / 图书馆北门" />
        </el-form-item>

        <section class="type-fields">
          <template v-if="form.type === 'EXPRESS'">
            <div class="form-grid">
              <el-form-item label="取件地点" prop="pickupLocation">
                <el-input v-model="form.pickupLocation" placeholder="例如：东门快递柜" />
              </el-form-item>
              <el-form-item label="送达地点" prop="deliveryLocation">
                <el-input v-model="form.deliveryLocation" placeholder="例如：6 号宿舍楼下" />
              </el-form-item>
            </div>
            <div class="form-grid">
              <el-form-item label="取件码">
                <el-input v-model="form.pickupCode" placeholder="可填写“私聊发送”" />
              </el-form-item>
              <el-form-item label="期望送达时间">
                <el-date-picker
                  v-model="form.expectedDeliveryTime"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm"
                  placeholder="请选择时间"
                />
              </el-form-item>
            </div>
          </template>

          <template v-else-if="form.type === 'SECONDHAND'">
            <div class="form-grid">
              <el-form-item label="物品名称" prop="itemName">
                <el-input v-model="form.itemName" placeholder="例如：无线耳机" />
              </el-form-item>
              <el-form-item label="分类">
                <el-input v-model="form.category" placeholder="例如：数码 / 教材" />
              </el-form-item>
            </div>
            <div class="form-grid">
              <el-form-item label="价格">
                <el-input-number v-model="form.price" :min="0" :precision="2" />
              </el-form-item>
              <el-form-item label="原价">
                <el-input-number v-model="form.originalPrice" :min="0" :precision="2" />
              </el-form-item>
            </div>
            <div class="form-grid">
              <el-form-item label="成色">
                <el-select v-model="form.conditionLevel" clearable placeholder="请选择">
                  <el-option label="全新" value="全新" />
                  <el-option label="九成新" value="九成新" />
                  <el-option label="八成新" value="八成新" />
                  <el-option label="正常使用痕迹" value="正常使用痕迹" />
                </el-select>
              </el-form-item>
              <el-form-item label="交易地点">
                <el-input v-model="form.tradeLocation" placeholder="例如：图书馆北门" />
              </el-form-item>
            </div>
          </template>

          <template v-else-if="form.type === 'TUTORING'">
            <div class="form-grid">
              <el-form-item label="科目" prop="subject">
                <el-input v-model="form.subject" placeholder="例如：高等数学" />
              </el-form-item>
              <el-form-item label="辅导方式">
                <el-select v-model="form.tutoringMode" clearable placeholder="请选择">
                  <el-option label="线下" value="线下" />
                  <el-option label="线上" value="线上" />
                  <el-option label="均可" value="均可" />
                </el-select>
              </el-form-item>
            </div>
            <div class="form-grid">
              <el-form-item label="期望辅导时间">
                <el-date-picker
                  v-model="form.expectedTime"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm"
                  placeholder="请选择时间"
                />
              </el-form-item>
              <el-form-item label="时长（分钟）">
                <el-input-number v-model="form.duration" :min="30" :step="30" />
              </el-form-item>
            </div>
            <el-form-item label="水平要求">
              <el-input v-model="form.levelRequirement" placeholder="例如：能讲清极限和导数" />
            </el-form-item>
          </template>

          <template v-else>
            <el-form-item label="组队目标" prop="teamGoal">
              <el-input v-model="form.teamGoal" placeholder="例如：数学建模练习" />
            </el-form-item>
            <div class="form-grid">
              <el-form-item label="当前人数">
                <el-input-number v-model="form.currentMembers" :min="0" />
              </el-form-item>
              <el-form-item label="目标人数">
                <el-input-number v-model="form.expectedMembers" :min="1" />
              </el-form-item>
            </div>
            <div class="form-grid">
              <el-form-item label="所需技能">
                <el-select
                  v-model="form.requiredSkills"
                  multiple
                  allow-create
                  filterable
                  default-first-option
                  placeholder="输入技能后回车"
                />
              </el-form-item>
              <el-form-item label="联系方式">
                <el-input v-model="form.contactMethod" placeholder="例如：站内消息 / 微信私聊" />
              </el-form-item>
            </div>
          </template>
        </section>

        <el-form-item label="图片">
          <el-upload
            :auto-upload="false"
            :show-file-list="false"
            accept="image/*"
            :on-change="handleImageChange"
          >
            <el-button :loading="uploading">上传图片</el-button>
          </el-upload>
          <div v-if="form.imageUrls?.length" class="image-list">
            <el-tag v-for="url in form.imageUrls" :key="url" closable @close="removeImage(url)">
              {{ url }}
            </el-tag>
          </div>
        </el-form-item>

        <div class="form-actions">
          <el-button @click="router.back()">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
        </div>
      </el-form>
    </section>
  </PageContainer>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules, type UploadFile } from 'element-plus'

import { createDemand, uploadDemandImage } from '@/api/demand'
import PageContainer from '@/components/common/PageContainer.vue'
import type { DemandCreatePayload } from '@/types/demand'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)
const uploading = ref(false)

const form = reactive<DemandCreatePayload>({
  type: 'EXPRESS',
  title: '',
  description: '',
  location: '',
  deadline: '',
  imageUrls: [],
  pickupLocation: '',
  deliveryLocation: '',
  pickupCode: '',
  expectedDeliveryTime: '',
  itemName: '',
  category: '',
  price: null,
  originalPrice: null,
  conditionLevel: '',
  tradeLocation: '',
  subject: '',
  tutoringMode: '',
  expectedTime: '',
  duration: null,
  levelRequirement: '',
  teamGoal: '',
  currentMembers: null,
  expectedMembers: null,
  requiredSkills: [],
  contactMethod: '',
})

const rules: FormRules<DemandCreatePayload> = {
  type: [{ required: true, message: '请选择需求类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
  location: [{ required: true, message: '请输入地点', trigger: 'blur' }],
}

async function handleImageChange(uploadFile: UploadFile) {
  if (!uploadFile.raw) {
    return
  }

  uploading.value = true
  try {
    const response = await uploadDemandImage(uploadFile.raw)
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    form.imageUrls?.push(response.data.data.url)
    ElMessage.success('图片接口已返回占位地址')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '图片上传失败')
  } finally {
    uploading.value = false
  }
}

function removeImage(url: string) {
  form.imageUrls = form.imageUrls?.filter((item) => item !== url)
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitting.value = true

  try {
    const response = await createDemand(form)
    if (response.data.code !== 200) {
      throw new Error(response.data.message)
    }
    ElMessage.success('需求已发布')
    router.push('/demands')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '发布失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.create-panel {
  max-width: 860px;
  margin: 0 auto;
  padding: var(--spacing-lg);
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--spacing-md);
}

.type-fields {
  margin-top: var(--spacing-md);
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-left: var(--spacing-md);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
