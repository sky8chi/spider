package com.skyzd.framework.http;

import com.google.common.base.Strings;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by sky.chi on 4/15/2017 9:21 AM.
 * Email: sky8chi@gmail.com
 */
public class HttpUtil {
    public static String buildGetUrl(String strUrl, String query) throws IOException {
        URL url = new URL(strUrl);
        if (Strings.isNullOrEmpty(query)) {
            return strUrl;
        }
        StringBuilder urlSb = new StringBuilder(strUrl);
        if (Strings.isNullOrEmpty(url.getQuery())) {
            if (!strUrl.endsWith("?")) urlSb.append("?");
        } else {
            if (!strUrl.endsWith("&")) urlSb.append("&");
        }
        return urlSb.append(query).toString();
    }

    public static String getAbsoluteURL(String baseURI, String relativePath){
        String abURL=null;
        try {
            URI base=new URI(baseURI);//基本网页URI
            URI abs=base.resolve(relativePath);//解析于上述网页的相对URL，得到绝对URI
            URL absURL=abs.toURL();//转成URL
            abURL = absURL.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally{
            return abURL;
        }
    }
}
