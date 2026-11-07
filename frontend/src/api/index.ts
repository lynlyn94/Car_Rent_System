import request from './request'
import type { Car, LoginVO, Message, Order, PageResult, Result, User } from '../types'

// ---------------- 认证 ----------------
export function register(data: Record<string, unknown>): Promise<Result<LoginVO>> {
  return request.post('/auth/register', data)
}

export function login(data: Record<string, unknown>): Promise<Result<LoginVO>> {
  return request.post('/auth/login', data)
}

export function adminLogin(data: Record<string, unknown>): Promise<Result<LoginVO>> {
  return request.post('/auth/admin/login', data)
}

export function getMe(): Promise<Result<User>> {
  return request.get('/auth/me')
}

export function updateProfile(data: Record<string, unknown>): Promise<Result<void>> {
  return request.put('/auth/profile', data)
}

// ---------------- 车辆 ----------------
export function getCars(params: Record<string, unknown>): Promise<Result<PageResult<Car>>> {
  return request.get('/cars', { params })
}

export function getCarDetail(id: number): Promise<Result<Car>> {
  return request.get(`/cars/${id}`)
}

export function getBrands(): Promise<Result<string[]>> {
  return request.get('/cars/brands')
}

export function getCarTypes(): Promise<Result<string[]>> {
  return request.get('/cars/types')
}

// ---------------- 订单（用户） ----------------
export function createOrder(data: Record<string, unknown>): Promise<Result<Order>> {
  return request.post('/orders', data)
}

export function getMyOrders(params: Record<string, unknown>): Promise<Result<PageResult<Order>>> {
  return request.get('/orders/my', { params })
}

export function payOrder(id: number, payType: string): Promise<Result<void>> {
  return request.post(`/orders/${id}/pay`, null, { params: { payType } })
}

export function cancelOrder(id: number): Promise<Result<void>> {
  return request.post(`/orders/${id}/cancel`)
}

// ---------------- 留言 ----------------
export function submitMessage(content: string): Promise<Result<void>> {
  return request.post('/messages', { content })
}

export function getMyMessages(params: Record<string, unknown>): Promise<Result<PageResult<Message>>> {
  return request.get('/messages/my', { params })
}

// ---------------- 管理端 ----------------
export function adminGetCars(params: Record<string, unknown>): Promise<Result<PageResult<Car>>> {
  return request.get('/admin/cars', { params })
}

export function adminAddCar(data: Record<string, unknown>): Promise<Result<void>> {
  return request.post('/admin/cars', data)
}

export function adminUpdateCar(id: number, data: Record<string, unknown>): Promise<Result<void>> {
  return request.put(`/admin/cars/${id}`, data)
}

export function adminDeleteCar(id: number): Promise<Result<void>> {
  return request.delete(`/admin/cars/${id}`)
}

export function adminGetCustomers(params: Record<string, unknown>): Promise<Result<PageResult<User>>> {
  return request.get('/admin/customers', { params })
}

export function adminAddCustomer(data: Record<string, unknown>): Promise<Result<void>> {
  return request.post('/admin/customers', data)
}

export function adminUpdateCustomer(id: number, data: Record<string, unknown>): Promise<Result<void>> {
  return request.put(`/admin/customers/${id}`, data)
}

export function adminDeleteCustomer(id: number): Promise<Result<void>> {
  return request.delete(`/admin/customers/${id}`)
}

export function adminGetOrders(params: Record<string, unknown>): Promise<Result<PageResult<Order>>> {
  return request.get('/admin/orders', { params })
}

export function adminUpdateOrderStatus(id: number, status: number): Promise<Result<void>> {
  return request.put(`/admin/orders/${id}/status`, null, { params: { status } })
}

export function adminGetLedger(params: Record<string, unknown>): Promise<Result<Order[]>> {
  return request.get('/admin/orders/ledger', { params })
}

export function adminGetMessages(params: Record<string, unknown>): Promise<Result<PageResult<Message>>> {
  return request.get('/admin/messages', { params })
}

export function adminReplyMessage(id: number, reply: string): Promise<Result<void>> {
  return request.post(`/admin/messages/${id}/reply`, { reply })
}

export function adminGetStats(): Promise<Result<Record<string, unknown>>> {
  return request.get('/admin/stats')
}

export function uploadImage(file: File): Promise<Result<string>> {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/admin/upload', formData)
}
