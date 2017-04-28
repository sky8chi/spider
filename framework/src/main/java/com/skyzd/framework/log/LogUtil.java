package com.skyzd.framework.log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by sky.chi on 4/13/2017 9:30 AM.
 * Email: sky8chi@gmail.com
 */
public class LogUtil {
    private static Logger logger = LoggerFactory.getLogger(LogUtil.class);
//    static {
//        try {
//            Field field = logger.getClass().getDeclaredField("FQCN");
//            field.setAccessible(true);
//            Field modifiersField = Field.class.getDeclaredField("modifiers");
//            modifiersField.setAccessible(true);
//            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
//            field.set(logger, LogUtil.class.getName());
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }
    public static void info(String msg) {
        log(Level.INFO, msg);
    }
    private static void log(Level level, Object message) {
//        	logger.log(level, message); not match method class
            logger.info(LogUtil.class.getName(),level, message,null);
    }
}
