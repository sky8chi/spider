package com.skyzd.spider.car.shops.mot.beans;

/**
 * Created by sky.chi on 4/16/2017 4:39 PM.
 * Email: sky8chi@gmail.com
 */
public class ParameterInfo {
    private String url;
    private String chassisCompany;  	//底盘企业：底盘生产企业
    private String chassisNo;           //底盘型号
    private String engineCompany;       //改动机企业：改动机生产企业
    private String engineNo;            //改动机型号
    private String transmissionNo;      //变速器型号
    private String mainReducerRate;     //主减速器速比

    public String getChassisCompany() {
        return chassisCompany;
    }

    public void setChassisCompany(String chassisCompany) {
        this.chassisCompany = chassisCompany;
    }

    public String getChassisNo() {
        return chassisNo;
    }

    public void setChassisNo(String chassisNo) {
        this.chassisNo = chassisNo;
    }

    public String getEngineCompany() {
        return engineCompany;
    }

    public void setEngineCompany(String engineCompany) {
        this.engineCompany = engineCompany;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getTransmissionNo() {
        return transmissionNo;
    }

    public void setTransmissionNo(String transmissionNo) {
        this.transmissionNo = transmissionNo;
    }

    public String getMainReducerRate() {
        return mainReducerRate;
    }

    public void setMainReducerRate(String mainReducerRate) {
        this.mainReducerRate = mainReducerRate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
