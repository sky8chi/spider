
package com.skyzd.spider.dao.db;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**   
 *    
 * 创建时间：2011-10-10 上午11:27:50
 *    
 */

public class Config extends Thread {
	private static Logger logger = LoggerFactory.getLogger(Config.class);
	private static Properties p = new Properties();
	/**
	 * class类路径 
	 */
	public static String classPath = Config.class.getClassLoader().getResource("").getPath();
	public static String paramsFile = null;
	static{
		classPath = classPath.endsWith("/")?classPath:classPath+"/";
		File file = null;
		try{
			String os = System.getProperty("os.name");
			if(os.toLowerCase().startsWith("win")){
				linux = false;
			}else{
				linux = true;
			}
			paramsFile = "config/config.properties";
			String classPath = Config.class.getClassLoader().getResource("").getPath();
			file = new File(classPath+paramsFile);
			lastModified = file.lastModified();
			p.load(new FileInputStream(file));
			//new Config().start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/** 默认编码 */
	public static String DEFUALT_ENCODE = "utf-8";
	/** 机器路径分隔符 */
	public static final String SEPARTOR = System.getProperty("line.separator");
	/** statement 执行查询超时时间 单位是秒 0 is never */
	public static Integer QUERY_TIMEOUT = 20 * 60;
	/** 一次加载的数据条数 */
	public static final Integer DB_ROWSPERTIME = getInt("db.rowspertime", 10000);
	

	
	/**
	 * 最后更新时间
	 */
	private static long lastModified = 0;

	/**
	 * 是否是linux
	 */
	private static boolean linux;
	/**
	 * 获取属性的值
	 * @param key
	 * @return
	 */
	public static String getString(String key){
		String value =  p.getProperty(key);
		if(!Strings.isNullOrEmpty(value)){
			value = value.trim();
		}
		return value;
	}	
	
	public static String getString(String key, String defaultValue){
		String value = p.getProperty(key,defaultValue);
		if(!Strings.isNullOrEmpty(value)){
			value = value.trim();
		}
		return value;
	}
	
	public static Integer getInt(String key){
		return getInt(key,0);
	}
	
	public static Integer getInt(String key, Integer defaultValue){
		Integer result = 0;
		try{
			result = Integer.parseInt(p.getProperty(key, String.valueOf(defaultValue)));
		}catch(Exception e){
			logger.warn(key+":"+defaultValue +":"+e.getMessage());
		}
		return result;
	}
	
	public static Long getLong(String key){
		return getLong(key,0l);
	}
	
	public static Long getLong(String key, Long defaultValue){
		Long result = 0l;
		try{
			result = Long.parseLong(p.getProperty(key, String.valueOf(defaultValue)));
		}catch(Exception e){
			logger.warn(key+":"+defaultValue +":"+e.getMessage());
		}
		return result;
	}
	
	public static Float getFloat(String key){
		return getFloat(key,0.0f);
	}
	
	public static Float getFloat(String key, Float defaultValue){
		Float result = 0.0f;
		try{
			result = Float.parseFloat(p.getProperty(key, String.valueOf(defaultValue)));
		}catch(Exception e){
			logger.warn(key+":"+defaultValue +":"+e.getMessage());
		}
		return result;
	}
	
	public static Double getDouble(String key){
		return getDouble(key,0.0d);
	}
	
	public static Double getDouble(String key, Double defaultValue){
		Double result = 0d;
		try{
			result = Double.parseDouble(p.getProperty(key, String.valueOf(defaultValue)));
		}catch(Exception e){
			logger.warn(key+":"+defaultValue +":"+e.getMessage());
		}
		return result;
	}
	
	
	public static Boolean getBoolean(String key){
		return getBoolean(key,false);
	}
	
	public static Boolean getBoolean(String key, Boolean defaultValue){
		Boolean result = false;
		try{
			result = Boolean.parseBoolean(p.getProperty(key, String.valueOf(defaultValue)));
		}catch(Exception e){
			logger.warn(key+":"+defaultValue +":"+e.getMessage());
		}
		return result;
	}

	public static boolean isLinux() {
		return linux;
	}
	
}


