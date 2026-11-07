import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface SearchContext {
  takeAddr: string
  returnAddr: string
  takeTime: string
  returnTime: string
}

/** 保存首页搜索条件，供车辆列表与下单页复用 */
export const useSearchStore = defineStore('search', () => {
  const takeAddr = ref('')
  const returnAddr = ref('')
  const takeTime = ref('')
  const returnTime = ref('')

  function set(ctx: SearchContext) {
    takeAddr.value = ctx.takeAddr
    returnAddr.value = ctx.returnAddr
    takeTime.value = ctx.takeTime
    returnTime.value = ctx.returnTime
  }

  function clear() {
    takeAddr.value = ''
    returnAddr.value = ''
    takeTime.value = ''
    returnTime.value = ''
  }

  return { takeAddr, returnAddr, takeTime, returnTime, set, clear }
})
