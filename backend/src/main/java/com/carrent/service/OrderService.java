package com.carrent.service;

import com.carrent.common.PageResult;
import com.carrent.dto.OrderCreateRequest;
import com.carrent.vo.OrderVO;

import java.util.List;

/**
 * 订单服务
 */
public interface OrderService {

    OrderVO create(OrderCreateRequest req);

    PageResult<OrderVO> myOrders(Integer current, Integer size, Integer status);

    OrderVO detail(Long id);

    void pay(Long id, String payType);

    void cancel(Long id);

    PageResult<OrderVO> adminPage(Integer current, Integer size, String orderNo, String customer,
                                  String plateNo, Integer status, String startDate, String endDate);

    void updateStatus(Long id, Integer status);

    List<OrderVO> ledger(String orderNo, String customer, String plateNo, Integer status,
                         String startDate, String endDate);
}
