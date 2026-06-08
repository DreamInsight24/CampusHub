export type DemandType = 'EXPRESS' | 'SECONDHAND' | 'TUTORING' | 'TEAM' | 'TEAMUP' | 'TYPE1' | 'TYPE2'

export type DemandStatus =
  | 'OPEN'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'CLOSED'
  | 'CANCELLED'
  | 'NO'
  | 'YES'
  | 'DONE'
  | 'DEL'

export type DemandApplicationStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'EXPIRED'

export interface DemandApplication {
  id: string
  demandId: string
  applicantId: string
  applicantName: string
  applicantAvatar?: string
  statement?: string
  status: DemandApplicationStatus
  appliedAt: string
  updatedAt?: string
}

export interface Demand {
  id: string
  title: string
  type: DemandType
  description: string
  location: string
  reward?: number | null
  status: DemandStatus
  publisherId?: string
  publisherName: string
  publisherAvatar?: string
  takerId?: string | null
  createdAt: string
  updatedAt?: string
  deadline?: string
  responseCount?: number
  imageUrls?: string[]
  pickupLocation?: string
  deliveryLocation?: string
  pickupCode?: string
  expectedDeliveryTime?: string
  itemName?: string
  category?: string
  price?: number | null
  originalPrice?: number | null
  conditionLevel?: string
  tradeLocation?: string
  subject?: string
  tutoringMode?: string
  expectedTime?: string
  duration?: number | null
  levelRequirement?: string
  teamGoal?: string
  currentMembers?: number | null
  expectedMembers?: number | null
  requiredSkills?: string[]
  contactMethod?: string
}

export interface DemandCreatePayload {
  type: DemandType
  title: string
  description: string
  location: string
  deadline?: string
  imageUrls?: string[]
  pickupLocation?: string
  deliveryLocation?: string
  pickupCode?: string
  expectedDeliveryTime?: string
  itemName?: string
  category?: string
  price?: number | null
  originalPrice?: number | null
  conditionLevel?: string
  tradeLocation?: string
  subject?: string
  tutoringMode?: string
  expectedTime?: string
  duration?: number | null
  levelRequirement?: string
  teamGoal?: string
  currentMembers?: number | null
  expectedMembers?: number | null
  requiredSkills?: string[]
  contactMethod?: string
}

export interface DemandQueryParams {
  keyword?: string
  type?: DemandType
  status?: DemandStatus
  sort?: 'latest' | 'deadline'
  page?: number
  pageSize?: number
}

export interface PageResult<T> {
  items: T[]
  total: number
  page: number
  pageSize: number
}

export interface ApiResult<T> {
  code: number
  message: string
  data: T
}
