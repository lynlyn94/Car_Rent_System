package com.carrent.controller;

import com.carrent.common.PageResult;
import com.carrent.common.Result;
import com.carrent.dto.OrderCreateRequest;
import com.carrent.service.OrderService;
import com.carrent.vo.OrderVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户订单接口
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<OrderVO> create(@Valid @RequestBody OrderCreateRequest req) {
        return Result.success("下单成功", orderService.create(req));
    }

    @GetMapping("/my")
    public Result<PageResult<OrderVO>> myOrders(
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer status) {
        return Result.success(orderService.myOrders(current, size, status));
    }

    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        return Result.success(orderService.detail(id));
    }

    @PostMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id,
                            @RequestParam(required = false, defaultValue = "微信支付") String payType) {
        orderService.pay(id, payType);
        return Result.success("支付成功", null);
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancel(id);
        return Result.success("订单已取消", null);
    }
}
