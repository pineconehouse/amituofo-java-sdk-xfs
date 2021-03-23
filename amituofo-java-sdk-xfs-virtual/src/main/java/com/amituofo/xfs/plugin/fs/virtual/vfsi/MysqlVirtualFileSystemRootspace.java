package com.amituofo.xfs.plugin.fs.virtual.vfsi;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.amituofo.xfs.plugin.fs.virtual.VirtualFileSystemEntry;
import com.amituofo.xfs.plugin.fs.virtual.VirtualFileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.virtual.vfs.JdbcVirtualFSIConnPool;
import com.amituofo.xfs.plugin.fs.virtual.vfs.MysqlVirtualFSI;

public class MysqlVirtualFileSystemRootspace extends JdbcVirtualFileSystemRootspace {

	public MysqlVirtualFileSystemRootspace(VirtualFileSystemEntry fileSystemEntry, String spacename) {
		super(fileSystemEntry, spacename);
	}

	@Override
	protected DataSource createDataSource(VirtualFileSystemEntryConfig config, String dbname) throws SQLException {
//		String host = "localhost";// "192.168.3.21";//virtualFileSystemEntryConfig.getHost();
		// String user = virtualFileSystemEntryConfig.getUser();
		// String pwd = virtualFileSystemEntryConfig.getPassword();
		String URL = config.getDatabaseUrl();
		// "jdbc:mysql://"+host+":3306/" + dbname + "?characterEncoding=UTF-8&useSSL=false";
		// "com.mysql.jdbc.Driver"
		DataSource ds ;
		Connection conn = null;
		try {
			ds = JdbcVirtualFSIConnPool.create(dbname, config.getDatabaseDriver(), URL, config.getUser(), config.getPassword(), 50);

			conn = ds.getConnection();
		} catch (Exception e) {
			// e.printStackTrace();
			JdbcVirtualFSIConnPool.close(dbname);
//			URL = "jdbc:mysql://" + host + ":3306/?characterEncoding=UTF-8&useSSL=false";
//			ds = JdbcVirtualFSIConnPool.create(dbname, "com.mysql.jdbc.Driver", URL, "root", "1", 50);
//			conn = ds.getConnection();
//			createddb(conn, dbname);
			throw new SQLException(e);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

		return ds;
	}

	@Override
	protected VirtualFSI createVirtualFSI(String folderId, VirtualFileSystemEntry fileSystemEntry2, String folderPath) {
		MysqlVirtualFSI vf = new MysqlVirtualFSI(folderId, fileSystemEntry, folderPath, dbname, JdbcVirtualFSIConnPool.getDataSource(dbname));
		// JimfsVirtualFSI vf = new JimfsVirtualFSI(folderId, fileSystemEntry, folderPath, dbname, JdbcVirtualFSIConnPool.getDataSource(dbname));

		return vf;
	}

}
