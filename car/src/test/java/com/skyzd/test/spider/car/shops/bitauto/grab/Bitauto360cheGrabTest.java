package com.skyzd.test.spider.car.shops.bitauto.grab;

import com.google.gson.Gson;
import com.skyzd.spider.car.shops.bitauto.grab.BitautoGrab;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by sky.chi on 4/23/2017 9:57 PM.
 * Email: sky8chi@gmail.com
 */
public class Bitauto360cheGrabTest {
    private BitautoGrab bitautoGrab;
    @Before
    public void setUp() throws Exception {
        bitautoGrab = new BitautoGrab();
    }

    @Test
    public void getBrands() throws Exception {
        System.out.println(new Gson().toJson(bitautoGrab.getBrands()));
    }

    @Test
    public void getBrandInfo() throws Exception {
        System.out.println(new Gson().toJson(bitautoGrab.getBrandInfo("9")));
    }

    @Test
    public void getCarInfo() throws Exception {
        //        String url = "http://car.bitauto.com/tree_chexing/sb_3999/";
        String url = "http://car.bitauto.com/tree_chexing/sb_2573/";
        System.out.println(new Gson().toJson(bitautoGrab.getCarInfo(url)));
    }

    @Test
    public void getUnsaleCarInfo() throws Exception {
//        String url = "http://car.bitauto.com/aodia3-3999/2014/#car_list";
        String url = "http://car.bitauto.com/xinaodia6l/2015/#car_list";
        System.out.println(new Gson().toJson(bitautoGrab.getUnsaleCarInfo("unsale", url)));
    }

}