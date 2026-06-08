<template>
  <el-tag :type="tagType" effect="light" round>
    {{ label }}
  </el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

import type { DemandStatus } from '@/types/demand'

const props = defineProps<{
  status: DemandStatus
}>()

const labelMap: Record<DemandStatus, string> = {
  OPEN: '开放中',
  IN_PROGRESS: '进行中',
  COMPLETED: '已完成',
  CLOSED: '已结束',
  CANCELLED: '已取消',
  NO: '未开始',
  YES: '进行中',
  DONE: '已完成',
  DEL: '已删除',
}

const typeMap: Record<DemandStatus, 'success' | 'warning' | 'info' | 'danger'> = {
  OPEN: 'success',
  IN_PROGRESS: 'warning',
  COMPLETED: 'info',
  CLOSED: 'info',
  CANCELLED: 'danger',
  NO: 'success',
  YES: 'warning',
  DONE: 'info',
  DEL: 'danger',
}

const label = computed(() => labelMap[props.status])
const tagType = computed(() => typeMap[props.status])
</script>
