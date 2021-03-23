package com.amituofo.xfs.plugin.fs.cifs.item;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.cifs.SmbFileSystemEntryConfig;
import com.amituofo.xfs.service.FileSystem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemBase;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;
import com.amituofo.xfs.service.ItemProperties;
import com.hierynomus.msfscc.fileinformation.FileAllInformation;
import com.hierynomus.smbj.share.DiskShare;

public abstract class SmbItemBase extends ItemBase<SmbSharespace> implements FileSystem {
	public static final char SEPARATOR_CHAR = '\\';

	public SmbItemBase(SmbSharespace itemspace) {
		super(itemspace);
	}

	public DiskShare getDiskShare() {
		return itemspace.getDiskShare();
	}

	@Override
	public int getStatus() {
		return 0;
	}

	@Override
	public char getPathSeparator() {
		return SEPARATOR_CHAR;
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
		if (!(item instanceof SmbItemBase)) {
			return false;
		}

		return true;
	}

	@Override
	public String getSystemName() {
		return SmbFileSystemEntryConfig.SYSTEM_NAME;
	}

	@Override
	public ItemProperties getProperties() throws ServiceException {
		ItemProperties p = super.getProperties();
		FileAllInformation info = getDiskShare().getFileInformation(this.getPath());

		p.add("extend:IndexNumber", StringUtils.nullToString(info.getInternalInformation().getIndexNumber(), "-"));
		p.add("extend:FileAttributes", StringUtils.nullToString(info.getBasicInformation().getFileAttributes(), "-"));
		p.add("extend:CurrentByteOffset", StringUtils.nullToString(info.getPositionInformation().getCurrentByteOffset(), "-"));
		p.add("extend:NumberOfLinks", StringUtils.nullToString(info.getStandardInformation().getNumberOfLinks(), "-"));
		p.add("extend:AllocationSize", StringUtils.nullToString(info.getStandardInformation().getAllocationSize(), "-"));
		p.add("extend:EndOfFile", StringUtils.nullToString(info.getStandardInformation().getEndOfFile(), "-"));
		p.add("extend:EaSize", StringUtils.nullToString(info.getEaInformation().getEaSize(), "-"));
		p.add("extend:AccessFlags", StringUtils.nullToString(info.getAccessInformation().getAccessFlags(), "-"));
		p.add("extend:AlignmentRequirement", StringUtils.nullToString(info.getAlignmentInformation().getAlignmentRequirement(), "-"));
		p.add("extend:Mode", StringUtils.nullToString(info.getModeInformation().getMode(), "-"));
		return p;
	}


}
