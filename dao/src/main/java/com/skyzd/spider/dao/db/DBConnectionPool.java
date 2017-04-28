package com.skyzd.spider.dao.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.impl.NewProxyConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionPool {
	private static Logger logger = LoggerFactory.getLogger(DBConnectionPool.class);
	private static DBConnectionPool pool;
	private static DBConnectionPool readOnlypool ;
	private static DBConnectionPool tuanpool ;
	private ComboPooledDataSource cpds = null;
	private boolean isReadOnly = false;

	private DBConnectionPool(boolean readOnly, boolean isTuan) {
		this.isReadOnly = readOnly;
		try {
			cpds = new ComboPooledDataSource();
			cpds.setDriverClass(Config.getString("mysql.jdbc.driver"));
			cpds.setAcquireIncrement(Config.getInt("acquireIncrement",2));// 连接池在无空闲连接可用时一次性创建的新数据库连接数
			cpds.setMinPoolSize(Config.getInt("minConnection", 3));
			cpds.setMaxPoolSize(Config.getInt("maxConnection", 10));
			cpds.setCheckoutTimeout(Config.getInt("connectionTimeout", 300000)); 
			cpds.setMaxIdleTime(Config.getInt("maxConnectionAgeInSeconds", 3600)); 
			cpds.setIdleConnectionTestPeriod(Config.getInt("idleConnectionTestPeriod", 14400));                                
			
			if(isTuan){//配置团购数据库连接池
				cpds.setJdbcUrl(Config.getString("mysql.tuan.jdbc.url"));
				cpds.setUser(Config.getString("mysql.tuan.jdbc.username"));
				cpds.setPassword(Config.getString("mysql.tuan.jdbc.password"));
			}else {
				if (this.isReadOnly()) {
					cpds.setJdbcUrl(Config.getString("mysql.read.jdbc.url"));
					cpds.setUser(Config.getString("mysql.read.jdbc.username"));
					cpds.setPassword(Config.getString("mysql.read.jdbc.password"));
				} else { // 写数据库
					cpds.setJdbcUrl(Config.getString("mysql.write.jdbc.url"));
					cpds.setUser(Config.getString("mysql.write.jdbc.username"));
					cpds.setPassword(Config.getString("mysql.write.jdbc.password"));
				}
			}
		} catch (Exception e) {
			logger.error("inistall db connection pool failed");
			logger.error(e.getMessage(), e);
			System.exit(0);
		}
/*
		// 注册JVM退出
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() { // 关闭数据库连接池
				if(cpds!=null)
					cpds.close();
				logger.debug("jvm exits and close connectionPool");
			}
		});*/
	}

	public synchronized Connection getConnection() throws SQLException {
		Connection conn = null;
		try {
			conn = this.cpds.getConnection();
			conn.setAutoCommit(true);
			if (isReadOnly)
				conn.setReadOnly(true);
		} catch (Exception e) {
			// 如果链接有异常，关闭，并重新获取新的链接
			logger.error("connection using:"
					+ this.cpds.getNumBusyConnections()
					+ " , connection free:"
					+ this.cpds.getNumIdleConnections()
					+ " , connection Created:"
					+ this.cpds.getNumConnections(), e);
			if (conn != null) {
				NewProxyConnection connhan = (NewProxyConnection) conn;
				connhan.close();
			}
			conn = this.cpds.getConnection();
		}
		return conn;
	}

	public void release(Connection conn) throws SQLException {
		conn.close();
	}

	public static DBConnectionPool getInstance() {
		if(null==pool){
			synchronized (DBConnectionPool.class) {  
				if(null==pool){
					pool =  new DBConnectionPool(false,false);
				}
			}
		}
		return pool;
	}

	public static DBConnectionPool getReadOnlyInstacne() {
		if(null==readOnlypool){
			synchronized (DBConnectionPool.class) {  
				if(null==readOnlypool){
					readOnlypool = new DBConnectionPool(true,false);
				}
			}
		}
		return readOnlypool;
	}
	
	public static DBConnectionPool getTuanInstacne() {
		if(null==tuanpool){
			synchronized (DBConnectionPool.class) {  
				if(null==tuanpool){
					tuanpool = new DBConnectionPool(false,true);
				}
			}
		}
		return tuanpool;
	}

	public void destroy() {
		pool = null;
		readOnlypool = null;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public ComboPooledDataSource getCpds() {
		return cpds;
	}
	
	

}
