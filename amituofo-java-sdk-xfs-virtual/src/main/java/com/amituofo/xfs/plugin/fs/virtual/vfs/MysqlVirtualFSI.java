package com.amituofo.xfs.plugin.fs.virtual.vfs;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.amituofo.xfs.plugin.fs.virtual.VirtualFileSystemEntry;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFileSystemException;

public class MysqlVirtualFSI extends JdbcVirtualFSI {

//	private final String URL;
//	private static final String USERNAME = "root";
//	private static final String PASSWORD = "1";

	public MysqlVirtualFSI(String fsid, VirtualFileSystemEntry fileSystemEntry, String folderPath, String dbname, DataSource ds) {
		super(fsid, fileSystemEntry, folderPath, dbname, ds);
		this.TABLE_NAME = dbname + ".FT_" + fsid;
//		this.URL = "jdbc:mysql://127.0.0.1:3306/" + dbname + "?characterEncoding=UTF-8&useSSL=false";

		// try {
		// Class.forName("com.mysql.jdbc.Driver");
		// } catch (ClassNotFoundException e) {
		// e.printStackTrace();
		// }
	}

	@Override
	protected void createTable() throws VirtualFileSystemException {
		try {
			createdb(this.getDbname());
		} catch (SQLException e) {
			throw new VirtualFileSystemException(e);
		}
		super.createTable();
	}

	protected void createdb(String dbname) throws SQLException {
		Statement stat = null;
		Connection conn = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();
			stat.execute("CREATE DATABASE IF NOT EXISTS " + dbname);
		} finally {
			closeAndCommit(conn, stat);
		}
	}
}
