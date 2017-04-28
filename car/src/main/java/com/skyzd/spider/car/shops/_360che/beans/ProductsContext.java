package com.skyzd.spider.car.shops._360che.beans;

import java.util.List;

/**
 * Created by sky.chi on 4/20/2017 7:31 AM.
 * Email: sky8chi@gmail.com
 */
public class ProductsContext {
    private List<String[]> urlAndStatuses;
    private List<String[]> productUrlAndNames;
    private String currentStatusName;

    public String getCurrentStatusName() {
        return currentStatusName;
    }

    public void setCurrentStatusName(String currentStatusName) {
        this.currentStatusName = currentStatusName;
    }

    public List<String[]> getUrlAndStatuses() {
        return urlAndStatuses;
    }

    public void setUrlAndStatuses(List<String[]> urlAndStatuses) {
        this.urlAndStatuses = urlAndStatuses;
    }

    public List<String[]> getProductUrlAndNames() {
        return productUrlAndNames;
    }

    public void setProductUrlAndNames(List<String[]> productUrlAndNames) {
        this.productUrlAndNames = productUrlAndNames;
    }
}
