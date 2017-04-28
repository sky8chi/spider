package com.skyzd.spider.car.shops.bitauto.grab;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.skyzd.framework.http.HttpClientUtil;
import com.skyzd.framework.http.HttpException;
import com.skyzd.framework.http.HttpUtil;
import com.skyzd.framework.regex.IRegexService;
import com.skyzd.framework.regex.RegexService;
import com.skyzd.spider.car.shops.bitauto.beans.Brand;
import com.skyzd.spider.car.shops.bitauto.beans.StyleInfo;
import com.skyzd.spider.car.shops.bitauto.beans.Turbocharg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.List;
import java.util.Map;

/**
 * Created by sky.chi on 4/17/2017 6:18 AM.
 * Email: sky8chi@gmail.com
 */
public class BitautoGrab {
    private Logger logger = LoggerFactory.getLogger(BitautoGrab.class);
    private IRegexService regexService = new RegexService();
    private String pageUrl = "http://car.bitauto.com/";


    private String getVal(Map<String, Object> map, String key) {
        if (map == null) {
            return null;
        }
        Object val = map.get(key);
        if (val == null) {
            return null;
        }
        return val.toString();
    }
    private Brand getBrand(String alpha, Map<String, Object> brandInfoMap) {
        Brand brand = new Brand();
        brand.setAlpha(alpha);
        brand.setId(getVal(brandInfoMap, "id"));
        brand.setType(getVal(brandInfoMap, "type"));
        brand.setName(getVal(brandInfoMap, "name"));
        brand.setUrl(HttpUtil.getAbsoluteURL(pageUrl, getVal(brandInfoMap, "url")));
        brand.setNum(getVal(brandInfoMap, "num"));
        brand.setSalestate(getVal(brandInfoMap, "salestate"));
        return brand;
    }

    public List<Brand> getBrands() {
        Map<String, Brand> brandsMap = getBrandInfo("0");
        List<Brand> brandsLst = Lists.newArrayList();
        brandsMap.values().forEach(brand -> brandsLst.add(brand));
        return brandsLst;
    }

    public Map<String, Brand> getBrandInfo(String objId) {
        String url = "http://api.car.bitauto.com/CarInfo/getlefttreejson.ashx?tagtype=chexing&pagetype=masterbrand&objid=" + objId;
        Map<String, Brand> brandsMap = Maps.newHashMap();
        try {
            String body = HttpClientUtil.get(url);
            String brandsStr = regexService.getSingleGroup(body, "(\\{[\\s\\S]*\\})");
            ScriptEngineManager sem = new ScriptEngineManager();
            ScriptEngine engine = sem.getEngineByExtension("js");
            engine.eval("var a="+brandsStr);
            Map<String, Object> map = (Map<String, Object>)engine.get("a");
            Map<String, Object> brandsJsonMap = (Map<String, Object>)map.get("brand");
            brandsJsonMap.forEach((alpha, v) -> {
                ((Map<String, Map<String, Object>>)v).forEach((i, brandInfoMap) -> {
                    Brand brand = getBrand(alpha, brandInfoMap);
                    Map<String, Map<String, Object>> child = (Map<String, Map<String, Object>>)brandInfoMap.get("child");
                    List<Brand> secondLst = Lists.newArrayList();
                    if (child != null) {
                        child.forEach((sindex, secondBrandInfoMap) -> {
                            Brand secondBrand = getBrand(alpha, secondBrandInfoMap);
                            secondLst.add(secondBrand);
                            Map<String, Map<String, Object>> thirdChild = (Map<String, Map<String, Object>>)secondBrandInfoMap.get("child");
                            List<Brand> thirdBrands = Lists.newArrayList();
                            if (thirdChild != null) {
                                thirdChild.forEach((tindex, thirdBrandInfoMap) -> {
                                    Brand thirdBrand = getBrand(alpha, thirdBrandInfoMap);
                                    thirdBrands.add(thirdBrand);
                                });
                                secondBrand.setChild(thirdBrands);
                            }
                        });
                        brand.setChild(secondLst);
                    }
                    brandsMap.put(alpha + ":" + brand.getId(), brand);
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return brandsMap;
    }

    private void parseTable(String url, String saleType, List<Turbocharg> turbochargList, List<String> trStrLst, String style) {
        Turbocharg turbocharg = new Turbocharg();
        List<StyleInfo> styleInfos = Lists.newArrayList();
        for (String trStr : trStrLst) {
            if (trStr.contains(style)) {
                turbocharg = new Turbocharg();
                styleInfos = Lists.newArrayList();
                String displacement = regexService.getSingleGroup(trStr,
                        "<th[^>]*?class=\"first-item\"[^>]*+>\\s*<strong>([\\s\\S]*?)</strong>");
                turbocharg.setDisplacement(displacement);
                turbocharg.setSaleType(saleType);
                turbocharg.setCars(styleInfos);
                turbochargList.add(turbocharg);
            } else {
                String fund = regexService.getSingleGroup(trStr, "<a[^>]*+>([\\s\\S]*?)</a>");
                String[] fundInfoArr = fund.split("\\s+", 2);
                String[] gearboxAndMoney = regexService.getMultiGroup(trStr,
                        "(?:<td[^>]*+>[\\s\\S]*?</td>){2}<td[^>]*+>([\\s\\S]*?)</td>\\s*<td[^>]*+>\\s*<span[^>]*+>([\\s\\S]*?)</span>");
                if (Strings.isNullOrEmpty(gearboxAndMoney[0]) || Strings.isNullOrEmpty(gearboxAndMoney[1])) {
                    logger.warn("has no gearbox info: " + url);
                }
                StyleInfo styleInfo = StyleInfo.custome().setYear(fundInfoArr[0]).setStyle(fundInfoArr[1])
                        .setGearbox(gearboxAndMoney[0]).setPrice(gearboxAndMoney[1]).build();
                styleInfos.add(styleInfo);
            }
        }
    }

    public List<Turbocharg> getCarInfo(String url) {
        List<Turbocharg> turbochargList = Lists.newArrayList();
        List<Turbocharg> unsaleTurbochargList = Lists.newArrayList();
        try {
            String body = HttpClientUtil.get(url);
            String unsaleStr = regexService.getSingleGroup(body, "<div[^>]*?id=\"pop_nosalelist\"[^>]*+>([\\s\\S]*?)</div>");
            if (! Strings.isNullOrEmpty(unsaleStr)) {
                List<String[]> unsaleUrlAndYear = regexService.getMultiGroups(unsaleStr, "<a[^>]*?href=\"([^\"]*+)\"[^>]*+>([\\s\\S]*?)</a>");
                unsaleUrlAndYear.forEach(urlAndYear -> {
                    String year = regexService.getSingleGroup(urlAndYear[1], "(\\d+)");
                    if (Integer.parseInt(year) >= 2014) {
                        List<Turbocharg> unsaleLst = getUnsaleCarInfo("unsale", HttpUtil.getAbsoluteURL(url, urlAndYear[0]));
                        if (unsaleLst != null && ! unsaleLst.isEmpty()) {
                            unsaleTurbochargList.addAll(unsaleLst);
                        }
                    }
                });
            } else {
                logger.warn("has no unsale info: " + url);
            }
            String tableStr = regexService.getSingleGroup(body,
                    "<div[^>]*?class=\"list-table\"[^>]*+>\\s*<table[^>]*?>([\\s\\S]*?)</table>");
            if (! Strings.isNullOrEmpty(tableStr)) {
                List<String> trStrLst = regexService.getSingleGroups(tableStr, "(<tr[^>]*+>[\\s\\S]*?</tr>)");
                parseTable(url, "sale", turbochargList, trStrLst, "table-tit");
            } else {
                logger.warn("has no sale info: " + url);
            }
        } catch (HttpException e) {
            e.printStackTrace();
        }
        turbochargList.addAll(unsaleTurbochargList);
        return turbochargList;
    }

    public List<Turbocharg> getUnsaleCarInfo(String type, String url) {
        List<Turbocharg> turbochargList = Lists.newArrayList();
        try {
            String body = HttpClientUtil.get(url);
            String tableStr = regexService.getSingleGroup(body,
                    "<div[^>]*?class=\"list-table\"[^>]*+>\\s*<table[^>]*?>([\\s\\S]*?)</table>");
            List<String> trStrLst = regexService.getSingleGroups(tableStr, "(<tr[^>]*+>[\\s\\S]*?</tr>)");
            parseTable(url, type, turbochargList, trStrLst, "car_filter_gid");
        } catch (HttpException e) {
            e.printStackTrace();
        }
        return turbochargList;
    }
}
