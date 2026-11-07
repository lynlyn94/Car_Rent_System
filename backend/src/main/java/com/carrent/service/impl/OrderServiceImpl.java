package com.carrent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrent.common.BusinessException;
import com.carrent.common.PageResult;
import com.carrent.dto.OrderCreateRequest;
import com.carrent.entity.Car;
import com.carrent.entity.Order;
import com.carrent.entity.User;
import com.carrent.mapper.CarMapper;
import com.carrent.mapper.OrderMapper;
import com.carrent.mapper.UserMapper;
import com.carrent.service.OrderService;
import com.carrent.util.UserContext;
import com.carrent.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final DateTimeFormatter ORDER_NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrderMapper orderMapper;
    private final CarMapper carMapper;
    private final UserMapper userMapper;

    public OrderServiceImpl(OrderMapper orderMapper, CarMapper carMapper, UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.carMapper = carMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public OrderVO create(OrderCreateRequest req) {
        Long userId = UserContext.getUserId();
        Car car = carMapper.selectById(req.carId());
        if (car == null) {
            throw new BusinessException("车辆不存在");
        }
        if (req.takeTime() == null || req.returnTime() == null) {
            throw new BusinessException("请选择取车与还车时间");
        }
        if (!req.returnTime().isAfter(req.takeTime())) {
            throw new BusinessException("还车时间必须晚于取车时间");
        }
        if (car.getRentStatus() != null && car.getRentStatus() == 1) {
            throw new BusinessException("该车辆已被出租，请重新选择");
        }

        // 计算租期（不足一天按一天计）
        long minutes = Duration.between(req.takeTime(), req.returnTime()).toMinutes();
        long days = Math.max(1, (long) Math.ceil(minutes / (24.0 * 60)));
        BigDecimal totalRent = car.getDailyRent()
                .multiply(BigDecimal.valueOf(days))
                .setScale(2, RoundingMode.HALF_UP);

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setCarId(car.getId());
        order.setTakeTime(req.takeTime());
        order.setReturnTime(req.returnTime());
        order.setTakeAddr(req.takeAddr());
        order.setReturnAddr(req.returnAddr());
        order.setTotalRent(totalRent);
        order.setDeposit(car.getDeposit());
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);

        // 占用车辆，避免重复出租
        car.setRentStatus(1);
        carMapper.updateById(car);

        return toVO(order);
    }

    @Override
    public PageResult<OrderVO> myOrders(Integer current, Integer size, Integer status) {
        int cur = current == null || current < 1 ? 1 : current;
        int siz = size == null || size < 1 ? 10 : size;
        Page<Order> page = new Page<>(cur, siz);
        LambdaQueryWrapper<Order> qw = new LambdaQueryWrapper<>();
        qw.eq(Order::getUserId, UserContext.getUserId())
                .eq(status != null, Order::getStatus, status)
                .orderByDesc(Order::getId);
        Page<Order> result = orderMapper.selectPage(page, qw);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(),
                toVOList(result.getRecords()));
    }

    @Override
    public OrderVO detail(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(UserContext.getUserId())) {
            throw new BusinessException(403, "无权查看该订单");
        }
        return toVO(order);
    }

    @Override
    @Transactional
    public void pay(Long id, String payType) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(UserContext.getUserId())) {
            throw new BusinessException(403, "无权操作该订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("当前订单状态不支持支付");
        }
        order.setStatus(1);
        order.setPayType(StringUtils.hasText(payType) ? payType : "微信支付");
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(UserContext.getUserId())) {
            throw new BusinessException(403, "无权操作该订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("仅待支付订单可以取消");
        }
        order.setStatus(3);
        orderMapper.updateById(order);
        releaseCar(order.getCarId());
    }

    @Override
    public PageResult<OrderVO> adminPage(Integer current, Integer size, String orderNo, String customer,
                                         String plateNo, Integer status, String startDate, String endDate) {
        int cur = current == null || current < 1 ? 1 : current;
        int siz = size == null || size < 1 ? 10 : size;
        Page<Order> page = new Page<>(cur, siz);
        Page<Order> result = orderMapper.selectPage(page, buildAdminWrapper(orderNo, customer, plateNo, status, startDate, endDate));
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(),
                toVOList(result.getRecords()));
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (status == 2) {
            if (order.getStatus() != 1) {
                throw new BusinessException("仅租用中的订单可标记完成");
            }
            order.setStatus(2);
            releaseCar(order.getCarId());
        } else if (status == 3) {
            if (order.getStatus() == 2) {
                throw new BusinessException("已完成的订单不能取消");
            }
            if (order.getStatus() == 1) {
                releaseCar(order.getCarId());
            }
            order.setStatus(3);
        } else {
            throw new BusinessException("不支持的订单状态");
        }
        orderMapper.updateById(order);
    }

    @Override
    public List<OrderVO> ledger(String orderNo, String customer, String plateNo, Integer status,
                                String startDate, String endDate) {
        List<Order> orders = orderMapper.selectList(buildAdminWrapper(orderNo, customer, plateNo, status, startDate, endDate));
        return toVOList(orders);
    }

    /**
     * 组装管理端订单查询条件
     */
    private LambdaQueryWrapper<Order> buildAdminWrapper(String orderNo, String customer, String plateNo,
                                                        Integer status, String startDate, String endDate) {
        LambdaQueryWrapper<Order> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.hasText(orderNo), Order::getOrderNo, orderNo);

        if (StringUtils.hasText(plateNo)) {
            List<Long> carIds = carMapper.selectList(
                            new LambdaQueryWrapper<Car>().like(Car::getPlateNo, plateNo))
                    .stream().map(Car::getId).collect(Collectors.toList());
            qw.in(Order::getCarId, carIds.isEmpty() ? List.of(-1L) : carIds);
        }
        if (StringUtils.hasText(customer)) {
            List<Long> userIds = userMapper.selectList(
                            new LambdaQueryWrapper<User>()
                                    .and(w -> w.like(User::getUsername, customer).or().like(User::getName, customer)))
                    .stream().map(User::getId).collect(Collectors.toList());
            qw.in(Order::getUserId, userIds.isEmpty() ? List.of(-1L) : userIds);
        }

        qw.eq(status != null, Order::getStatus, status);
        if (StringUtils.hasText(startDate)) {
            qw.ge(Order::getCreateTime, LocalDate.parse(startDate).atStartOfDay());
        }
        if (StringUtils.hasText(endDate)) {
            qw.le(Order::getCreateTime, LocalDate.parse(endDate).atTime(LocalTime.MAX));
        }
        qw.orderByDesc(Order::getId);
        return qw;
    }

    private void releaseCar(Long carId) {
        Car car = carMapper.selectById(carId);
        if (car != null && car.getRentStatus() != null && car.getRentStatus() == 1) {
            car.setRentStatus(0);
            carMapper.updateById(car);
        }
    }

    private String generateOrderNo() {
        return "CR" + LocalDateTime.now().format(ORDER_NO_FORMAT)
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }

    private OrderVO toVO(Order order) {
        return toVOList(List.of(order)).get(0);
    }

    private List<OrderVO> toVOList(List<Order> orders) {
        List<OrderVO> vos = new ArrayList<>();
        if (orders.isEmpty()) {
            return vos;
        }
        List<Long> carIds = orders.stream().map(Order::getCarId).distinct().collect(Collectors.toList());
        List<Long> userIds = orders.stream().map(Order::getUserId).distinct().collect(Collectors.toList());
        Map<Long, Car> carMap = carMapper.selectBatchIds(carIds).stream()
                .collect(Collectors.toMap(Car::getId, Function.identity()));
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        for (Order o : orders) {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(o, vo);
            Car c = carMap.get(o.getCarId());
            if (c != null) {
                vo.setCarPlateNo(c.getPlateNo());
                vo.setCarBrand(c.getBrand());
                vo.setCarType(c.getCarType());
                vo.setCarImage(c.getImage());
            }
            User u = userMap.get(o.getUserId());
            if (u != null) {
                vo.setUserName(u.getName() != null ? u.getName() : u.getUsername());
            }
            vos.add(vo);
        }
        return vos;
    }
}
