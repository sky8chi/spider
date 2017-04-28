package com.skyzd.spider.dao.db;


import java.sql.ResultSet;

public interface ModelDaoCallback {
	public Object doExecute(ResultSet rs) throws Exception;
}
