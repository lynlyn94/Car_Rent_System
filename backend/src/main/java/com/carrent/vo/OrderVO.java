package com.carrent.vo;

import com.carrent.entity.Order;

/**
 * 订单视图对象：在订单基础上补充车辆与用户信息
 */
public class OrderVO extends Order {

    private String carPlateNo;
    private String carBrand;
    private String carType;
    private String carImage;
    private String userName;

    public String getCarPlateNo() {
        return carPlateNo;
    }

    public void setCarPlateNo(String carPlateNo) {
        this.carPlateNo = carPlateNo;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
