package com.amituofo.xfs.plugin.fs.virtual.item;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.virtual.VirtualFileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFSI;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFileSystemException;
import com.amituofo.xfs.service.FileSystem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemBase;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;

public abstract class VirtualItemBase extends ItemBase<VirtualItemspace> implements FileSystem {
	public static final char SEPARATOR_CHAR = '/';

	protected final VirtualFSI virtualFSI;

	public VirtualItemBase(VirtualItemspace itemspace, VirtualFSI virtualFSI) {
		super(itemspace);
		this.virtualFSI = virtualFSI;
	}

	public VirtualFSI getVirtualFSI() {
		return virtualFSI;
	}

	@Override
	public void rename(String newfilename) throws ServiceException {
		try {
			boolean ok = ((VirtualItemBase) this.getParent()).getVirtualFSI().rename(this.getName(), newfilename, this.getType());
		} catch (VirtualFileSystemException e) {
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
		if (!(item instanceof VirtualItemBase)) {
			return false;
		}

		return true;
	}

	@Override
	public String getSystemName() {
		return VirtualFileSystemEntryConfig.SYSTEM_NAME;
	}

//	@Override
//	public String[] getSupportVersion() {
//		return new String[] { "" };
//	}

}
