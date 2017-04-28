package com.skyzd.test.spider.car.shops.mot.grab;

import com.google.gson.Gson;
import com.skyzd.spider.car.shops.mot.beans.CarModelInfo;
import com.skyzd.spider.car.shops.mot.grab.MotGrab;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by sky.chi on 4/16/2017 4:32 PM.
 * Email: sky8chi@gmail.com
 */
public class MotBitauto360cheGrabTest {
    MotGrab motGrab;
    @Before
    public void setUp() throws Exception {
        motGrab = new MotGrab();
    }

    @Test
    public void getBatchNoticeUrls() throws Exception {
        System.out.println(motGrab.getBatchNoticeUrls());
    }

    @Test
    public void getBatchNoticeInfo() throws Exception {
        String url = "http://atestsc.mot.gov.cn/pub/publish/38/index.html";
        List<CarModelInfo> carModelInfos = motGrab.getBatchNoticeInfo(url);
        System.out.println(new Gson().toJson(carModelInfos));
    }

    @Test
    public void getConfigUrls() throws Exception {
        String url = "http://atestsc.mot.gov.cn/pub/publish/38/BJ1049V9JEA-C1.html";
        System.out.println(motGrab.getConfigUrls(url));
    }

    @Test
    public void getParameterInfo() throws Exception {
//        String url = "http://atestsc.mot.gov.cn/pub/publish/38/5a747eb858bdaa8f015ab71196056ad9.html";
        String url = "http://atestsc.mot.gov.cn/pub/publish/39/5a747eb858bdaa60015b611c8ccd69ff.html";
        System.out.println(new Gson().toJson(motGrab.getParameterInfo(url)));
    }
}