package com.carrent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.carrent.entity.Car;
import com.carrent.entity.Order;
import com.carrent.mapper.CarMapper;
import com.carrent.mapper.OrderMapper;
import com.carrent.mapper.UserMapper;
import com.carrent.service.StatsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据统计服务实现
 */
@Service
public class StatsServiceImpl implements StatsService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM-dd");

    private final CarMapper carMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    public StatsServiceImpl(CarMapper carMapper, OrderMapper orderMapper, UserMapper userMapper) {
        this.carMapper = carMapper;
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Map<String, Object> dashboard() {
        Map<String, Object> map = new HashMap<>();

        // 车辆统计
        long totalCars = carMapper.selectCount(null);
        long availableCars = carMapper.selectCount(
                new LambdaQueryWrapper<Car>().eq(Car::getRentStatus, 0));
        map.put("totalCars", totalCars);
        map.put("availableCars", availableCars);
        map.put("rentedCars", totalCars - availableCars);

        // 客户统计
        map.put("totalUsers", userMapper.selectCount(null));

        // 订单统计
        long totalOrders = orderMapper.selectCount(null);
        long pendingOrders = orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 0));
        long rentingOrders = orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 1));
        long completedOrders = orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 2));
        long cancelledOrders = orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 3));
        map.put("totalOrders", totalOrders);
        map.put("pendingOrders", pendingOrders);
        map.put("rentingOrders", rentingOrders);
        map.put("completedOrders", completedOrders);
        map.put("cancelledOrders", cancelledOrders);

        // 营收（租用中 + 已完成 的租金总和）
        List<Order> paidOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>().in(Order::getStatus, 1, 2));
        BigDecimal revenue = paidOrders.stream()
                .map(Order::getTotalRent)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        map.put("revenue", revenue);

        // 近 7 日订单趋势
        map.put("orderTrend", buildOrderTrend());

        // 车辆品牌分布
        map.put("brandDistribution", buildBrandDistribution());

        // 热门车型 Top5
        map.put("hotModels", buildHotModels());

        return map;
    }

    private List<Map<String, Object>> buildOrderTrend() {
        LocalDate today = LocalDate.now();
        List<Order> recent = orderMapper.selectList(
                new LambdaQueryWrapper<Order>().ge(Order::getCreateTime, today.minusDays(6).atStartOfDay()));
        Map<LocalDate, Long> countByDate = recent.stream()
                .collect(Collectors.groupingBy(o -> o.getCreateTime().toLocalDate(), Collectors.counting()));
        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            Map<String, Object> item = new HashMap<>();
            item.put("date", d.format(DATE_FMT));
            item.put("count", countByDate.getOrDefault(d, 0L));
            trend.add(item);
        }
        return trend;
    }

    private List<Map<String, Object>> buildBrandDistribution() {
        List<Car> cars = carMapper.selectList(null);
        Map<String, Long> byBrand = cars.stream()
                .collect(Collectors.groupingBy(c -> c.getBrand() == null ? "未知" : c.getBrand(), Collectors.counting()));
        return byBrand.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", e.getKey());
                    item.put("value", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildHotModels() {
        List<Order> orders = orderMapper.selectList(null);
        List<Car> cars = carMapper.selectList(null);
        Map<Long, Car> carMap = cars.stream().collect(Collectors.toMap(Car::getId, c -> c));
        Map<Long, Long> countByCar = orders.stream()
                .collect(Collectors.groupingBy(Order::getCarId, Collectors.counting()));
        return countByCar.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    Car c = carMap.get(e.getKey());
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("name", c == null ? "已删除车辆" : (c.getBrand() + " " + c.getCarType()));
                    item.put("value", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }
}
