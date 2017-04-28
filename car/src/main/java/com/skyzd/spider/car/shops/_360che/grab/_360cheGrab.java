package com.skyzd.spider.car.shops._360che.grab;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.skyzd.framework.http.HttpClientUtil;
import com.skyzd.framework.http.HttpException;
import com.skyzd.framework.http.HttpUtil;
import com.skyzd.framework.regex.IRegexService;
import com.skyzd.framework.regex.RegexService;
import com.skyzd.spider.car.shops._360che.beans.Brand;
import com.skyzd.spider.car.shops._360che.beans.ProductPara;
import com.skyzd.spider.car.shops._360che.beans.ProductsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by sky.chi on 4/17/2017 6:18 AM.
 * Email: sky8chi@gmail.com
 */
public class _360cheGrab {
    private Logger logger = LoggerFactory.getLogger(_360cheGrab.class);
    private IRegexService regexService = new RegexService();
    public List<Brand> getBrands(String url) {
        List<Brand> brands = Lists.newArrayList();
        try {
            String body = HttpClientUtil.get(url);
            String brandsStr = regexService.getSingleGroup(body
                    ,"<div class=\"filter-brands-detail\">([\\s\\S]*?)</div>");
            List<String[]> brandStrLst = regexService.getMultiGroups(brandsStr
                    , "<li[^>]*+>\\s*<a[^>]*?href=\"([^\"]*+)\"[^>]*+>" +
                            "\\s*(?:<i[^>]*+>(?:[^<]|<(?!/i>))*+</i>)?" +
                            "\\s*([\\s\\S]*?)\\s*</a>\\s*</li>");
            brandStrLst.stream().forEach(brandInfoArr -> {
                Brand brand = new Brand();
                brand.setUrl(HttpUtil.getAbsoluteURL(url, brandInfoArr[0]));
                brand.setName(brandInfoArr[1]);
                brands.add(brand);
            });
        } catch (HttpException e) {
            e.printStackTrace();
        }
        return brands;
    }

    private List<String[]> getPages(String url, String body) {
        String pageStr = regexService.getSingleGroup(body, "<div[^>]+?class=\"page\"[^>]*+>([\\s\\S]*?)</div>");
        List<String[]> otherPages = Lists.newArrayList();
        if (! Strings.isNullOrEmpty(pageStr)) {
            otherPages = regexService.getMultiGroups(pageStr, "<a[^>]+?href=\"([^\"]*+)\"[^>]*+>(\\d+)</a>");
            otherPages.stream().forEach(otherPageInfoArr ->
                    otherPageInfoArr[0] = HttpUtil.getAbsoluteURL(url, otherPageInfoArr[0]));
        } else {
            logger.warn("url:{} has no page", url);
        }
        return otherPages;
    }

    private List<String[]> getProductUrls(String url, String body) {
        String productLstStr = regexService.getSingleGroup(body
                , "<ul[^>]+?class=\"products-list\"[^>]*+>([\\s\\S]*?)</ul>");
        List<String[]> productsLst = regexService.getMultiGroups(productLstStr
                , "<li[^>]*+>(?:[^<]|<(?!/li>|h5>\\s*<a[^>]*?href=\"([^\"]*+)\"[^>]*+>([\\s\\S]*?)</a>))*+(?:<h5>|</li>)");
        productsLst.stream().forEach(productInfoArr ->
                productInfoArr[0] = HttpUtil.getAbsoluteURL(url, productInfoArr[0].replace("index", "para")));
        return productsLst;
    }

    private void setStatus(ProductsContext productsContext, String url, String body) {
        String tabStr = regexService.getSingleGroup(body
                , "<div[^>]+?class=\"tabs\"[^>]*+>([\\s\\S]*?)</div>");
        List<String[]> saleStatusLst = regexService.getMultiGroups(tabStr
                , "<a[^>]+?href=\"([^\"]*+)\"[^>]*+>((?:[^<]|<(?!em|/a>))*+)(?:<em>|</a>)");
        if (saleStatusLst != null && !saleStatusLst.isEmpty()) {
            saleStatusLst.stream().forEach(saleStatusArr ->
                    saleStatusArr[0] = HttpUtil.getAbsoluteURL(url, saleStatusArr[0]));
        } else {
            logger.warn("has no other sale status urls: " + url);
        }
        String currentStatus = regexService.getSingleGroup(tabStr, "<span[^>]+?class=\"selected\"[^>]*+>((?:[^<]|<(?!em|/span>))*+)(?:<em>|</span>)");
        productsContext.setUrlAndStatuses(saleStatusLst);
        productsContext.setCurrentStatusName(currentStatus);
    }

    private ProductsContext getProductsContext(String url) {
        ProductsContext productsContext = new ProductsContext();
        try {
            String body = HttpClientUtil.get(url);
            setStatus(productsContext, url, body);
            List<String[]> pageList = getPages(url, body);
            List<String[]> productUrlLst = getProductUrls(url, body);
            pageList.forEach(pageInfoArr -> {
                String tmpBody = null;
                try {
                    tmpBody = HttpClientUtil.get(pageInfoArr[0]);
                } catch (HttpException e) {
                    e.printStackTrace();
                }
                productUrlLst.addAll(getProductUrls(pageInfoArr[0], tmpBody));
            });
            productsContext.setProductUrlAndNames(productUrlLst);
        } catch (HttpException e) {
            e.printStackTrace();
        }
        return productsContext;
    }

    public List<ProductPara> getProducts(String brandUrl) {
        List<ProductPara> productParas = Lists.newArrayList();
        getProductUrls(brandUrl).forEach((statusName, productUrlAndNameArrLst) -> {
            productUrlAndNameArrLst.forEach(productUrlAndNameArr -> {
                String paraUrl = productUrlAndNameArr[0];
                ProductPara productPara = getProductPara(statusName, paraUrl);
                productParas.add(productPara);
            });
        });
        return productParas;
    }

    public ProductPara getProductPara(String statusName, String url) {
        try {
            String body = HttpClientUtil.get(url);
            String paraTableStr = regexService.getSingleGroup(body
                    , "<table[^>]*+>([\\s\\S]*?)</table>");
            String model = regexService.getSingleGroup(paraTableStr
                    , "<tr>\\s*<td class=\"p_c_a\"><div>变速箱：</div></td>(?:[^<]|<(?!/tr>))*+(?<=<div[^>]{0,50}+>([\\s\\S]{0,50}?)</div>[\\s\\S]{0,200}?)</tr>");
            String serie = regexService.getSingleGroup(paraTableStr
                    , "<tr>\\s*<td class=\"p_c_a\"><div>系列：</div></td>(?:[^<]|<(?!/tr>))*+(?<=<div[^>]{0,50}+>([\\s\\S]{0,50}?)</div>[\\s\\S]{0,200}?)</tr>");
            String gears = regexService.getSingleGroup(paraTableStr
                    , "<tr>\\s*<td class=\"p_c_a\"><div>档位数：</div></td>(?:[^<]|<(?!/tr>))*+(?<=<div[^>]{0,50}+>([\\s\\S]{0,50}?)</div>[\\s\\S]{0,200}?)</tr>");
            String form = regexService.getSingleGroup(paraTableStr
                    , "<tr>\\s*<td class=\"p_c_a\"><div>换挡形式：</div></td>(?:[^<]|<(?!/tr>))*+(?<=<div[^>]{0,50}+>([\\s\\S]{0,50}?)</div>[\\s\\S]{0,200}?)</tr>");
            String form2 = regexService.getSingleGroup(paraTableStr
                    , "<tr>\\s*<td class=\"p_c_a\"><div>换挡方式：</div></td>(?:[^<]|<(?!/tr>))*+(?<=<div[^>]{0,50}+>([\\s\\S]{0,50}?)</div>[\\s\\S]{0,400}?)</tr>");
            return ProductPara.custom().setUrl(url).setStatus(statusName).setModel(model).setSerie(serie)
                    .setGears(gears).setForm(form).setForm2(form2).build();
        } catch (Exception e) {
            logger.error("error: {}", url, e);
        }
        return null;
    }

    private Map<String, List<String[]>> getProductUrls(String url) {
        ProductsContext productsContext = getProductsContext(url);
        List<String[]> statuseUrls = productsContext.getUrlAndStatuses();
        Map<String, List<String[]>> map = Maps.newHashMap();
        map.put(productsContext.getCurrentStatusName(), productsContext.getProductUrlAndNames());
        if (statuseUrls != null && ! statuseUrls.isEmpty()) {
            statuseUrls.forEach(urlAndStatusArr -> {
                String statusUrl = urlAndStatusArr[0];
                ProductsContext tmpProductsContext = getProductsContext(statusUrl);
                map.put(tmpProductsContext.getCurrentStatusName(), tmpProductsContext.getProductUrlAndNames());
            });
        }
        return map;
    }

}
