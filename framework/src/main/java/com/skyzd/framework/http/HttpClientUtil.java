package com.skyzd.framework.http;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by sky.chi on 4/12/2017 11:14 PM.
 * Email: sky8chi@gmail.com
 */
public class HttpClientUtil {

    public static String get(String url) throws HttpException {
        return get(url, null, null);
    }

    public static String get(String url, Parameters parameters, HttpConfig httpConfig) throws HttpException {
        return doMethod(url, parameters, httpConfig, new MethodTypeNew() {
            public HttpRequestBase gen(String url, Cloneable parametersOrStringEntity) throws HttpException {
                Parameters parameters = (Parameters) parametersOrStringEntity;
                String reqUrl = url;
                if (parameters != null) {
                    String params = null;
                    try {
                        params = parameters.paramsAsString();
                        reqUrl = HttpUtil.buildGetUrl(url, params);
                    } catch (IOException e) {
                        throw new HttpException("url error: {0} {1}", url, params);
                    }
                }
                return new HttpGet(reqUrl);
            }
        });
    }

    public static String postData(String url, String data, HttpConfig httpConfig) throws HttpException {
        final StringEntity entity = data != null ? new StringEntity(data, Consts.UTF_8.displayName()) : null;
        return doMethod(url, entity, httpConfig, new MethodTypeNew() {
            public HttpRequestBase gen(String url, Cloneable parametersOrStringEntity) throws HttpException {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity((StringEntity)parametersOrStringEntity);
                return httpPost;
            }
        });
    }

    public static String post(String url, Parameters parameters, HttpConfig httpConfig) throws HttpException {
        return doMethod(url, parameters, httpConfig, new MethodTypeNew() {
            public HttpRequestBase gen(String url, Cloneable parametersOrStringEntity) throws HttpException {
                Parameters parameters = (Parameters)parametersOrStringEntity;
                final StringEntity entity;
                List<BasicNameValuePair> nameValuePairLst = new ArrayList<BasicNameValuePair>();
                if (parameters != null) {
                    Set<String> params = parameters.getParameterNames();
                    for (String param : params) {
                        String[] values = parameters.getParameterValues(param);
                        for (String value : values) {
                            BasicNameValuePair nameValuePair = new BasicNameValuePair(param, value);
                            nameValuePairLst.add(nameValuePair);
                        }
                    }
                }
                try {
                    entity = new UrlEncodedFormEntity(nameValuePairLst, Consts.UTF_8.displayName());
                } catch (UnsupportedEncodingException e) {
                    throw new HttpException("url encode form entity {0}", parameters.toString());
                }
                HttpPost post = new HttpPost(url);
                post.setEntity(entity);
                return post;
            }
        });
    }

    private static String doMethod(String url, Cloneable parametersOrStringEntity, HttpConfig httpConfig
            ,  MethodTypeNew methodType) throws HttpException {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpRequestBase method = methodType.gen(url, parametersOrStringEntity);
        if (httpConfig != null) {
            RequestConfig requestConfig = httpConfig.getRequestConfig();
            method.setConfig(requestConfig);
        }
        String body = null;
        CloseableHttpResponse response = null;
        try {
            HttpContext context = new BasicHttpContext();
            response = client.execute(method, context);
            body = EntityUtils.toString(response.getEntity(), Consts.UTF_8.displayName());
        } catch (IOException e) {
        } catch (Exception e) {
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e1) {
            }
            try {
                client.close();
            } catch (Exception e) {
            }
        }
        return body;
    }

    private interface MethodTypeNew {
        HttpRequestBase gen(String url, Cloneable parametersOrStringEntity) throws HttpException;
    }
}
