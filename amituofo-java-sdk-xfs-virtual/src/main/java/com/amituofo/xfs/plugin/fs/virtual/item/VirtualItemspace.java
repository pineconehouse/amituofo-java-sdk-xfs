package com.amituofo.xfs.plugin.fs.virtual.item;

import com.amituofo.xfs.plugin.fs.virtual.VirtualFileSystemEntry;
import com.amituofo.xfs.plugin.fs.virtual.VirtualFileSystemPreference;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFSI;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemType;
import com.amituofo.xfs.service.ItemspaceBase;

public class VirtualItemspace extends ItemspaceBase<VirtualFileSystemEntry, VirtualFileSystemPreference> {
//	protected final VirtualFSI virtualFSI;
	private String name;

	public VirtualItemspace(VirtualFileSystemEntry fileSystemEntry, String name) {
		super(fileSystemEntry);
//		this.virtualFSI = virtualFSI;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public FolderItem newFolderItemInstance(String fullpath) {
		VirtualFSI parentMemVirtualFSI = entry.getVirtualFileSystem().getVirtualFSI(fullpath, ItemType.Directory);
		VirtualFolderItem folder = new VirtualFolderItem(this, parentMemVirtualFSI);
//		((ItemInnerFunc) parent).setName(URLUtils.getLastNameFromPath(fullpath));
		((ItemHiddenFunction) folder).setPath(fullpath);
		return folder;
	}

	@Override
	public FileItem newFileItemInstance(String fullpath) {
		VirtualFSI parentMemVirtualFSI = entry.getVirtualFileSystem().getVirtualFSI(fullpath, ItemType.File);
		
		VirtualFileItem file = new VirtualFileItem(this, parentMemVirtualFSI, null);
//		((ItemInnerFunc) parent).setName(URLUtils.getLastNameFromPath(fullpath));
		((ItemHiddenFunction) file).setPath(fullpath);
		return file;
	}

//	@Override
//	protected FolderItem createRootFolder() {
//		return newFolderItemInstance("/");
//	}

//	public VirtualFSI getVirtualFSI() {
//		return virtualFSI;
//	}

}
