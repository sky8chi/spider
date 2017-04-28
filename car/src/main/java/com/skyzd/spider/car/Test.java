package com.skyzd.spider.car;


/**
 * Created by sky.chi on 4/12/2017.
 * Email: sky8chi@gmail.com
 */
public class Test {
//    private static Logger logger = LoggerFactory.getLogger(LogUtil.class);
    public static void main(String[] args) {
        System.setProperty("slf4j.detectLoggerNameMismatch", "true");
        System.out.println(System.getProperty("slf4j.detectLoggerNameMismatch"));
//        LogUtil.info("fdfdfdf");
//        logger.info("aaaaa");
        com.skyzd.framework.log.Logger.info("aaa");
    }
}
