package com.amituofo.xfs.plugin.fs.virtual.vfs;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class JdbcVirtualFSIConnPool {
	public final static String DEFAULT="default";
	private final static Map<String, DataSource> dsmap = new HashMap<String, DataSource>();

	public static DataSource getDataSource(String dbname) {
		return dsmap.get(dbname);
	}

	public static DataSource create(String dbname, String driverName, String url, String user, String pwd, int maxActiveConn) {
		BasicDataSource ds = (BasicDataSource)dsmap.get(dbname);
		if (ds != null) {
			return ds;
		}

		ds = new BasicDataSource();

		// 设置连接池所需的驱动
		ds.setDriverClassName(driverName);
		// 设置链接数据库的URL
		ds.setUrl(url);
		// 设置连接数据库的用户名
		ds.setUsername(user);
		// 设置连接数据库的密码
		ds.setPassword(pwd);
		// 设置连接池的初始连接数
		ds.setInitialSize(2);
		// 设置连接池最多可有多少个活动连接数
		ds.setMaxActive(maxActiveConn < 3 ? 3 : maxActiveConn);
		// 设置连接池中最少有两个空闲的链接
		// ds.setMinTdle(2);
		
		ds.setDefaultAutoCommit(false);

		dsmap.put(dbname, ds);

		return ds;
	}
	
	public static void register(String name, DataSource ds) {
		dsmap.put(name, ds);
	}

	public static void close() {
		Collection<DataSource> vs = dsmap.values();
		for (DataSource ds : vs) {
			if (ds != null && ds instanceof BasicDataSource) {
				try {
					((BasicDataSource)ds).close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		dsmap.clear();
	}

	public static void close(String dbname) {
		DataSource ds = dsmap.get(dbname);
		if (ds != null && ds instanceof BasicDataSource) {
			try {
				((BasicDataSource)ds).close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				dsmap.remove(dbname);
			}
		}
	}

}
