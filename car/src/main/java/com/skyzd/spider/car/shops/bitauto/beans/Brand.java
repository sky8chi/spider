package com.skyzd.spider.car.shops.bitauto.beans;

import java.util.List;

/**
 * Created by sky.chi on 4/23/2017 9:47 PM.
 * Email: sky8chi@gmail.com
 */
public class Brand {
    private String alpha;
    private String id;
    private String type;  //mb 主品牌  cb 二级品牌  cs 车型
    private String name;
    private String url;
    private String num;
    private String salestate;
    private List<Brand> child;

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public List<Brand> getChild() {
        return child;
    }

    public void setChild(List<Brand> child) {
        this.child = child;
    }

    public String getSalestate() {
        return salestate;
    }

    public void setSalestate(String salestate) {
        this.salestate = salestate;
    }
}
