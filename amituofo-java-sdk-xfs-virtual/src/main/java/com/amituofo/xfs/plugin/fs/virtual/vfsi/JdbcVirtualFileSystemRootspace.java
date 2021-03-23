package com.amituofo.xfs.plugin.fs.virtual.vfsi;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.amituofo.xfs.plugin.fs.virtual.VirtualFileSystemEntry;
import com.amituofo.xfs.plugin.fs.virtual.VirtualFileSystemEntryConfig;

public abstract class JdbcVirtualFileSystemRootspace extends DefaultVirtualFileSystemRootspace {

	protected final String dbname;

	public JdbcVirtualFileSystemRootspace(VirtualFileSystemEntry fileSystemEntry, String spacename) {
		super(fileSystemEntry, spacename);
		dbname = fileSystemEntry.getName() + "_" + spacename;
	}

	@Override
	public void init() throws VirtualFileSystemException {
		try {
			DataSource ds = createDataSource(fileSystemEntry.getEntryConfig(), dbname);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new VirtualFileSystemException(e);
		}
	}

	protected void createddb(Connection conn, String dbname) throws SQLException {
		Statement stat = null;
		try {
			stat = conn.createStatement();
			stat.execute("CREATE DATABASE " + dbname);
		} finally {
			try {
				stat.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	protected abstract DataSource createDataSource(VirtualFileSystemEntryConfig virtualFileSystemEntryConfig, String dbname) throws SQLException;

}
