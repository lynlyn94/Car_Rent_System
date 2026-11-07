package com.carrent.service;

import com.carrent.common.PageResult;
import com.carrent.entity.Car;

import java.math.BigDecimal;
import java.util.List;

/**
 * 车辆服务
 */
public interface CarService {

    PageResult<Car> pageCars(Integer current, Integer size, String brand, String carType,
                             Integer rentStatus, BigDecimal minRent, BigDecimal maxRent,
                             Integer seats, String keyword);

    Car detail(Long id);

    List<String> brands();

    List<String> carTypes();

    void add(Car car);

    void update(Car car);

    void delete(Long id);
}
