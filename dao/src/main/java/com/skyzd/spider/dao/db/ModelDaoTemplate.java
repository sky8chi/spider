package com.skyzd.spider.dao.db;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class ModelDaoTemplate {
	private static Logger logger = LoggerFactory.getLogger(ModelDaoTemplate.class);
	private String sql;
	private Connection conn;
	private List<Object> params;

	public ModelDaoTemplate(Connection conn, String sql, List<Object> params) {
		this.sql = sql;
		this.conn = conn;
		this.params = params;
	}
	
	public Object execute(ModelDaoCallback callback){
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			if(null!=params&&params.size()>0){
				setParams(pstmt,params);
			}
			rs = pstmt.executeQuery();
			Object result = callback.doExecute(rs);
			return result;
		} catch (Throwable e) {
			logger.error("exec sql:{"+sql+"} occured error:",e);
			e.printStackTrace();
		} finally {
			JdbcDaoSupport.close(rs, pstmt, conn);
		}
		return null;
	}
	private static void setParams(PreparedStatement pstmt, List<Object> params){
		for(int i=0; i<params.size(); i++){
			try {
				pstmt.setObject(i+1, params.get(i));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
