package com.skyzd.test.spider.car.shops._360che.grab;

import com.google.gson.Gson;
import com.skyzd.spider.car.shops._360che.grab._360cheGrab;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by sky.chi on 4/19/2017 7:28 AM.
 * Email: sky8chi@gmail.com
 */
public class Bitauto360cheGrabTest {
    _360cheGrab a360cheGrab;
    @Before
    public void setUp() throws Exception {
        a360cheGrab = new _360cheGrab();
    }

    @Test
    public void getBrands() throws Exception {
        String url = "https://product.360che.com/price/c3_s62_b0_s0.html";
        System.out.println(new Gson().toJson(a360cheGrab.getBrands(url)));
    }
    @Test
    public void getProducts() throws Exception {
        String url = "https://product.360che.com/price/c3_s62_b501_s0.html";
        a360cheGrab.getProducts(url);
    }

    @Test
    public void getProductPara() throws Exception {
//        String url = "https://product.360che.com/m28/7186_para.html";
//        String url = "https://product.360che.com/m28/7183_para.html";
//        String url = "https://product.360che.com/m55/13755_para.html";
        String url = "https://product.360che.com/m38/9685_para.html";
        System.out.println(new Gson().toJson(a360cheGrab.getProductPara("", url)));
    }

}