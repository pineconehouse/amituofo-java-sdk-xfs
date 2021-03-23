package com.amituofo.xfs.plugin.fs.ftp.item;

import org.apache.commons.net.ftp.FTPClient;

import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.ftp.FTPClientPool;
import com.amituofo.xfs.plugin.fs.ftp.FTPFileSystemEntry;
import com.amituofo.xfs.plugin.fs.ftp.FTPFileSystemPreference;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemspaceBase;

public class FTPRootspace extends ItemspaceBase<FTPFileSystemEntry, FTPFileSystemPreference> {

	protected final FTPClientPool pool;

	public FTPRootspace(FTPFileSystemEntry entry, FTPClientPool pool) {
		super(entry);
		this.pool = pool;
	}

	@Override
	public String getName() {
		return entry.getRootPath();
	}

	protected FolderItem createHomeFolder() {
		String pwd = null;
		FTPClient ftpClient = null;
		try {
			ftpClient = pool.borrowObject();
			pwd = ftpClient.printWorkingDirectory();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.returnObject(ftpClient);
		}

		if (StringUtils.isEmpty(pwd)) {
			pwd = entry.getRootPath();
		}

		FolderItem root = newFolderItemInstance(pwd);
		((ItemHiddenFunction) root).setName(URLUtils.getLastNameFromPath(pwd));
		return root;
	}

	@Override
	public FileItem newFileItemInstance(String fullpath) {
		FileItem item = new FTPFileItem(this);

		// String targetKey = folderPath.replace('\\', '/');
		// ((ItemInnerFunc) item).setName(URLUtils.getLastNameFromPath(filepath));
		((ItemHiddenFunction) item).setPath(fullpath);
		// item.setParent(this.getParent());

		return item;
	}

	@Override
	public FolderItem newFolderItemInstance(String fullpath) {
		FolderItem item = new FTPFolderItem(this);

		// String targetKey = folderPath.replace('\\', '/');
		// ((ItemInnerFunc) item).setName(URLUtils.getLastNameFromPath(filepath));
		((ItemHiddenFunction) item).setPath(fullpath);
		// item.setParent(this.getParent());

		return item;
	}

	public FTPClientPool getClientPool() {
		return pool;
	}

}
