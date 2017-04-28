package com.skyzd.spider.car.shops.bitauto.beans;

import java.util.List;

/**
 * Created by sky.chi on 4/24/2017 12:16 AM.
 * Email: sky8chi@gmail.com
 */
public class Turbocharg {
    private String displacement;
    private String saleType;
    private List<StyleInfo> cars;

    public String getDisplacement() {
        return displacement;
    }

    public void setDisplacement(String displacement) {
        this.displacement = displacement;
    }

    public List<StyleInfo> getCars() {
        return cars;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public void setCars(List<StyleInfo> cars) {
        this.cars = cars;
    }
}
