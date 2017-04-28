package com.skyzd.framework.http;

import org.apache.http.client.config.RequestConfig;

/**
 * Created by sky.chi on 4/15/2017 11:30 AM.
 * Email: sky8chi@gmail.com
 */
public class HttpConfig implements Cloneable  {
    private final RequestConfig requestConfig;
    public HttpConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }
    public static HttpConfig.Builder custom() {
        return new HttpConfig.Builder();
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    public static class Builder {
        private RequestConfig requestConfig;
        public HttpConfig.Builder setRequestConfig(RequestConfig requestconfig) {
            this.requestConfig = requestconfig;
            return this;
        }
        public HttpConfig build() {
            return new HttpConfig(this.requestConfig);
        }
    }
}
