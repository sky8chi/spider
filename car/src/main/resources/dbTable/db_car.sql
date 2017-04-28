/*
SQLyog Ultimate v12.2.6 (64 bit)
MySQL - 5.7.18-log : Database - car
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`car` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `car`;

/*Table structure for table `360che_brand` */

DROP TABLE IF EXISTS `360che_brand`;

CREATE TABLE `360che_brand` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `url` varchar(1024) DEFAULT NULL COMMENT '链接',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 COMMENT='360che品牌表';

/*Table structure for table `360che_gearbox` */

DROP TABLE IF EXISTS `360che_gearbox`;

CREATE TABLE `360che_gearbox` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `brand_id` int(20) NOT NULL COMMENT '品牌id',
  `model` varchar(100) NOT NULL COMMENT '型号',
  `serie` varchar(100) DEFAULT NULL COMMENT '系列',
  `form` varchar(100) DEFAULT NULL COMMENT '换挡形式',
  `status` tinyint(4) DEFAULT '1' COMMENT '1.在售2.停售3.未上市',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=859 DEFAULT CHARSET=utf8 COMMENT='360che变速箱';

/*Table structure for table `bitauto_brand` */

DROP TABLE IF EXISTS `bitauto_brand`;

CREATE TABLE `bitauto_brand` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `org_id` varchar(20) DEFAULT NULL COMMENT '原始id',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `url` varchar(1024) DEFAULT NULL COMMENT '链接',
  `alpha` varchar(4) DEFAULT NULL COMMENT '首字母',
  `parent_id` int(20) NOT NULL DEFAULT '0' COMMENT '父id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=367 DEFAULT CHARSET=utf8 COMMENT='titauto品牌表';

/*Table structure for table `bitauto_car_model` */

DROP TABLE IF EXISTS `bitauto_car_model`;

CREATE TABLE `bitauto_car_model` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `car_style_id` int(20) NOT NULL COMMENT '车型id',
  `name` varchar(100) NOT NULL COMMENT '款项',
  `year` varchar(1024) DEFAULT NULL COMMENT '年款',
  `displacement` varchar(1024) DEFAULT NULL COMMENT '排量',
  `gearbox` varchar(1024) DEFAULT NULL COMMENT '变速箱',
  `price` decimal(10,0) DEFAULT NULL COMMENT '价格',
  `status` tinyint(4) DEFAULT '1' COMMENT '1在售0停售',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `IDX_carstyleid` (`car_style_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20072 DEFAULT CHARSET=utf8 COMMENT='titauto车款';

/*Table structure for table `bitauto_car_style` */

DROP TABLE IF EXISTS `bitauto_car_style`;

CREATE TABLE `bitauto_car_style` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `url` varchar(1024) DEFAULT NULL COMMENT '链接',
  `brand_id` int(20) NOT NULL COMMENT '品牌id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_NAME` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=9523 DEFAULT CHARSET=utf8 COMMENT='titauto车型';

/*Table structure for table `mot_batch` */

DROP TABLE IF EXISTS `mot_batch`;

CREATE TABLE `mot_batch` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `url` varchar(1024) NOT NULL COMMENT '链接',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_url` (`url`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8 COMMENT='mot批次表';

/*Table structure for table `mot_model` */

DROP TABLE IF EXISTS `mot_model`;

CREATE TABLE `mot_model` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `batch_id` int(20) NOT NULL COMMENT '批次id',
  `no` varchar(100) NOT NULL COMMENT '型号',
  `name` varchar(100) NOT NULL COMMENT '车辆名称',
  `brand` varchar(100) DEFAULT NULL COMMENT '品牌',
  `maker` varchar(200) DEFAULT NULL COMMENT '制造商',
  `detail_url` varchar(1024) DEFAULT NULL COMMENT '详情url',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `IDX_batchid` (`batch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=70976 DEFAULT CHARSET=utf8 COMMENT='mot型号';

/*Table structure for table `mot_model_param` */

DROP TABLE IF EXISTS `mot_model_param`;

CREATE TABLE `mot_model_param` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `model_id` int(20) NOT NULL COMMENT '型号id',
  `chassisCompany` varchar(200) DEFAULT NULL COMMENT '底盘企业',
  `chassisNo` varchar(200) DEFAULT NULL COMMENT '底盘型号',
  `engineCompany` varchar(200) DEFAULT NULL COMMENT '改动机企业',
  `engineNo` varchar(200) DEFAULT NULL COMMENT '改动机型号',
  `transmissionNo` varchar(200) DEFAULT NULL COMMENT '变速器型号',
  `mainReducerRate` varchar(100) DEFAULT NULL COMMENT '主减速器速比',
  `url` varchar(1024) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `IDX_modelid` (`model_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3121 DEFAULT CHARSET=utf8 COMMENT='mot型号参数';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
