package com.skyzd.test.framework.http;

import com.skyzd.framework.http.HttpUtil;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by sky.chi on 4/15/2017 9:42 AM.
 * Email: sky8chi@gmail.com
 */
public class HttpUtilTest {
    @Test
    public void buildGetUrl() throws Exception {
        assertEquals(HttpUtil.buildGetUrl("http://www.baidu.com", null), "http://www.baidu.com");
        assertEquals(HttpUtil.buildGetUrl("http://www.baidu.com?", null), "http://www.baidu.com?");
        assertEquals(HttpUtil.buildGetUrl("http://www.baidu.com", "aaa=key"), "http://www.baidu.com?aaa=key");
        assertEquals(HttpUtil.buildGetUrl("http://www.baidu.com?", "aaa=key"), "http://www.baidu.com?aaa=key");
        assertEquals(HttpUtil.buildGetUrl("http://www.baidu.com?aaa=key", "bbb=121"), "http://www.baidu.com?aaa=key&bbb=121");
        assertEquals(HttpUtil.buildGetUrl("http://www.baidu.com?aaa=key", "bbb=121"), "http://www.baidu.com?aaa=key&bbb=121");
    }
}