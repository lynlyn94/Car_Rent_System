export interface Result<T> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  total: number
  current: number
  size: number
  records: T[]
}

export interface Car {
  id: number
  plateNo: string
  brand: string
  carType: string
  color: string
  seats: number
  dailyRent: number
  deposit: number
  maintainStatus: string
  purchaseDate: string
  rentStatus: number
  image: string
  description: string
  createTime: string
}

export interface User {
  id: number
  username: string
  name: string
  gender: number
  birthDate: string
  idCard: string
  phone: string
  driveYears: number
  status: number
  createTime: string
}

export interface Order {
  id: number
  orderNo: string
  userId: number
  carId: number
  takeTime: string
  returnTime: string
  takeAddr: string
  returnAddr: string
  totalRent: number
  deposit: number
  status: number
  payType: string
  createTime: string
  carPlateNo?: string
  carBrand?: string
  carType?: string
  carImage?: string
  userName?: string
}

export interface Message {
  id: number
  userId: number
  content: string
  createTime: string
  reply: string
  replyTime: string
  status: number
  userName?: string
}

export interface LoginVO {
  token: string
  id: number
  username: string
  name: string
  role: string
}
