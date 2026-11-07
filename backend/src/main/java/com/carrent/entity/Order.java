package com.carrent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private Long carId;

    private LocalDateTime takeTime;

    private LocalDateTime returnTime;

    private String takeAddr;

    private String returnAddr;

    private BigDecimal totalRent;

    private BigDecimal deposit;

    /** 订单状态：0 待支付 / 1 租用中 / 2 已完成 / 3 已取消 */
    private Integer status;

    private String payType;

    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public LocalDateTime getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(LocalDateTime takeTime) {
        this.takeTime = takeTime;
    }

    public LocalDateTime getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(LocalDateTime returnTime) {
        this.returnTime = returnTime;
    }

    public String getTakeAddr() {
        return takeAddr;
    }

    public void setTakeAddr(String takeAddr) {
        this.takeAddr = takeAddr;
    }

    public String getReturnAddr() {
        return returnAddr;
    }

    public void setReturnAddr(String returnAddr) {
        this.returnAddr = returnAddr;
    }

    public BigDecimal getTotalRent() {
        return totalRent;
    }

    public void setTotalRent(BigDecimal totalRent) {
        this.totalRent = totalRent;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
