/** 金额格式化 */
export function formatMoney(v?: number | string | null): string {
  if (v === null || v === undefined || v === '') return '0.00'
  return Number(v).toFixed(2)
}

/** 根据出生日期计算年龄 */
export function calcAge(birth?: string | null): string {
  if (!birth) return '-'
  const d = new Date(birth)
  if (isNaN(d.getTime())) return '-'
  const now = new Date()
  let age = now.getFullYear() - d.getFullYear()
  const m = now.getMonth() - d.getMonth()
  if (m < 0 || (m === 0 && now.getDate() < d.getDate())) age--
  return age >= 0 ? String(age) : '-'
}

export function genderText(g?: number | null): string {
  if (g === 0) return '女'
  if (g === 1) return '男'
  return '-'
}

/** 订单状态 */
export const ORDER_STATUS: Record<number, string> = {
  0: '待支付',
  1: '租用中',
  2: '已完成',
  3: '已取消'
}

export const ORDER_STATUS_TYPE: Record<number, string> = {
  0: 'warning',
  1: 'primary',
  2: 'success',
  3: 'info'
}

/** 出租状态 */
export const RENT_STATUS: Record<number, string> = {
  0: '可租',
  1: '已出租'
}

export function rentStatusText(s?: number | null): string {
  if (s === 0) return '可租'
  if (s === 1) return '已出租'
  return '-'
}

/** 将 datetime-local 的值（yyyy-MM-ddTHH:mm）转为后端格式（yyyy-MM-dd HH:mm:ss） */
export function toBackendDateTime(v: string): string {
  if (!v) return v
  return v.replace('T', ' ').length === 16 ? v.replace('T', ' ') + ':00' : v.replace('T', ' ')
}

/** 后端时间（yyyy-MM-dd HH:mm:ss）转为 datetime-local 值 */
export function toLocalInput(v?: string | null): string {
  if (!v) return ''
  return v.slice(0, 16).replace(' ', 'T')
}
