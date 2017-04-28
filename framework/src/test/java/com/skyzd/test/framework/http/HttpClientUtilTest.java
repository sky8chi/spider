package com.skyzd.test.framework.http;

import com.skyzd.framework.http.HttpClientUtil;
import com.skyzd.framework.http.HttpConfig;
import com.skyzd.framework.http.Parameters;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.junit.Test;
import org.junit.runner.Request;

import static org.junit.Assert.*;

/**
 * Created by sky.chi on 4/15/2017 10:14 AM.
 * Email: sky8chi@gmail.com
 */
public class HttpClientUtilTest {
    @Test
    public void get() throws Exception {
        Parameters parameters = new Parameters();
        parameters.add4Encode("a", "bbb").add4Encode("b", "中国");
        HttpConfig httpConfig = HttpConfig.custom().setRequestConfig(RequestConfig.DEFAULT).build();
    }

    @Test
    public void post() throws Exception {

    }

}