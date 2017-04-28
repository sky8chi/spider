package com.skyzd.spider.car.shops.bitauto.db;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.skyzd.spider.car.shops.bitauto.beans.Brand;
import com.skyzd.spider.car.shops.bitauto.beans.DbBrand;
import com.skyzd.spider.car.shops.bitauto.beans.StyleInfo;
import com.skyzd.spider.car.shops.bitauto.beans.Turbocharg;
import com.skyzd.spider.car.shops.bitauto.grab.BitautoGrab;
import com.skyzd.spider.dao.db.JdbcDaoSupport;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by sky.chi on 4/27/2017 1:10 AM.
 * Email: sky8chi@gmail.com
 */
public class BitautoDb {
    private static Logger logger = LoggerFactory.getLogger(BitautoDb.class);
    private static BitautoGrab grab = new BitautoGrab();
    public static void main(String[] args) {
//        long lastRunTime = 1493274182* 1000L;
//        Date startDate = new Date(lastRunTime);
        Date startDate = new Date();
        logger.info("start bitauto time: " + startDate.getTime());
        saveAllBrand();
        getAllCarStyle(startDate).forEach(carStyle -> saveCarStyle(carStyle));
        logger.info("Bitauto run end !!!!!!!!!!!");
    }

    private static void saveCarStyle(DbBrand carStyle){
        List<Turbocharg> turbochargs = grab.getCarInfo(carStyle.getUrl());
        turbochargs.forEach(turbocharg ->
            turbocharg.getCars().forEach(styleInfo -> saveCarModel(carStyle.getDbId(), turbocharg, styleInfo))
        );
        logger.info("car style end: " + carStyle.getName() + "; url: " + carStyle.getUrl()
        + "; dbid: " + carStyle.getDbId());
    }

    private static void saveCarModel(long carStyleId, Turbocharg turbocharg, StyleInfo styleInfo) {
        String sql = "INSERT INTO `car`.`bitauto_car_model` ( `car_style_id`, `name`, `year`" +
                ", `displacement`, `gearbox`, `price`, `status`) VALUES(?,?,?,?,?,?,?)";
        JdbcDaoSupport.execute(sql, carStyleId, styleInfo.getStyle(), styleInfo.getYear()
                , turbocharg.getDisplacement(), styleInfo.getGearbox(), styleInfo.getPrice()
                , "unsale".equals(turbocharg.getSaleType()) ? 0 : 1);
    }
    private static List<DbBrand> getAllCarStyle(Date startDate) {
//        String sql = "select * from bitauto_car_style where update_time>=? and id > 8841";
        String sql = "select * from bitauto_car_style where update_time>=?";
        List<DbBrand> carStyles = Lists.newArrayList();
        JdbcDaoSupport.getMapForList(sql, startDate).forEach(brandMap -> {
            DbBrand brand = new DbBrand();
            brand.setDbId(Long.parseLong(brandMap.get("id")));
            brand.setName(brandMap.get("name"));
            brand.setUrl(brandMap.get("url"));
            carStyles.add(brand);
        });
        logger.info("total car style size: " + carStyles.size());
        return carStyles;
    }
    private static void saveAllBrand() {
        List<Brand> brands = grab.getBrands();
        brands.forEach(brand -> {
            long brandId = saveMainBrand(brand);
            Map<String, Brand> childMap = grab.getBrandInfo(brand.getId());
            Brand detailBrand = childMap.get(brand.getAlpha() + ":" + brand.getId());
            List<Brand> subBrands = detailBrand.getChild();
            subBrands.forEach(subBrand -> {
                switch(subBrand.getType()) {
                    case "cb":
                        long subBrandId = saveSubBrand(brandId, subBrand);
                        subBrand.getChild().forEach(carStyle -> {
                            saveCarStyle(subBrandId, carStyle);
                        });
                        break;
                    case "cs":
                        subBrands.forEach(carStyle -> {
                            saveCarStyle(brandId, carStyle);
                        });
                        break;
                    default:
                        throw new RuntimeException("unknow Type for brand: " + subBrand.getType());
                }
            });
            logger.info("brand end: " + brand.getName());
        });
        logger.info("save all brand end!");
    }

    private static long saveMainBrand(Brand brand) {
        String sql = "INSERT INTO `car`.`bitauto_brand` (`org_id`, `name`, `url`, `alpha`, `parent_id`) " +
                "VALUES (?,?,?,?,0)" +
                "ON DUPLICATE KEY UPDATE `name`=?, `url`=?, `alpha`=?, `update_time`=now()";
        long id = JdbcDaoSupport.executeAndgetGeneratedKey(sql
                , brand.getId(), brand.getName(), brand.getUrl(), brand.getAlpha()
                , brand.getName(), brand.getUrl(), brand.getAlpha()
        );
        return id;
    }

    private static long saveSubBrand(long brandId, Brand subBrand) {
        String sql = "INSERT INTO `car`.`bitauto_brand` (`org_id`, `name`, `url`, `parent_id`) " +
                "VALUES (?,?,?,?)" +
                "ON DUPLICATE KEY UPDATE `name`=?, `url`=?, `parent_id`=?, `update_time`=now()";
        long id = JdbcDaoSupport.executeAndgetGeneratedKey(sql
                , subBrand.getId(), subBrand.getName(), subBrand.getUrl(), brandId
                , subBrand.getName(), subBrand.getUrl(), brandId
        );
        return id;
    }

    private static boolean filterCarStyle(long brandId, Brand carStyle) {
        switch (carStyle.getSalestate()) {
            case "在销":
            case "待销":
            case "待查":
                return true;
            case "停销":
                return false;
            default:
                logger.warn("unknown db brandId: " + brandId + "sale state: " + carStyle.getSalestate());
                return false;
        }
    }

    private static void saveCarStyle(long brandId, Brand carStyle) {
        if (! filterCarStyle(brandId, carStyle)) {
            return;
        }
        String sql = "INSERT INTO `car`.`bitauto_car_style` (`name`, `url`, `brand_id`) " +
                "VALUES (?,?,?)" +
                "ON DUPLICATE KEY UPDATE `name`=?, `url`=?, `brand_id`=?, `update_time`=now()";
        long id = JdbcDaoSupport.executeAndgetGeneratedKey(sql
                , carStyle.getName(), carStyle.getUrl(), brandId
                , carStyle.getName(), carStyle.getUrl(), brandId
        );
    }
}
