package com.skyzd.spider.car.shops.bitauto.beans;

import com.google.common.base.Strings;
import com.skyzd.framework.regex.IRegexService;
import com.skyzd.framework.regex.RegexService;
import com.skyzd.spider.car.shops.bitauto.db.BitautoDb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.Style;
import java.math.BigDecimal;

/**
 * Created by sky.chi on 4/24/2017 12:18 AM.
 * Email: sky8chi@gmail.com
 */
public class StyleInfo {
    private String year;
    private String style;
    private String gearbox;
    private String price;

    public StyleInfo(String year, String style, String gearbox, String price) {
        this.year = year;
        this.style = style;
        this.gearbox = gearbox;
        this.price = price;
    }

    public String getYear() {
        return year;
    }

    public String getStyle() {
        return style;
    }

    public String getGearbox() {
        return gearbox;
    }

    public String getPrice() {
        return price;
    }

    public static Builder custome() {
        return new StyleInfo.Builder();
    }

    public static class Builder {
        private static Logger logger = LoggerFactory.getLogger(Builder.class);
        private String year;
        private String style;
        private String gearbox;
        private String price;

        public Builder setYear(String year) {
            this.year = year;
            return this;
        }

        public Builder setStyle(String style) {
            this.style = style;
            return this;
        }

        public Builder setGearbox(String gearbox) {
            this.gearbox = gearbox;
            return this;
        }

        public Builder setPrice(String price) {
            this.price = price;
            return this;
        }

        public StyleInfo build() {
            IRegexService regexService = new RegexService();
            String tmpGearbox;

            if (Strings.isNullOrEmpty(this.gearbox) || this.gearbox.equals("待查")) {
                tmpGearbox = "";
            } else if (this.gearbox.equals("CVT无级变速")) {
                tmpGearbox = "CVT";
            } else if (this.gearbox.equals("E-CVT无级变速")) {
                tmpGearbox = "ECVT";
            } else if (this.gearbox.equals("手动")) {
                tmpGearbox = "T";
            } else if (this.gearbox.equals("自动")) {
                tmpGearbox = "AT";
            } else if (this.gearbox.equals("手自一体") || this.gearbox.equals("半自动")) {
                tmpGearbox = "AMT";
            } else if (this.gearbox.equals("电动车单速变速箱")) {
                tmpGearbox = "";
                logger.warn("unknow gearbox: " + this.gearbox);
            } else {
                String[] infoArr = regexService.getMultiGroup(this.gearbox, "(\\d+)(.*)");
                if (infoArr == null) {
                    throw new RuntimeException("unknow gearbox: " + this.gearbox);
                } else {
                    switch (infoArr[1]) {
                        case "挡手动":
                            tmpGearbox = infoArr[0] + "T";
                            break;
                        case "挡自动":
                            tmpGearbox = infoArr[0] + "AT";
                            break;
                        case "挡手自一体":
                        case "挡半自动":
                            tmpGearbox = infoArr[0] + "AMT";
                            break;
                        case "挡双离合":
                            tmpGearbox = infoArr[0] + "DCT";
                            break;
                        case "挡CVT无级变速":
                            tmpGearbox = infoArr[0] + "CVT";
                            break;
                        case "挡E-CVT无级变速":
                            tmpGearbox = infoArr[0] + "ECVT";
                            break;
                        case "挡电动车单速变速箱":
                            logger.warn("unknow gearbox: " + this.gearbox);
                            tmpGearbox = "";
//                            tmpGearbox = infoArr[0] + "UNKNOWN";
                            break;
                        default:
                            throw new RuntimeException("unknow gearbox " + infoArr[1]);
                    }
                }
            }

            String tmpPrice;
            if ("暂无".equals(this.price)) {
                tmpPrice = "0";
            } else {
                String[] priceArr = regexService.getMultiGroup(this.price, "(\\d+(?:\\.\\d+)?)(.*)");
                switch (priceArr[1]) {
                    case "万":
                        tmpPrice = new BigDecimal(priceArr[0]).multiply(new BigDecimal(10000)).toString();
                        break;
                    default:
                        throw new RuntimeException("unknow price " + this.price);
                }
            }
            String tmpYear = regexService.getSingleGroup(this.year, "(\\d+)");
            return new StyleInfo(tmpYear, this.style, tmpGearbox, tmpPrice);
        }
    }
}
