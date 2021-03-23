package com.amituofo.xfs.plugin.fs.sftp.item;

import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.sftp.SFTPClientPool;
import com.amituofo.xfs.plugin.fs.sftp.SFTPFileSystemEntry;
import com.amituofo.xfs.plugin.fs.sftp.SFTPFileSystemPreference;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemspaceBase;
import com.jcraft.jsch.ChannelSftp;

public class SFTPRootspace extends ItemspaceBase<SFTPFileSystemEntry, SFTPFileSystemPreference> {

	protected final SFTPClientPool pool;

	public SFTPRootspace(SFTPFileSystemEntry entry, SFTPClientPool pool) {
		super(entry);
		this.pool = pool;
	}

	@Override
	public String getName() {
		return entry.getRootPath();
	}

	protected FolderItem createHomeFolder() {
		String pwd = null;
		ChannelSftp ftpClient = null;
		try {
			ftpClient = pool.borrowObject();
			pwd = ftpClient.pwd();
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
		FileItem item = new SFTPFileItem(this);

		// String targetKey = folderPath.replace('\\', '/');
		// ((ItemInnerFunc) item).setName(URLUtils.getLastNameFromPath(filepath));
		((ItemHiddenFunction) item).setPath(fullpath);
		// item.setParent(this.getParent());

		return item;
	}

	@Override
	public FolderItem newFolderItemInstance(String fullpath) {
		FolderItem item = new SFTPFolderItem(this);

		// String targetKey = folderPath.replace('\\', '/');
		// ((ItemInnerFunc) item).setName(URLUtils.getLastNameFromPath(filepath));
		((ItemHiddenFunction) item).setPath(fullpath);
		// item.setParent(this.getParent());

		return item;
	}

	public SFTPClientPool getClientPool() {
		return pool;
	}

}
