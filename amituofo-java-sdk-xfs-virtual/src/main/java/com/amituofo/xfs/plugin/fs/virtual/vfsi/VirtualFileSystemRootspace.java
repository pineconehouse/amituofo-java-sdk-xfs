package com.amituofo.xfs.plugin.fs.virtual.vfsi;

import com.amituofo.xfs.service.ItemType;

public interface VirtualFileSystemRootspace {
	long generateFileId();

	void close();

	void init() throws VirtualFileSystemException;

	VirtualFSI getVirtualFSI(String folderPath, ItemType type);

	boolean cleanAndDeleteVirtualFSI(String folderPath);

	String toFolderId(String folderPath);

	VirtualFSI removeVirtualFSICache(String folderPath);

}
