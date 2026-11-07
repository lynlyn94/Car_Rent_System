-- =============================================================
-- 汽车租赁管理系统 数据库结构
-- Database: car_rent
-- MySQL 8.x / 9.x
-- =============================================================

CREATE DATABASE IF NOT EXISTS car_rent
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE car_rent;

-- -------------------------------------------------------------
-- 管理员表 admin
-- -------------------------------------------------------------
DROP TABLE IF EXISTS admin;
CREATE TABLE admin (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  username    VARCHAR(50)  NOT NULL COMMENT '用户名，唯一',
  password    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
  name        VARCHAR(50)  DEFAULT NULL COMMENT '姓名',
  role        VARCHAR(20)  DEFAULT 'ADMIN' COMMENT '角色',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_admin_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '管理员表';

-- -------------------------------------------------------------
-- 用户表 user
-- -------------------------------------------------------------
DROP TABLE IF EXISTS user;
CREATE TABLE user (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  username    VARCHAR(50)  NOT NULL COMMENT '用户名，唯一',
  password    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
  name        VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
  gender      TINYINT      DEFAULT 1 COMMENT '性别（0 女 / 1 男）',
  birth_date  DATE         DEFAULT NULL COMMENT '出生日期',
  id_card     VARCHAR(18)  DEFAULT NULL COMMENT '身份证号',
  phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  drive_years INT          DEFAULT 0 COMMENT '驾驶年份（驾龄）',
  status      TINYINT      DEFAULT 1 COMMENT '账号状态（1 正常 / 0 禁用）',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- -------------------------------------------------------------
-- 车辆表 car
-- -------------------------------------------------------------
DROP TABLE IF EXISTS car;
CREATE TABLE car (
  id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  plate_no        VARCHAR(20)   NOT NULL COMMENT '车牌号（汽车号码）',
  brand           VARCHAR(50)   DEFAULT NULL COMMENT '品牌',
  car_type        VARCHAR(50)   DEFAULT NULL COMMENT '汽车类型',
  color           VARCHAR(20)   DEFAULT NULL COMMENT '颜色',
  seats           INT           DEFAULT 5 COMMENT '座位数',
  daily_rent      DECIMAL(10,2) DEFAULT 0 COMMENT '日租金',
  deposit         DECIMAL(10,2) DEFAULT 0 COMMENT '押金',
  maintain_status VARCHAR(50)   DEFAULT '正常' COMMENT '维护状态',
  purchase_date   DATE          DEFAULT NULL COMMENT '购入日期',
  rent_status     TINYINT       DEFAULT 0 COMMENT '出租状态（0 可租 / 1 已出租）',
  image           VARCHAR(500)  DEFAULT NULL COMMENT '车辆图片 URL',
  description     VARCHAR(1000) DEFAULT NULL COMMENT '车辆说明',
  create_time     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
  PRIMARY KEY (id),
  KEY idx_car_brand (brand),
  KEY idx_car_type (car_type),
  KEY idx_car_rent_status (rent_status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '车辆表';

-- -------------------------------------------------------------
-- 订单表 orders
-- -------------------------------------------------------------
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
  id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  order_no    VARCHAR(40)   NOT NULL COMMENT '订单号，唯一',
  user_id     BIGINT        NOT NULL COMMENT '用户ID',
  car_id      BIGINT        NOT NULL COMMENT '车辆ID',
  take_time   DATETIME      DEFAULT NULL COMMENT '取车时间',
  return_time DATETIME      DEFAULT NULL COMMENT '还车时间',
  take_addr   VARCHAR(200)  DEFAULT NULL COMMENT '取车地址',
  return_addr VARCHAR(200)  DEFAULT NULL COMMENT '还车地址',
  total_rent  DECIMAL(10,2) DEFAULT 0 COMMENT '租金总额',
  deposit     DECIMAL(10,2) DEFAULT 0 COMMENT '押金',
  status      TINYINT       DEFAULT 0 COMMENT '订单状态（0 待支付/1 租用中/2 已完成/3 已取消）',
  pay_type    VARCHAR(20)   DEFAULT NULL COMMENT '支付方式',
  create_time DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_orders_order_no (order_no),
  KEY idx_orders_user (user_id),
  KEY idx_orders_car (car_id),
  KEY idx_orders_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '订单表';

-- -------------------------------------------------------------
-- 留言表 message
-- -------------------------------------------------------------
DROP TABLE IF EXISTS message;
CREATE TABLE message (
  id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id     BIGINT        NOT NULL COMMENT '用户ID',
  content     VARCHAR(1000) DEFAULT NULL COMMENT '留言内容',
  create_time DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
  reply       VARCHAR(1000) DEFAULT NULL COMMENT '回复内容',
  reply_time  DATETIME      DEFAULT NULL COMMENT '回复时间',
  status      TINYINT       DEFAULT 0 COMMENT '状态（0 待回复 / 1 已回复）',
  PRIMARY KEY (id),
  KEY idx_message_user (user_id),
  KEY idx_message_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '留言表';
