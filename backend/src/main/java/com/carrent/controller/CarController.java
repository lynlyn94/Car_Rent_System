package com.carrent.controller;

import com.carrent.common.PageResult;
import com.carrent.common.Result;
import com.carrent.entity.Car;
import com.carrent.service.CarService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * 车辆查询接口（公开）
 */
@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public Result<PageResult<Car>> page(
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

    @GetMapping("/brands")
    public Result<List<String>> brands() {
        return Result.success(carService.brands());
    }

    @GetMapping("/types")
    public Result<List<String>> types() {
        return Result.success(carService.carTypes());
    }

    @GetMapping("/{id}")
    public Result<Car> detail(@PathVariable Long id) {
        return Result.success(carService.detail(id));
    }
}
