package com.skyzd.framework.log;

import com.skyzd.framework.common.ReflectionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.LoggerRegistry;
import org.apache.logging.slf4j.Log4jLoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.*;

/**
 * Created by sky.chi on 4/13/2017 4:31 PM.
 * Email: sky8chi@gmail.com
 */
public class Logger {
    private static org.slf4j.Logger logger;
    private static final String FQCN = Logger.class.getName();

    static {
        try {
            Enhancer eh = new Enhancer();
            eh.setSuperclass(org.apache.logging.log4j.core.Logger.class);
            eh.setCallbackType(LogInterceptor.class);
            Class c = eh.createClass();
            Enhancer.registerCallbacks(c, new LogInterceptor[]{new LogInterceptor()});
            Constructor<org.apache.logging.log4j.core.Logger> constructor = c.getConstructor(LoggerContext.class, String.class, MessageFactory.class);
            org.apache.logging.log4j.core.Logger loggerProxy = constructor.newInstance(LogManager.getContext(), Logger.class.getName(), null);

            Log4jLoggerFactory loggerFactory = (Log4jLoggerFactory)LoggerFactory.getILoggerFactory();

            Method getContentMethod = Log4jLoggerFactory.class.getDeclaredMethod("getContext");
            getContentMethod.setAccessible(true);
            LoggerContext loggerContext = (LoggerContext)getContentMethod.invoke(loggerFactory);
//            Field LoggerRegistry<org.apache.logging.log4j.core.Logger> loggerRegistry;
//            Field loggerRegistryField = loggerContext.getClass().getField("loggerRegistry");
            LoggerRegistry<org.apache.logging.log4j.core.Logger> loggerRegistry = ReflectionUtil.getFieldValue(loggerContext, "loggerRegistry");
            LoggerRegistry tmpLloggerRegistry = new LoggerRegistry();
            loggerRegistry.putIfAbsent(Logger.class.getName(), null, loggerProxy);
            ReflectionUtil.setFieldValue(loggerContext, "loggerRegistry", tmpLloggerRegistry);
            logger = loggerFactory.getLogger(Logger.class.getName());
            ReflectionUtil.setFieldValue(loggerContext, "loggerRegistry", loggerRegistry);
//            StaticLoggerBinder binder = StaticLoggerBinder.getSingleton();
//            Log4jLoggerFactory iLoggerFactory = ReflectionUtil.getFieldValue(binder, "loggerFactory");
//            Object loggerFactoryProxy = Proxy.newProxyInstance(
//                    Log4jLoggerFactory.class.getClassLoader(),
//                    new Class[]{Log4jLoggerFactory.class},
//                    new NewLoggerHandler(loggerProxy)
//            );
//            LoggerRepository loggerRepository = LogManager.getLoggerRepository();
//            org.apache.log4j.spi.LoggerFactory lf = ReflectionUtil.getFieldValue(loggerRepository, "defaultFactory");
//            Object loggerFactoryProxy = Proxy.newProxyInstance(
//                    LoggerFactory.class.getClassLoader(),
//                    new Class[]{LoggerFactory.class},
//                    new NewLoggerHandler(loggerProxy)
//            );

//            ReflectionUtil.setFieldValue(loggerRepository, "defaultFactory", loggerFactoryProxy);
//            logger = org.slf4j.LoggerFactory.getLogger(Logger.class.getName());
//            ReflectionUtil.setFieldValue(loggerRepository, "defaultFactory", lf);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("初始化Logger失败", e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private static class LogInterceptor implements MethodInterceptor {
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            // 只拦截log方法。
            if (!method.getName().equals("logIfEnabled"))
                return methodProxy.invokeSuper(o, objects);
            objects[0] = FQCN;
            return methodProxy.invokeSuper(o, objects);
        }
    }

    private static class NewLoggerHandler implements InvocationHandler {
        private final org.apache.logging.log4j.core.Logger proxyLogger;

        public NewLoggerHandler(org.apache.logging.log4j.core.Logger proxyLogger) {
            this.proxyLogger = proxyLogger;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return proxyLogger;
        }
    }

    // 剩下的Logger需要封装的方法可以根据自己的需要来实现
    // 我个人认为slf4j的api足够好用了，所以大部分只是写了一些类似下面的代码
    public static void info(String msg) {
        logger.info(msg);
    }
}
