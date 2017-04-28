package com.skyzd.spider.car.shops._360che.db;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.skyzd.spider.car.shops._360che.beans.Brand;
import com.skyzd.spider.car.shops._360che.beans.DbBrand;
import com.skyzd.spider.car.shops._360che.beans.ProductPara;
import com.skyzd.spider.car.shops._360che.grab._360cheGrab;
import com.skyzd.spider.dao.db.JdbcDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by sky.chi on 4/27/2017 4:20 PM.
 * Email: sky8chi@gmail.com
 */
public class _360cheDb {
    private static Logger logger = LoggerFactory.getLogger(_360cheDb.class);
    private static _360cheGrab grab = new _360cheGrab();
    public static void main(String[] args) {
//        long lastRunTime = 1493282119* 1000L;
//        Date startDate = new Date(lastRunTime);
        Date startDate = new Date();
        logger.info("start bitauto time: " + startDate.getTime());
        saveBrands();
        getBrands(startDate).forEach(dbBrand -> {
            grab.getProducts(dbBrand.getUrl()).forEach(productPara ->
                    saveParas(dbBrand.getDbId(), productPara));
            logger.info("brand end: " + dbBrand.getName() + "; url: " + dbBrand.getUrl() + "; dbId: " + dbBrand.getDbId());
        });
        logger.info("360che all end!!!");
    }

    public static void saveParas(long dbBrandId, ProductPara productPara) {
        String sql = "INSERT INTO `car`.`360che_gearbox` (`brand_id`, `model`, `serie`, `form`, `status`)" +
                " VALUES(?,?,?,?,?)";
        try {
            JdbcDaoSupport.execute(sql, dbBrandId, productPara.getModel(), productPara.getSerie()
                    , productPara.getForm(), productPara.getStatus());
        } catch (Exception e) {
            logger.error("dbError: {} {}", dbBrandId, new Gson().toJson(productPara), e);
        }
    }

    public static List<DbBrand> getBrands(Date startDate){
        String sql = "select * from 360che_brand where update_time>=?";
//        String sql = "select * from 360che_brand where update_time>=? and id > 18";
        List<DbBrand> brands = Lists.newArrayList();
        JdbcDaoSupport.getMapForList(sql, startDate).forEach(brandMap -> {
            DbBrand brand = new DbBrand();
            brand.setDbId(Long.parseLong(brandMap.get("id")));
            brand.setName(brandMap.get("name"));
            brand.setUrl(brandMap.get("url"));
            brands.add(brand);
        });
        logger.info("total brands size: " + brands.size());
        return brands;
    }

    public static void saveBrands() {
        String url = "https://product.360che.com/price/c3_s62_b0_s0.html";
        List<Brand> brands = grab.getBrands(url);
        brands.forEach(brand -> saveBrand(brand));
        logger.info("save brands end!!! size: " + brands.size());
    }

    private static long saveBrand(Brand brand) {
        String sql = "INSERT INTO `car`.`360che_brand` (`name`,`url`) " +
                "VALUES(?,?)" +
                "ON DUPLICATE KEY UPDATE `name`=?, `url`=?, `update_time`=now()";
        return JdbcDaoSupport.executeAndgetGeneratedKey(sql, brand.getName(), brand.getUrl()
                , brand.getName(), brand.getUrl());
    }
}
