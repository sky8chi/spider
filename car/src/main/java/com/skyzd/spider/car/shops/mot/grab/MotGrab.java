package com.skyzd.spider.car.shops.mot.grab;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.skyzd.framework.dom.Table;
import com.skyzd.framework.http.HttpClientUtil;
import com.skyzd.framework.http.HttpException;
import com.skyzd.framework.http.HttpUtil;
import com.skyzd.framework.regex.IRegexService;
import com.skyzd.framework.regex.RegexService;
import com.skyzd.spider.car.shops.mot.beans.Batch;
import com.skyzd.spider.car.shops.mot.beans.CarModelInfo;
import com.skyzd.spider.car.shops.mot.beans.ParameterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by sky.chi on 4/15/2017 3:28 PM.
 * Email: sky8chi@gmail.com
 */
public class MotGrab {
    private Logger logger = LoggerFactory.getLogger(MotGrab.class);
    private final static String ROOT_URL = "http://atestsc.mot.gov.cn/pub/publish/index.html";
    private final IRegexService regexService = new RegexService();

    /**
     * 获取各批次通知url
     * @return
     */
    public List<Batch> getBatchNoticeUrls(){
        final List<Batch> urls = Lists.newArrayList();
        try {
            String body = HttpClientUtil.get(ROOT_URL);
            String listBlock = regexService.getSingleGroup(body, "<ul class=\"newul\">([\\s\\S]*?)</ul>");
            List<String[]> tmpUrlAndNames = regexService.getMultiGroups(listBlock, "href=\"([^\"]*+)\"[^>]*+>([\\s\\S]*?)</a>");
            tmpUrlAndNames.stream().forEach(tmpUrlAndName -> {
                Batch batch = new Batch();
                batch.setUrl(HttpUtil.getAbsoluteURL(ROOT_URL, tmpUrlAndName[0]));
                batch.setName(tmpUrlAndName[1]);
                urls.add(batch);
            });
        } catch (HttpException e) {
        }
        return urls;
    }


    public List<CarModelInfo> getBatchNoticeInfo(String url) {
        List<CarModelInfo> carModelInfos = Lists.newArrayList();
        try {
            String body = HttpClientUtil.get(url);
            String tableStr = regexService.getSingleGroup(body, "<table[^>]*?class=\"x-grid-table\"[^>]*+>([\\s\\S]*?)</table>");
            List<String> trs = regexService.getSingleGroups(tableStr, "<tr[^>]*+>([\\s\\S]*?)</tr>");
            trs.stream().forEach(trInfo -> {
                List<String[]> tdStrsLst = regexService.getMultiGroups(trInfo,
                        "<td[^>]*+>[\\s\\S]*?<a[^>]*?href=\"([^\"]*+)\"[^>]*+>([\\s\\S]*?)</a>[\\s\\S]*?</td>" +
                                "[\\s\\S]*?<td[^>]*+>([\\s\\S]*?)</td>" +
                                "[\\s\\S]*?<td[^>]*+>([\\s\\S]*?)</td>" +
                                "[\\s\\S]*?<td[^>]*+>([\\s\\S]*?)</td>" +
                                "[\\s\\S]*?<td[^>]*+>([\\s\\S]*?)</td>");
                if (tdStrsLst != null && ! tdStrsLst.isEmpty()) {
                    tdStrsLst.stream().forEach(tdStrArr -> {
                        CarModelInfo carModelInfo = new CarModelInfo();
                        carModelInfo.setConfigsUrl(HttpUtil.getAbsoluteURL(url, tdStrArr[0].replaceAll(" ", "%20")));
                        carModelInfo.setNo(tdStrArr[1]);
                        carModelInfo.setName(tdStrArr[2]);
                        carModelInfo.setBrand(tdStrArr[3]);
                        carModelInfo.setMaker(tdStrArr[4]);
                        carModelInfos.add(carModelInfo);
                    });
                } else {
                    logger.warn("can not pass for: {}", trInfo);
                }
            });
        } catch (HttpException e) {
            e.printStackTrace();
        }
        return carModelInfos;
    }

    public List<String> getConfigUrls(String url) {
        List<String> configUrls = Lists.newArrayList();
        try {
            String body = HttpClientUtil.get(url);
            String tableStr = regexService.getSingleGroup(body, "(<table[^>]*?class=\"tabcenter\"[^>]*+>[\\s\\S]*?</table>)");
            Table table = new Table(tableStr);
            table.parse();
            List<Table.Tr> trLst = table.getTrs();
            trLst.stream().forEach(tr -> {
                String tmpUrl = regexService.getSingleGroup(tr.getTds().get(1).getContent(),"href=\"([^\"]*+)\"");
                if (! Strings.isNullOrEmpty(tmpUrl)) {
                    configUrls.add(HttpUtil.getAbsoluteURL(url, tmpUrl));
                }
            });
        } catch (HttpException e) {
            e.printStackTrace();
        }
        return configUrls;
    }

    public ParameterInfo getParameterInfo(String url) {
        ParameterInfo parameterInfo = new ParameterInfo();
        try {
            String body = HttpClientUtil.get(url);
            String tableStr = regexService.getSingleGroup(body
                    , "<div class=\"topsearch\">[\\s\\S]*?(<table[^>]*+>[\\s\\S]*?</table>)");
            parameterInfo.setChassisCompany(regexService.getSingleGroup(tableStr
                    , "<th[^>]*+>\\s*底盘生产企业\\s*(?:<strong>\\s*</strong>)?\\s*</th>[\\s\\S]*?<td[^>]*+>((?:[^<]|<(?!/td>))*+)</td>"));
            parameterInfo.setChassisNo(regexService.getSingleGroup(tableStr
                    , "<th[^>]*+>\\s*底盘型号\\s*(?:<strong>\\s*</strong>)?\\s*</th>[\\s\\S]*?<td[^>]*+>((?:[^<]|<(?!/td>))*+)</td>"));
            parameterInfo.setEngineCompany(regexService.getSingleGroup(tableStr
                    , "<th[^>]*+>\\s*发动机生产企业\\s*(?:<strong>\\s*</strong>)?\\s*</th>[\\s\\S]*?<td[^>]*+>((?:[^<]|<(?!/td>))*+)</td>"));
            parameterInfo.setEngineNo(regexService.getSingleGroup(tableStr
                    , "<th[^>]*+>\\s*发动机型号\\s*(?:<strong>\\s*</strong>)?\\s*</th>[\\s\\S]*?<td[^>]*+>((?:[^<]|<(?!/td>))*+)</td>"));
            parameterInfo.setTransmissionNo(regexService.getSingleGroup(tableStr
                    , "<th[^>]*+>\\s*变速器型号\\s*(?:<strong>\\s*</strong>)?\\s*</th>[\\s\\S]*?<td[^>]*+>((?:[^<]|<(?!/td>))*+)</td>"));
            parameterInfo.setMainReducerRate(regexService.getSingleGroup(tableStr
                    , "<th[^>]*+>\\s*主减速器速比（驱动桥速比）\\s*</th>[\\s\\S]*?<td[^>]*+>((?:[^<]|<(?!/td>))*+)</td>"));
        } catch (HttpException e) {
            e.printStackTrace();
        }
        return parameterInfo;
    }
}
