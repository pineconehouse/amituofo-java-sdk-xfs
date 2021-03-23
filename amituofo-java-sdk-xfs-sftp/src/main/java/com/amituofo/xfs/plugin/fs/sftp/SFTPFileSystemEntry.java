package com.amituofo.xfs.plugin.fs.sftp;

import java.util.List;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.ftp.item.FTPFolderItem;
import com.amituofo.xfs.plugin.fs.sftp.item.SFTPRootspace;
import com.amituofo.xfs.service.FileSystemFeatures;
import com.amituofo.xfs.service.ItemspaceConfig;
import com.amituofo.xfs.service.impl.FileSystemEntryBase;

public class SFTPFileSystemEntry extends FileSystemEntryBase<SFTPFileSystemEntryConfig, SFTPFileSystemPreference, SFTPRootspace> {
	private SFTPClientPool pool = null;

	public SFTPFileSystemEntry(SFTPFileSystemEntryConfig entryConfig) {
		super(entryConfig);
	}

	public SFTPFileSystemEntry(SFTPFileSystemEntryConfig entryConfig, SFTPFileSystemPreference preference) {
		super(entryConfig, preference);
	}

	private synchronized SFTPClientPool getPool() throws ServiceException {
		if (pool == null) {
			SFTPClientFactory factory = new SFTPClientFactory((SFTPFileSystemEntryConfig) entryConfig);
			try {
				pool = new SFTPClientPool(factory);
			} catch (Exception e) {
				throw new ServiceException(e);
			}
		}

		return pool;
	}

	@Override
	protected SFTPRootspace createDefaultItemspace() throws ServiceException {
		SFTPClientPool pool = getPool();

		return new SFTPRootspace(this, pool);
	}

	@Override
	protected List<SFTPRootspace> listAccessibleItemspaces() throws ServiceException {
		return null;
	}

	// @Override
	// public FolderItem parsePath(FolderItem workingDirectory, String path) throws ServiceException {
	// FolderItem dir = workingDirectory == null ? getRoot() : (FolderItem) workingDirectory.getRoot().clone();
	//
	// path = path.replace('\\', '/');
	// if (path.charAt(path.length() - 1) != '/') {
	// path += "/";
	// }
	// ((ItemInnerFunc) dir).setName(URLUtils.getLastNameFromPath(path));
	// ((ItemInnerFunc) dir).setPath(path);
	// if (dir.exists()) {
	// // hcpdir.getS3Client().
	// // is Dir?
	// return dir;
	// } else {
	// throw new ServiceException("Path not exist or format incorrect!");
	// }
	// }

	// @Override
	// public FileItem linkFile(RootItem rootitem, String fullpath) throws ServiceException {
	// FTPClientPool ftppool = getPool();
	// FTPFileItem item = new FTPFileItem(entryConfig.getDefaultSection(), this, preference, ftppool);
	//
	// String targetKey = fullpath.replace('\\', '/');
	// item.setName(URLUtils.getRequestTargetName(targetKey));
	// item.setPath(targetKey);
	// item.setSize(null);
	// item.setCreateTime(null);
	// item.setLastUpdateTime(null);
	//
	// // root.setParent(null);
	//// item.setCatelog(null);
	//// item.setData(null);
	//
	// return item;
	// }
	//
	// @Override
	// public FolderItem linkFolder(RootItem rootitem, String fullpath) throws ServiceException {
	// FTPClientPool ftppool = getPool();
	// FTPFolderItem item = new FTPFolderItem(entryConfig.getDefaultSection(), this, preference, ftppool);
	//
	// String targetKey = fullpath.replace('\\', '/');
	// item.setName(URLUtils.getRequestTargetName(targetKey));
	// item.setPath(targetKey);
	//// item.setSize(null);
	//// item.setCreateTime(null);
	//// item.setLastUpdateTime(null);
	//
	// // root.setParent(null);
	//// item.setCatelog(null);
	//// item.setData(null);
	//
	// return item;
	// }

	@Override
	public char getSeparatorChar() {
		return FTPFolderItem.SEPARATOR_CHAR;
	}

	@Override
	public void close() throws ServiceException {
		if (pool != null) {
			SFTPClientPool ftppool;
			ftppool = getPool();
			ftppool.close();
			pool = null;
		}
	}

	@Override
	public boolean hasFeature(int featureId) {
		if (featureId == FileSystemFeatures.AUTO_CREATE_FOLDER) {
			return false;
		}

		return false;
	}

	@Override
	public void createItemSpace(ItemspaceConfig config) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyItemSpace(ItemspaceConfig itemspaceConfig) throws ServiceException {
		
	}
	
	@Override
	public void deleteItemSpace(String name) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

}
