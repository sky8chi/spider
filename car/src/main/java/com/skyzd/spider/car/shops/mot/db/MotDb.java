package com.skyzd.spider.car.shops.mot.db;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.skyzd.spider.car.shops.bitauto.beans.Brand;
import com.skyzd.spider.car.shops.mot.beans.*;
import com.skyzd.spider.car.shops.mot.grab.MotGrab;
import com.skyzd.spider.dao.db.JdbcDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by sky.chi on 4/27/2017 4:21 PM.
 * Email: sky8chi@gmail.com
 */
public class MotDb {
    private static Logger logger = LoggerFactory.getLogger(MotDb.class);
    private static MotGrab grab = new MotGrab();

    public static void main(String[] args) {
//        long lastRunTime = 1493274182* 1000L;
//        Date startDate = new Date(lastRunTime);
        Date startDate = new Date();
        saveBatches();
        saveModels(startDate);
        saveParams(startDate);
        logger.info("mot all end!!!");
    }

    private static List<DbBatch> getBatches(Date startDate) {
        List<DbBatch> batches = Lists.newArrayList();
        String sql = "select * from mot_batch where update_time > ?";
//        String sql = "select * from mot_batch where update_time > ? and id =1";
        List<Map<String, String>> mapLst = JdbcDaoSupport.getMapForList(sql, startDate);
        mapLst.forEach(map -> {
            DbBatch batch = new DbBatch();
            batch.setName(map.get("name"));
            batch.setUrl(map.get("url"));
            batch.setDbId(Long.parseLong(map.get("id")));
            batches.add(batch);
        });

        return batches;
    }

    private static void saveBatches() {
        MotGrab motGrab = new MotGrab();
        List<Batch> batches = motGrab.getBatchNoticeUrls();
        batches.stream().forEach(batch -> saveBatch(batch));
    }

    private static long saveBatch(Batch batch) {
        String sql = "INSERT INTO `car`.`mot_batch` (`name`,`url`) " +
                "VALUES(?,?)" +
                "ON DUPLICATE KEY UPDATE `name`=?, `url`=?, `update_time`=now()";
        return JdbcDaoSupport.executeAndgetGeneratedKey(sql, batch.getName(), batch.getUrl()
                , batch.getName(), batch.getUrl());
    }

    private static void saveModel(long batchId, CarModelInfo carModelInfo) {
        String sql = "INSERT INTO `car`.`mot_model` (`batch_id`, `name`, `brand`, `no`, `maker`, `detail_url`)" +
                " VALUES(?, ?,?,?,?,?)" +
                "ON DUPLICATE KEY UPDATE `name`=?, `brand`=?, `maker`=?, `detail_url`=?, `update_time`=now()";
        JdbcDaoSupport.execute(sql, batchId, carModelInfo.getName(), carModelInfo.getBrand(), carModelInfo.getNo()
                , carModelInfo.getMaker(), carModelInfo.getConfigsUrl()
                , carModelInfo.getName(), carModelInfo.getBrand(), carModelInfo.getMaker(), carModelInfo.getConfigsUrl()
        );
    }

    private static void saveModels(Date startDate) {
        List<DbBatch> batches = getBatches(startDate);
        batches.forEach(batch -> {
            String url = batch.getUrl();
            List<CarModelInfo> list = grab.getBatchNoticeInfo(url);
            list.forEach(carModelInfo -> {
                saveModel(batch.getDbId(), carModelInfo);
            });
            logger.info("batch end: {}", new Gson().toJson(batch));
        });
    }

    private static List<DbCarModelInfo> getModels(Date startDate) {
        List<DbCarModelInfo> list = Lists.newArrayList();
        String sql = "select * from mot_model where update_time > ?";
//        String sql = "select * from mot_model where update_time > ? and id = 1";
        List<Map<String, String>> mapLst = JdbcDaoSupport.getMapForList(sql, startDate);
        mapLst.forEach(map -> {
            DbCarModelInfo carModelInfo = new DbCarModelInfo();
            carModelInfo.setConfigsUrl(map.get("detail_url"));
            carModelInfo.setDbId(Long.parseLong(map.get("id")));
            list.add(carModelInfo);
        });
        return list;
    }

    private static void saveParams(Date startDate) {
        List<DbCarModelInfo> models = getModels(startDate);
        models.forEach(dbCarModelInfo -> {
            List<String> configUrlLst = grab.getConfigUrls(dbCarModelInfo.getConfigsUrl());
            configUrlLst.forEach(paramerUrl -> {
                ParameterInfo parameterInfo = grab.getParameterInfo(paramerUrl);
                parameterInfo.setUrl(paramerUrl);
                saveParam(dbCarModelInfo.getDbId(), parameterInfo);
            });
            logger.info("model end: {}; url:{}", dbCarModelInfo.getDbId(), dbCarModelInfo.getConfigsUrl());
        });
    }

    private static void saveParam(long modelId, ParameterInfo parameterInfo) {
        String sql = "INSERT INTO `car`.`mot_model_param` (`model_id`, `chassisCompany`, `chassisNo`" +
                ", `engineCompany`, `engineNo`, `transmissionNo`, `mainReducerRate`, `url`)" +
                " VALUES(?,?,?,?,?,?,?,?)";
        JdbcDaoSupport.execute(sql, modelId, parameterInfo.getChassisCompany(), parameterInfo.getChassisNo()
                , parameterInfo.getEngineCompany(), parameterInfo.getEngineNo(), parameterInfo.getTransmissionNo()
                , parameterInfo.getMainReducerRate(), parameterInfo.getUrl());
    }
}
