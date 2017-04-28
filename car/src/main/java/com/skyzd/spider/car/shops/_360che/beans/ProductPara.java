package com.skyzd.spider.car.shops._360che.beans;

import com.google.common.base.Strings;
import com.skyzd.framework.regex.IRegexService;
import com.skyzd.framework.regex.RegexService;
import com.skyzd.spider.car.shops._360che.grab._360cheGrab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by sky.chi on 4/23/2017
 * 1:44 PM.
 * Email: sky8chi@gmail.com
 */
public class ProductPara {
    private String model;
    private String serie;
    private String form;
    private String status;

    public ProductPara(String model, String serie, String form, String status) {
        this.model = model;
        this.serie = serie;
        this.form = form;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getModel() {
        return model;
    }

    public String getSerie() {
        return serie;
    }

    public String getForm() {
        return form;
    }

    public static Builder custom() {
        return new ProductPara.Builder();
    }

    public static class Builder {
        private Logger logger = LoggerFactory.getLogger(Builder.class);
        private String model;
        private String serie;
        private String gears;
        private String form;
        private String form2;
        private String status;
        private String url;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setModel(String model) {
            this.model = model;
            return this;
        }

        public Builder setSerie(String serie) {
            this.serie = serie;
            return this;
        }

        public Builder setGears(String gears) {
            this.gears = gears;
            return this;
        }

        public Builder setForm(String form) {
            this.form = form;
            return this;
        }

        public Builder setForm2(String form2) {
            this.form2 = form2;
            return this;
        }

        public ProductPara build() {
            IRegexService regexService = new RegexService();
            String tmpModel = regexService.getSingleGroup(this.model, "([0-9A-Za-z]+)");
            String tmpSeries = regexService.getSingleGroup(this.serie, "([0-9A-Za-z]+)系列");
            if (Strings.isNullOrEmpty(tmpSeries)) {
                tmpSeries = regexService.getSingleGroup(this.serie, "([0-9A-Za-z]+)");
            }
            String tmpGears = regexService.getSingleGroup(this.gears, "(\\d+)");
            String tmpForm = getForm(this.form);
            if (tmpForm == null) {
                tmpForm = getForm(this.form2);
            }
            if (tmpForm == null) {
                logger.warn("unknow form: " + this.form + "; form2: "+ this.form2 + "; url: " + this.url);
            }

            String tmpStatus;
            switch(this.status) {
                case "在售": tmpStatus = "1"; break;
                case "停售": tmpStatus = "2"; break;
                case "未上市": tmpStatus = "3"; break;
                default:
                    throw new RuntimeException("unknow status: " + this.status);
            }
            return new ProductPara(tmpModel, tmpSeries, tmpForm == null ? "" : tmpGears + tmpForm, tmpStatus);
        }
        private String getForm(String form) {
            if (Strings.isNullOrEmpty(form)) {
                return null;
            }
            String tmpForm;
            switch(form) {
                case "手动":
                case "手动档":
                case "智能手动换档系统":
                case "液压手动换档系统":
                    tmpForm = "MT";break;
                case "自动":
                case "自动档":
                case "自动（AT）":
                case "自动(AT)":
                    tmpForm = "AT";break;
                case "手自一体":
                case "手自一体AMT":
                case "AMT手自一体":
                    tmpForm = "AMT";break;
                case "超速档":
//                    tmpForm = "UNKNOWN"; break;
                default:
                    tmpForm = null;
            }
            if (tmpForm == null) {
                if (form.contains("手动")){
                    tmpForm = "MT";
                } else if (form.contains("手自一体")) {
                    tmpForm = "AMT";
                } else if (form.contains("自动")) {
                    tmpForm = "AT";
                }
            }
            return tmpForm;
        }
    }
}
