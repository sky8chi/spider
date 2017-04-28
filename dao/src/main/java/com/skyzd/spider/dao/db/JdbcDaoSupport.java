package com.skyzd.spider.dao.db;


import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;


public class JdbcDaoSupport {
	private static Logger logger = LoggerFactory.getLogger(JdbcDaoSupport.class);
	@SuppressWarnings("unchecked")
	public static <T> List<T> getObjects(String sql, final Class<T> clazz, Object... params){
		List<T> objects = null;
		try {
			long stime= System.currentTimeMillis();
			Connection conn = DBConnectionPool.getReadOnlyInstacne().getConnection();
			ModelDaoTemplate modelDaoTemplate = new ModelDaoTemplate(conn,sql, Arrays.asList(params));
			objects = (List<T>)modelDaoTemplate.execute(new ModelDaoCallback(){
			public Object doExecute(ResultSet rs) throws Exception {
				String[] colNames = getColNames(rs);
				List<T> objects = new ArrayList<T>();
				Method[] ms = clazz.getMethods();
				while (rs.next()) {
					T object = clazz.newInstance();
					for (int i = 0; i < colNames.length; i++) {
						String colName = colNames[i];
						String methodName = "set" + colName.replaceFirst(colName.substring(0, 1),colName.substring(0, 1).toUpperCase());
						for (Method m : ms) {
							if (methodName.equals(m.getName())) {
								try {
									m.invoke(object, rs.getObject(colName));
								} catch (Exception e) {
									Class<?> clz = null;
									Field field = null;
									try {
										clz = rs.getObject(colName).getClass();
										field = clz.getField("TYPE");
									} catch(Exception e1) {
									}
									logger.error(methodName+"="+(field!=null?field:(clz!=null?clz.getName():null)),e);
								}
								break;
							}
						}
					}
					objects.add(object);
				}
				return objects;
			}
		  });
			String objs="";
			for(Object obj:params){
				objs+=obj.toString()+",";
			}
			logger.info("size:"+objects.size()+",cost:"+(System.currentTimeMillis()-stime)+",sql:"+sql+",params:"+objs);
		} catch (Throwable e) {
			logger.error("exec sql:{"+sql+"} occured error:",e);
			e.printStackTrace();
		} 
		return objects;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getObject(String sql, final Class<T> clazz, Object... params) {
		T object = null;
		try {
			Connection conn = DBConnectionPool.getReadOnlyInstacne().getConnection();
			ModelDaoTemplate modelDaoTemplate = new ModelDaoTemplate(conn,sql, Arrays.asList(params));
			object = (T)modelDaoTemplate.execute(new ModelDaoCallback(){
				public Object doExecute(ResultSet rs) throws Exception {
					T object = null;
					String[] colNames = getColNames(rs);
					Method[] ms = clazz.getMethods();
					if (rs.next()) {
						object = clazz.newInstance();
						for (int i = 0; i < colNames.length; i++) {
							String colName = colNames[i];
							String methodName = "set" + colName.replaceFirst(colName.substring(0, 1),colName.substring(0, 1).toUpperCase());
							for (Method m : ms) {
								if (methodName.equals(m.getName())) {
									try {
										m.invoke(object, rs.getObject(colName));
									} catch (Exception e) {
										logger.error(methodName,e);
									}
									break;
								}
							}
						}
					}
					return object;
				}
			});
		} catch (Exception e) {
			logger.error("exec sql:{"+sql+"} occured error:",e);
			e.printStackTrace();
		}
		return object;
	}
	
	private static String[] getColNames(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int count = rsmd.getColumnCount();
		String[] colNames = new String[count];
		for (int i = 1; i <= count; i++) {
			colNames[i - 1] = rsmd.getColumnLabel(i);
		}
		return colNames;
	}
	
	public static int execute(String sql, Object... params) {
		int result = 0;
		PreparedStatement pstmt = null;
		Connection con = null;
		try {
			con = DBConnectionPool.getInstance().getConnection();
			pstmt = con.prepareStatement(sql);
			int count = 0;
			for (Object param : params) {
				count++;
				pstmt.setObject(count, param);
			}
			pstmt.execute();
			result = pstmt.getUpdateCount();

		} catch (Throwable e) {
			logger.error("exec sql:{"+sql+"} occured error:",e);
			e.printStackTrace();
		} finally {
			close(null, pstmt, con);
		}
		return result;
	}
	
	/**
	 * 查询语句中结果只能是两列
	 * 
	 * @param sql
	 * @return
	 */
	public static Map<String, String> getMap(String sql, Object... params) {
		Connection con = null;
		Map<String, String> map = new LinkedHashMap<String, String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = DBConnectionPool.getReadOnlyInstacne().getConnection();
			pstmt = con.prepareStatement(sql);

			int count = 0;
			for (Object param : params) {
				count++;
				pstmt.setObject(count, param);
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String key = rs.getString(1);
				String value = rs.getString(2);
				if (!Strings.isBlank(key)) {
					map.put(key, value);
				}
				key = null;
				value = null;
			}

		} catch (SQLException e) {
			logger.error(sql, e);
			return null;
		} finally {
			close(rs, pstmt, con);
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getMapData(String sql, Object... params) {
		Map<String, String> map = null;
		try {
			Connection conn = DBConnectionPool.getReadOnlyInstacne().getConnection();
			ModelDaoTemplate modelDaoTemplate = new ModelDaoTemplate(conn,sql, Arrays.asList(params));
			map = (Map<String, String>)modelDaoTemplate.execute(new ModelDaoCallback(){
				public Object doExecute(ResultSet rs) throws Exception {
					Map<String, String> map = new HashMap<String, String>();
					while (rs.next()) {
						ResultSetMetaData rsMetaData = rs.getMetaData();
						int columnCount = rsMetaData.getColumnCount();
						for (int i = 1; i <= columnCount; i++) {
							map.put(rsMetaData.getColumnName(i), rs.getString(i));
						}
					}
					return map;
			  }
			});
		} catch (Throwable e) {
			logger.error("exec sql:{"+sql+"} occured error:",e);
			e.printStackTrace();
		} 
		return map;
	}
	
	public static Integer[] executeBatch(String sql, List<Object[]> list , int batchNum) {
		PreparedStatement pstmt = null;
		Connection con = null;
		List<Integer> results = new ArrayList<Integer>();
		try {
			con = DBConnectionPool.getInstance().getConnection();
			con.setAutoCommit(false);
			pstmt = con.prepareStatement(sql);
			for(int i=1;i<=list.size();i++){
				Object[] args = list.get(i-1);
				int count = 0;
				for (Object param : args) {
					count++;
					pstmt.setObject(count, param);
				}
				pstmt.addBatch();
				if(i%batchNum == 0){
					int[] rs = pstmt.executeBatch();
					con.commit();
					pstmt.clearBatch();
					for(int r:rs){
						results.add(r);
					}
				}
			}
			int[] rs = pstmt.executeBatch();
			for(int r:rs){
				results.add(r);
			}
			con.commit();
		} catch (Throwable e) {
			logger.error("exec sql:{"+sql+"} occured error:",e);
			e.printStackTrace();
		} finally {
//			logger.info("free db connetion~~");
			close(null, pstmt, con);
		}
		return results.toArray(new Integer[]{});
	}
	
	public static long executeAndgetGeneratedKey(String sql, Object... params) {
		long result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection con = null;
		try {
			con = DBConnectionPool.getInstance().getConnection();
			pstmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			int count = 0;
			for (Object param : params) {
				count++;
				pstmt.setObject(count, param);
			}
			pstmt.execute();
			rs = pstmt.getGeneratedKeys();
			if(rs.next())
				return rs.getLong(1);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("exec sql:{"+sql+"} occured error:",e);
		} finally {
			close(rs, pstmt, con);
		}
		return result;
	}
	/**
	 * 关闭
	 * 
	 * @param rs
	 * @param pstmt
	 * @param con
	 */
	public static void close(ResultSet rs, Statement pstmt, Connection con) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
			if (con != null) {
				DBConnectionPool.getInstance().release(con);
			}

		} catch (Throwable e) {
			logger.error("", e);
		}
	}
	
	public static String getString(String sql, Object... params) {
		Connection con = null;
		String result = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = DBConnectionPool.getReadOnlyInstacne().getConnection();
			pstmt = con.prepareStatement(sql);
			int count = 0;
			for (Object param : params) {
				count++;
				pstmt.setObject(count, param);
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
				break;
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			close(rs, pstmt, con);
		}
		return result;
	}
	
	/**
	 * 将查询的结果封装为List返回，每个元素是一个Map,对应于数据库的一条记录
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public static List<Map<String, String>> getMapForList(String sql, Object... params) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			con = DBConnectionPool.getInstance().getConnection();
			pstmt = con.prepareStatement(sql);
			int count = 0;
			for (Object param : params) {
				count++;			
				pstmt.setObject(count,param);	
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				ResultSetMetaData rsMetaData = rs.getMetaData();
				Map<String, String> map = new HashMap<String, String>();
				int columnCount = rsMetaData.getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					map.put(rsMetaData.getColumnName(i), rs.getString(i));
				}
				list.add(map);
			}
		} catch (SQLException e) {
			logger.error(sql, e);
			return list;
		} finally {
			close(rs, pstmt, con);
		}
		return list;
	}

}
