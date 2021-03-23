package com.amituofo.xfs.plugin.fs.virtual.vfsi;

import com.amituofo.xfs.plugin.fs.virtual.item.VirtualFolderItem;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemType;

public interface VirtualFSI {

	void newFile(String filename, long length, long createTime) throws VirtualFileSystemException;

	void newDirectory(String filename) throws VirtualFileSystemException;

	boolean delete(String filename, ItemType type) throws VirtualFileSystemException;

	boolean rename(String filename, String newfilename, ItemType type) throws VirtualFileSystemException;

	boolean exist(String filename, ItemType type) throws VirtualFileSystemException;

	void list(ItemFilter filter, ItemHandler itemHandler) throws VirtualFileSystemException;

	void listFolders(ItemFilter filter, ItemHandler handler) throws VirtualFileSystemException;
	
	int count() throws VirtualFileSystemException;

//	void clear() throws VirtualFileSystemException;

	void close();

	void init() throws VirtualFileSystemException;

	VirtualFolderItem getFolderItem();

	String getFolderName();

	String getFolderPath();

	void setFileLength(String name, long length) throws VirtualFileSystemException;

	boolean remove() throws VirtualFileSystemException;

	boolean renameTo(String newFoldername);

	String read(String filename) throws VirtualFileSystemException;

//	Object getAttribute(String filename, String attrname) throws VirtualFileSystemException;

	Long[] getSystemAttribute(String filename, ItemType type) throws VirtualFileSystemException;

	long getLastUpdatetime();

	String getFileSystemId();

//	boolean lock();

//	void unlock();

}
