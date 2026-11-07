package com.carrent.controller;

import com.carrent.common.PageResult;
import com.carrent.common.Result;
import com.carrent.dto.ReplyRequest;
import com.carrent.dto.UserForm;
import com.carrent.entity.Car;
import com.carrent.entity.User;
import com.carrent.service.CarService;
import com.carrent.service.MessageService;
import com.carrent.service.OrderService;
import com.carrent.service.StatsService;
import com.carrent.service.UserService;
import com.carrent.vo.MessageVO;
import com.carrent.vo.OrderVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 管理端接口（均需管理员权限）
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CarService carService;
    private final UserService userService;
    private final OrderService orderService;
    private final MessageService messageService;
    private final StatsService statsService;

    public AdminController(CarService carService, UserService userService, OrderService orderService,
                           MessageService messageService, StatsService statsService) {
        this.carService = carService;
        this.userService = userService;
        this.orderService = orderService;
        this.messageService = messageService;
        this.statsService = statsService;
    }

    // ---------------- 车辆管理 ----------------

    @GetMapping("/cars")
    public Result<PageResult<Car>> carPage(
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String carType,
            @RequestParam(required = false) Integer rentStatus,
            @RequestParam(required = false) BigDecimal minRent,
            @RequestParam(required = false) BigDecimal maxRent,
            @RequestParam(required = false) Integer seats,
            @RequestParam(required = false) String keyword) {
        return Result.success(carService.pageCars(current, size, brand, carType, rentStatus, minRent, maxRent, seats, keyword));
    }

    @PostMapping("/cars")
    public Result<Void> addCar(@RequestBody Car car) {
        carService.add(car);
        return Result.success("车辆添加成功", null);
    }

    @PutMapping("/cars/{id}")
    public Result<Void> updateCar(@PathVariable Long id, @RequestBody Car car) {
        car.setId(id);
        carService.update(car);
        return Result.success("车辆修改成功", null);
    }

    @DeleteMapping("/cars/{id}")
    public Result<Void> deleteCar(@PathVariable Long id) {
        carService.delete(id);
        return Result.success("车辆删除成功", null);
    }

    // ---------------- 客户管理 ----------------

    @GetMapping("/customers")
    public Result<PageResult<User>> customerPage(
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer gender) {
        return Result.success(userService.adminPage(current, size, keyword, gender));
    }

    @PostMapping("/customers")
    public Result<Void> addCustomer(@Valid @RequestBody UserForm form) {
        userService.add(form);
        return Result.success("客户添加成功", null);
    }

    @PutMapping("/customers/{id}")
    public Result<Void> updateCustomer(@PathVariable Long id, @Valid @RequestBody UserForm form) {
        userService.update(id, form);
        return Result.success("客户修改成功", null);
    }

    @DeleteMapping("/customers/{id}")
    public Result<Void> deleteCustomer(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("客户删除成功", null);
    }

    // ---------------- 订单管理与台账 ----------------

    @GetMapping("/orders")
    public Result<PageResult<OrderVO>> orderPage(
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String customer,
            @RequestParam(required = false) String plateNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(orderService.adminPage(current, size, orderNo, customer, plateNo, status, startDate, endDate));
    }

    @GetMapping("/orders/ledger")
    public Result<List<OrderVO>> ledger(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String customer,
            @RequestParam(required = false) String plateNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(orderService.ledger(orderNo, customer, plateNo, status, startDate, endDate));
    }

    @PutMapping("/orders/{id}/status")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestParam Integer status) {
        orderService.updateStatus(id, status);
        return Result.success("订单状态更新成功", null);
    }

    // ---------------- 留言管理 ----------------

    @GetMapping("/messages")
    public Result<PageResult<MessageVO>> messagePage(
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer status) {
        return Result.success(messageService.adminPage(current, size, status));
    }

    @PostMapping("/messages/{id}/reply")
    public Result<Void> reply(@PathVariable Long id, @Valid @RequestBody ReplyRequest req) {
        messageService.reply(id, req.reply());
        return Result.success("回复成功", null);
    }

    // ---------------- 数据统计 ----------------

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        return Result.success(statsService.dashboard());
    }
}
