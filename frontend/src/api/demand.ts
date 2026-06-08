import http from './http'

import type {
  ApiResult,
  Demand,
  DemandApplication,
  DemandCreatePayload,
  DemandQueryParams,
  PageResult,
} from '@/types/demand'

export function fetchDemands(params: DemandQueryParams = {}) {
  return http.get<ApiResult<PageResult<Demand>>>('/demands', { params })
}

export function fetchDemandById(id: string) {
  return http.get<ApiResult<Demand>>(`/demands/${id}`)
}

export function createDemand(payload: DemandCreatePayload) {
  return http.post<ApiResult<{ id: string }>>('/demands', payload)
}

export function uploadDemandImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return http.post<ApiResult<{ url: string }>>('/demands/images', formData)
}

export function respondDemand(id: string) {
  return http.post<ApiResult<Demand>>(`/demands/${id}/responses`)
}

export function fetchMyDemandApplication(id: string) {
  return http.get<ApiResult<DemandApplication | null>>(`/demands/${id}/applications/mine`)
}

export function fetchDemandApplications(id: string) {
  return http.get<ApiResult<DemandApplication[]>>(`/demands/${id}/applications`)
}

export function acceptDemandApplication(demandId: string, applicationId: string) {
  return http.post<ApiResult<Demand>>(`/demands/${demandId}/applications/${applicationId}/accept`)
}

export function rejectDemandApplication(demandId: string, applicationId: string) {
  return http.post<ApiResult<DemandApplication>>(`/demands/${demandId}/applications/${applicationId}/reject`)
}

export function endDemand(id: string) {
  return http.post<ApiResult<Demand>>(`/demands/${id}/end`)
}

export function cancelDemand(id: string) {
  return http.post<ApiResult<Demand>>(`/demands/${id}/cancel`)
}

export function fetchMyPublishedDemands() {
  return http.get<ApiResult<Demand[]>>('/demands/mine/published')
}

export function fetchMyAcceptedDemands() {
  return http.get<ApiResult<Demand[]>>('/demands/mine/accepted')
}

export function fetchMyFavoriteDemands() {
  return http.get<ApiResult<Demand[]>>('/demands/favorites')
}

export function favoriteDemand(id: string) {
  return http.post<ApiResult<null>>(`/demands/${id}/favorite`)
}

export function unfavoriteDemand(id: string) {
  return http.delete<ApiResult<null>>(`/demands/${id}/favorite`)
}
