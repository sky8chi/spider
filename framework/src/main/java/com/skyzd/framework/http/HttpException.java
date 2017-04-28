package com.skyzd.framework.http;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Created by sky.chi on 4/15/2017 8:58 AM.
 * Email: sky8chi@gmail.com
 */
public class HttpException extends Exception {
    public HttpException(String msgPattern, Object... args) {
        super(new MessageFormat(msgPattern, Locale.CHINA).format(args));
    }
}
