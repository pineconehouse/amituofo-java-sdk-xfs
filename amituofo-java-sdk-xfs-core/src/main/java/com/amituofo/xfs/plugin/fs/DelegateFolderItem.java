package com.amituofo.xfs.plugin.fs;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemProperties;
import com.amituofo.xfs.service.ItemType;
import com.amituofo.xfs.service.Itemspace;

public class DelegateFolderItem implements FolderItem {
	protected final FolderItem folderItem;

	public DelegateFolderItem(FolderItem folderItem) {
		super();
		this.folderItem = folderItem;
	}
	
	public FolderItem getOriginalFolderItem() {
		return folderItem;
	}

	public Itemspace getItemspace() {
		return folderItem.getItemspace();
	}

	public Itemspace[] getItemspaces() throws ServiceException {
		return folderItem.getItemspaces();
	}

	public ItemProperties getProperties() throws ServiceException {
		return folderItem.getProperties();
	}

	public void listFolders(final ItemFilter filter, ItemHandler handler) {
		folderItem.listFolders(filter, handler);
	}

	public char getPathSeparator() {
		return folderItem.getPathSeparator();
	}

	public boolean isDirectory() {
		return folderItem.isDirectory();
	}

	public boolean exists() throws ServiceException {
		return folderItem.exists();
	}

	public boolean isFile() {
		return folderItem.isFile();
	}

	public String getName() {
		return folderItem.getName();
	}

	public boolean delete(ItemHandler handler) {
		return folderItem.delete(handler);
	}

	public void setName(String name) {
		((ItemHiddenFunction)folderItem).setName(name);
	}

	public String getExt() {
		return folderItem.getExt();
	}

	public boolean delete() throws ServiceException {
		return folderItem.delete();
	}

//	public String getCatelog() {
//		return folderItem.getCatelog();
//	}

	public void rename(String newname) throws ServiceException {
		folderItem.rename(newname);
	}

	public Item clone() {
		return new DelegateFolderItem(folderItem);
	}

//	public void setCatelog(String catelog) {
//		folderItem.setCatelog(catelog);
//	}

	public Object getData() {
		return folderItem.getData();
	}

	public String getPath() {
		return folderItem.getPath();
	}

	public FileSystemPreference getOperationPreference() {
		return folderItem.getOperationPreference();
	}

	public void setData(Object data) {
		folderItem.setData(data);
	}

	public void setPath(String path) {
		((ItemHiddenFunction)folderItem).setPath(path);
	}

	public FolderItem getParent() {
		return folderItem.getParent();
	}

	public FileSystemEntry getFileSystemEntry() {
		return folderItem.getFileSystemEntry();
	}

	public void list(ItemFilter filter, ItemHandler handler) {
		folderItem.list(filter, handler);
	}

	public int getStatus() {
		return folderItem.getStatus();
	}

	public Long getSize() {
		return folderItem.getSize();
	}

	public void list(ItemHandler handler) {
		folderItem.list(handler);
	}

	public void setSize(Long size) {
		((ItemHiddenFunction)folderItem).setSize(size);
	}

//	public boolean deleteEmptyFolder() throws ServiceException {
//		return folderItem.deleteEmptyFolder();
//	}

	public Long getCreateTime() {
		return folderItem.getCreateTime();
	}

	public FolderItem getRoot() {
		return folderItem.getRoot();
	}

	public boolean equals(Object obj) {
		return folderItem.equals(obj);
	}

	public void setCreateTime(Long createTime) {
		((ItemHiddenFunction)folderItem).setCreateTime(createTime);
	}

	public boolean createDirectory(String name) throws ServiceException {
		return folderItem.createDirectory(name);
	}

	public Itemspace[] getRoots() throws ServiceException {
		return folderItem.getItemspaces();
	}

	public Long getLastUpdateTime() {
		return folderItem.getLastUpdateTime();
	}

	public void setLastUpdateTime(Long lastUpdateTime) {
		((ItemHiddenFunction)folderItem).setLastUpdateTime(lastUpdateTime);
	}

	public boolean createDirectory() throws ServiceException {
		return folderItem.createDirectory();
	}

	public boolean isSame(Item item) {
		return folderItem.isSame(item);
	}

	public Long getLastAccessTime() {
		return folderItem.getLastAccessTime();
	}

	public boolean isFromSameSystem(Item item) {
		return folderItem.isFromSameSystem(item);
	}

//	public FileItem createFile(String name) throws ServiceException {
//		return folderItem.createFile(name);
//	}

	public void setLastAccessTime(Long lastAccessTime) {
		((ItemHiddenFunction)folderItem).setLastAccessTime(lastAccessTime);
	}

	public String getSystemName() {
		return folderItem.getSystemName();
	}

//	public String[] getSupportVersion() {
//		return folderItem.getSupportVersion();
//	}

//	public boolean isVirtual() {
//		return folderItem.isVirtual();
//	}

//	public void write(String name, InputStream in, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
//		folderItem.write(name, in, progressListener);
//	}
//
//	public void write(String name, InputStream in) throws ServiceException {
//		folderItem.write(name, in);
//	}

	@Override
	public FileItem linkFile(String fileName) throws ServiceException {
		return folderItem.linkFile(fileName);
	}

	@Override
	public FolderItem linkFolder(String folderName) throws ServiceException {
		return folderItem.linkFolder(folderName);
	}

//	public URI toURI() {
//		return folderItem.toURI();
//	}

//	public RootItem getSection() {
//		return folderItem.getSection();
//	}

//	public FileItem linkFile(String originalFullPath, char originalPathSeparator) throws ServiceException {
//		return folderItem.linkFile(originalFullPath, originalPathSeparator);
//	}
//
//	public FolderItem linkFolder(String originalFullPath, char originalPathSeparator) throws ServiceException {
//		return folderItem.linkFolder(originalFullPath, originalPathSeparator);
//	}

	public boolean isRoot() {
		return folderItem.isRoot();
	}
	
	@Override
	public String toString() {
		return folderItem.toString();
	}
}
