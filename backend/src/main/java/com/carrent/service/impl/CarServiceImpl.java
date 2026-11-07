package com.carrent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrent.common.BusinessException;
import com.carrent.common.PageResult;
import com.carrent.entity.Car;
import com.carrent.mapper.CarMapper;
import com.carrent.service.CarService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 车辆服务实现
 */
@Service
public class CarServiceImpl implements CarService {

    private final CarMapper carMapper;

    public CarServiceImpl(CarMapper carMapper) {
        this.carMapper = carMapper;
    }

    @Override
    public PageResult<Car> pageCars(Integer current, Integer size, String brand, String carType,
                                    Integer rentStatus, BigDecimal minRent, BigDecimal maxRent,
                                    Integer seats, String keyword) {
        int cur = current == null || current < 1 ? 1 : current;
        int siz = size == null || size < 1 ? 8 : size;
        Page<Car> page = new Page<>(cur, siz);
        LambdaQueryWrapper<Car> qw = new LambdaQueryWrapper<>();
        qw.eq(StringUtils.hasText(brand), Car::getBrand, brand)
                .eq(StringUtils.hasText(carType), Car::getCarType, carType)
                .eq(rentStatus != null, Car::getRentStatus, rentStatus)
                .ge(minRent != null, Car::getDailyRent, minRent)
                .le(maxRent != null, Car::getDailyRent, maxRent)
                .eq(seats != null, Car::getSeats, seats)
                .and(StringUtils.hasText(keyword), w -> w.like(Car::getPlateNo, keyword)
                        .or().like(Car::getBrand, keyword)
                        .or().like(Car::getCarType, keyword))
                .orderByDesc(Car::getId);
        Page<Car> result = carMapper.selectPage(page, qw);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public Car detail(Long id) {
        Car car = carMapper.selectById(id);
        if (car == null) {
            throw new BusinessException("车辆不存在");
        }
        return car;
    }

    @Override
    public List<String> brands() {
        QueryWrapper<Car> qw = new QueryWrapper<>();
        qw.select("DISTINCT brand").isNotNull("brand").orderByAsc("brand");
        return carMapper.selectObjs(qw).stream().map(Object::toString).collect(Collectors.toList());
    }

    @Override
    public List<String> carTypes() {
        QueryWrapper<Car> qw = new QueryWrapper<>();
        qw.select("DISTINCT car_type").isNotNull("car_type").orderByAsc("car_type");
        return carMapper.selectObjs(qw).stream().map(Object::toString).collect(Collectors.toList());
    }

    @Override
    public void add(Car car) {
        if (!StringUtils.hasText(car.getPlateNo())) {
            throw new BusinessException("车牌号不能为空");
        }
        if (car.getRentStatus() == null) {
            car.setRentStatus(0);
        }
        if (car.getDailyRent() == null) {
            car.setDailyRent(BigDecimal.ZERO);
        }
        if (car.getDeposit() == null) {
            car.setDeposit(BigDecimal.ZERO);
        }
        if (car.getCreateTime() == null) {
            car.setCreateTime(LocalDateTime.now());
        }
        carMapper.insert(car);
    }

    @Override
    public void update(Car car) {
        if (car.getId() == null) {
            throw new BusinessException("车辆 ID 不能为空");
        }
        Car exist = carMapper.selectById(car.getId());
        if (exist == null) {
            throw new BusinessException("车辆不存在");
        }
        carMapper.updateById(car);
    }

    @Override
    public void delete(Long id) {
        Car car = carMapper.selectById(id);
        if (car == null) {
            throw new BusinessException("车辆不存在");
        }
        if (car.getRentStatus() != null && car.getRentStatus() == 1) {
            throw new BusinessException("该车辆正在出租，无法删除");
        }
        carMapper.deleteById(id);
    }
}
