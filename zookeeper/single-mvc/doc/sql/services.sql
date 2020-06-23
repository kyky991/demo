/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50723
Source Host           : localhost:3306
Source Database       : services

Target Server Type    : MYSQL
Target Server Version : 50723
File Encoding         : 65001

Date: 2018-11-27 23:04:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for item
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
  `id` varchar(64) NOT NULL,
  `name` varchar(32) NOT NULL COMMENT '商品名称',
  `amount` int(6) NOT NULL COMMENT '商品价格',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` varchar(64) NOT NULL,
  `order_num` varchar(64) NOT NULL COMMENT '订单号',
  `item_id` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_orders_1` (`item_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
