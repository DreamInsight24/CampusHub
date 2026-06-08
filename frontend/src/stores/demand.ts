import { defineStore } from 'pinia'

import type { Demand } from '@/types/demand'

interface DemandStoreState {
  selectedDemand: Demand | null
}

export const useDemandStore = defineStore('demand', {
  state: (): DemandStoreState => ({
    selectedDemand: null,
  }),
  actions: {
    selectDemand(demand: Demand) {
      this.selectedDemand = demand
    },
  },
})
