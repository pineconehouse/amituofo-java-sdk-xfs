package com.amituofo.xfs.plugin.fs.cifs.item;

import com.amituofo.xfs.plugin.fs.cifs.SmbFileSystemEntry;
import com.amituofo.xfs.plugin.fs.cifs.SmbFileSystemPreference;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.ItemspaceBase;
import com.hierynomus.smbj.share.DiskShare;

public class SmbSharespace extends ItemspaceBase<SmbFileSystemEntry, SmbFileSystemPreference> {
	private String name;
	private DiskShare diskShare;

	public SmbSharespace(SmbFileSystemEntry fileSystemEntry, String name, DiskShare diskShare) {
		super(fileSystemEntry);
		this.name = name;
		this.diskShare = diskShare;
	}

	@Override
	public String getName() {
		return name;
	}

	public DiskShare getDiskShare() {
//		diskShare.isConnected()
		return diskShare;
	}

	@Override
	public FolderItem newFolderItemInstance(String fullpath) {
		return new SmbFolderItem(this, fullpath);
	}

	@Override
	public FileItem newFileItemInstance(String fullpath) {
		return new SmbFileItem(this, fullpath);
	}

	@Override
	protected FolderItem createRootFolder() {
		SmbFolderItem folder = (SmbFolderItem) newFolderItemInstance("");
		folder.setName("");
		return folder;
	}

}
