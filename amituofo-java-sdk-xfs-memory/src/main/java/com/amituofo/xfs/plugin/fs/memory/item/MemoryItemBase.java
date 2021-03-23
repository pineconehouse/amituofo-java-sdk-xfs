package com.amituofo.xfs.plugin.fs.memory.item;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.memory.MemoryFileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.memory.filesystem.MemoryFileSystem;
import com.amituofo.xfs.plugin.fs.memory.filesystem.MemoryFileSystemException;
import com.amituofo.xfs.service.FileSystem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemBase;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;

public abstract class MemoryItemBase extends ItemBase<MemoryItemspace> implements FileSystem {
	public static final char SEPARATOR_CHAR = '/';
	protected final MemoryFileSystem memfs;

	public MemoryItemBase(MemoryItemspace itemspace, MemoryFileSystem memfs) {
		super(itemspace);
		this.memfs = memfs;
	}

	public MemoryFileSystem getMemoryFileSystem() {
		return memfs;
	}

	@Override
	public void rename(String newfilename) throws ServiceException {
		try {
			String newfilepath;

			String parentPath = URLUtils.getParentPath(this.getPath(), this.getPathSeparator(), getFileSystemEntry().getRootPath());
			newfilepath = URLUtils.catPath(parentPath, newfilename, itemspace.getFileSystemEntry().getSeparatorChar());

			memfs.rename(this.getPath(), newfilepath);
		} catch (MemoryFileSystemException e) {
			e.printStackTrace();
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

//	@Override
//	public FolderItem getParent() {
//		String parentPath = URLUtils.getParentPath(this.getPath());
//
//		if (parentPath == null) {
//			return null;
//		}
//
//		// VirtualFSI parentMemVirtualFSI = fileSystemEntry.getVirtualFileSystem().getVirtualFSI(parentPath, ItemType.Directory);
//		// VirtualFolderItem parent = new VirtualFolderItem(rootitem, fileSystemEntry, preference, parentMemVirtualFSI);
//		FolderItem parent = ((ItemInstanceCreator) itemspace).newFolderItemInstance(parentPath);
//		((ItemHiddenFunction) parent).setName(URLUtils.getLastNameFromPath(parentPath));
//		// ((ItemInnerFunc) parent).setPath(parentPath);
//
//		return parent;
//	}

	@Override
	public boolean isSame(Item item) {
		if (!(isFromSameSystem(item))) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isFromSameSystem(Item item) {
		if (!(item instanceof MemoryItemBase)) {
			return false;
		}

		return true;
	}

	@Override
	public String getSystemName() {
		return MemoryFileSystemEntryConfig.SYSTEM_NAME;
	}

	// @Override
	// public String[] getSupportVersion() {
	// return new String[] { "" };
	// }

}
