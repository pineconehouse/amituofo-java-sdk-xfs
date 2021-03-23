package com.amituofo.xfs.plugin.fs.hdfs.item;

import java.io.IOException;

import org.apache.hadoop.fs.Path;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.hdfs.HadoopHDFileSystemEntryConfig;
import com.amituofo.xfs.service.FileSystem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemBase;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;

public abstract class HadoopHDFSItemBase extends ItemBase<HadoopHDFSItemspace> implements FileSystem {
	public static final char SEPARATOR_CHAR = '/';
	protected final org.apache.hadoop.fs.FileSystem hadoopfs;
	// protected FileStatus fileStatus;
	protected Path filepath;

	public HadoopHDFSItemBase(HadoopHDFSItemspace itemspace, org.apache.hadoop.fs.FileSystem hadoopfs) {
		super(itemspace);
		this.hadoopfs = hadoopfs;
	}

	public org.apache.hadoop.fs.FileSystem getHadoopFileSystem() {
		return hadoopfs;
	}

	public Path getFilePath() {
		if (filepath == null) {
			// if (fileStatus != null) {
			// filepath = fileStatus.getPath();
			// } else {
			filepath = new Path(this.getPath());
			// }
		}
		return filepath;
	}

	// protected void setFileStatus(FileStatus fileStatus) {
	// this.fileStatus = fileStatus;
	// }

	@Override
	public boolean delete() throws ServiceException {
		try {
			return hadoopfs.delete(this.getFilePath());
		} catch (IOException e) {
			e.printStackTrace();
			// return false;
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean exists() throws ServiceException {
		try {
			return hadoopfs.exists(this.getFilePath());
		} catch (IOException e) {
			e.printStackTrace();
			// return false;
			throw new ServiceException(e);
		}
	}

	@Override
	public void rename(String newfilename) throws ServiceException {
		try {
			String newfilepath;

			String parentPath = URLUtils.getParentPath(this.getPath(), this.getPathSeparator(), getFileSystemEntry().getRootPath());
			newfilepath = URLUtils.catPath(parentPath, newfilename, itemspace.getFileSystemEntry().getSeparatorChar());

			hadoopfs.rename(new Path(this.getPath()), new Path(newfilepath));
		} catch (IOException e) {
			// e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	@Override
	public int getStatus() {
		return 0;
	}

	@Override
	public char getPathSeparator() {
		return '/';
	}

	// @Override
	// public FolderItem getParent() {
	// String parentPath = URLUtils.getParentPath(this.getPath());
	//
	// if (parentPath == null) {
	// return null;
	// }
	//
	// // VirtualFSI parentMemVirtualFSI = fileSystemEntry.getVirtualFileSystem().getVirtualFSI(parentPath, ItemType.Directory);
	// // VirtualFolderItem parent = new VirtualFolderItem(rootitem, fileSystemEntry, preference, parentMemVirtualFSI);
	// FolderItem parent = ((ItemInstanceCreator) itemspace).newFolderItemInstance(parentPath);
	// ((ItemHiddenFunction) parent).setName(URLUtils.getLastNameFromPath(parentPath));
	// // ((ItemInnerFunc) parent).setPath(parentPath);
	//
	// return parent;
	// }

	@Override
	public boolean isSame(Item item) {
		if (!(isFromSameSystem(item))) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isFromSameSystem(Item item) {
		if (!(item instanceof HadoopHDFSItemBase)) {
			return false;
		}

		return true;
	}

	@Override
	public String getSystemName() {
		return HadoopHDFileSystemEntryConfig.SYSTEM_NAME;
	}

	// @Override
	// public String[] getSupportVersion() {
	// return new String[] { "" };
	// }

}
