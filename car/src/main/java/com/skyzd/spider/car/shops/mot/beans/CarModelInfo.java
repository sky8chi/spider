package com.skyzd.spider.car.shops.mot.beans;

import java.util.List;

/**
 * Created by sky.chi on 4/16/2017 2:08 PM.
 * Email: sky8chi@gmail.com
 */
public class CarModelInfo {
    private String url;
    private String no;      //车辆型号
    private String name;    //车辆名称（产品名称）
    private String brand;   //品牌（商标）
    private String maker;   //制造商（车辆生产企业名称）
    private String configsUrl;   //车辆配置url
    private List<ParameterInfo> parameterInfos;     //多配置参数

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getConfigsUrl() {
        return configsUrl;
    }

    public void setConfigsUrl(String configsUrl) {
        this.configsUrl = configsUrl;
    }

    public List<ParameterInfo> getParameterInfos() {
        return parameterInfos;
    }

    public void setParameterInfos(List<ParameterInfo> parameterInfos) {
        this.parameterInfos = parameterInfos;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
