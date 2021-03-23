package com.amituofo.xfs.plugin.fs.virtual.vfs;

import java.io.File;

import javax.sql.DataSource;

import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.virtual.VirtualFileSystemEntry;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFileSystemException;

public class H2VirtualFSI extends JdbcVirtualFSI {

	private String dblocation;

	public H2VirtualFSI(String fsid, VirtualFileSystemEntry fileSystemEntry, String folderPath, String location, DataSource ds) {
		super(fsid, fileSystemEntry, folderPath, null, ds);

		if (StringUtils.isEmpty(location)) {
			location = "./data";
		}

		this.dblocation = URLUtils.catPath(location, fsid, File.separatorChar);

		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// @Override
	// protected Connection newConnection() throws VirtualFileSystemException {
	// try {
	// Connection conn = DriverManager.getConnection("jdbc:h2:" + dblocation, "sa", "");
	// conn.setAutoCommit(false);
	// return conn;
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new VirtualFileSystemException(e);
	// }
	// }

	@Override
	public boolean renameTo(String newFoldername) {
		// if (super.renameTo(newFoldername)) {
		// this.close();
		// File dbfile = new File(dblocation + ".mv.db");
		// String newFolderPath = URLUtils.catPath(thisFolderItem.getParent().getPath(), newFoldername, memFileSystemEntry.getSeparatorChar());
		// File newdbfile = new File(dbfile.getParent() + File.separator + virtualFileSystem.toFolderId(newFolderPath) + ".mv.db");
		// return dbfile.renameTo(newdbfile);
		// }
		return false;
	}

	@Override
	public boolean remove() throws VirtualFileSystemException {
		if (super.remove()) {
			return new File(dblocation).delete();
		}
		return false;
	}

}
