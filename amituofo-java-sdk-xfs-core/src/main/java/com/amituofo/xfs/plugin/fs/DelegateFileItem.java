package com.amituofo.xfs.plugin.fs;

import java.io.InputStream;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.service.ContentHash;
import com.amituofo.xfs.service.ContentWriter;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemProperties;
import com.amituofo.xfs.service.ItemType;
import com.amituofo.xfs.service.Itemspace;

public class DelegateFileItem implements FileItem {
	protected final FileItem originalFileItem;

	public boolean equals(Object obj) {
		return originalFileItem.equals(obj);
	}

	public FolderItem getRoot() {
		return originalFileItem.getRoot();
	}

	public DelegateFileItem(FileItem fileItem) {
		this.originalFileItem = fileItem;
	}

	public FileItem getOriginalFileItem() {
		return originalFileItem;
	}

	public char getPathSeparator() {
		return originalFileItem.getPathSeparator();
	}

	public ItemType getType() {
		return originalFileItem.getType();
	}

	public boolean isStreaming() {
		return originalFileItem.isStreaming();
	}

	public ItemProperties getProperties() throws ServiceException {
		return originalFileItem.getProperties();
	}

	public boolean isDirectory() {
		return originalFileItem.isDirectory();
	}

	public boolean exists() throws ServiceException {
		return originalFileItem.exists();
	}

	public boolean isFile() {
		return originalFileItem.isFile();
	}

	// public URI toURI() {
	// return originalFileItem.toURI();
	// }

	public String getName() {
		return originalFileItem.getName();
	}

	public void setName(String name) {
		((ItemHiddenFunction) originalFileItem).setName(name);
	}

	public String getExt() {
		return originalFileItem.getExt();
	}

	public boolean delete() throws ServiceException {
		return originalFileItem.delete();
	}

	// public String getCatelog() {
	// return originalFileItem.getCatelog();
	// }
	//
	// public void setCatelog(String catelog) {
	// originalFileItem.setCatelog(catelog);
	// }

	public void rename(String newname) throws ServiceException {
		originalFileItem.rename(newname);
	}

	public Item clone() {
		return originalFileItem.clone();
	}

	public String getPath() {
		return originalFileItem.getPath();
	}

	public Object getData() {
		return originalFileItem.getData();
	}

	public FileSystemPreference getOperationPreference() {
		return originalFileItem.getOperationPreference();
	}

	public void setPath(String path) {
		((ItemHiddenFunction) originalFileItem).setPath(path);
	}

	public InputStream getContent() throws ServiceException {
		return originalFileItem.getContent();
	}

	public void setData(Object data) {
		originalFileItem.setData(data);
	}

	public FolderItem getParent() {
		return originalFileItem.getParent();
	}

	public FileSystemEntry getFileSystemEntry() {
		return originalFileItem.getFileSystemEntry();
	}

	public ContentWriter getContentWriter() {
		return originalFileItem.getContentWriter();
	}

	public int getStatus() {
		return originalFileItem.getStatus();
	}

	public void upateProperties() throws ServiceException {
		originalFileItem.upateProperties();
	}

	public Long getSize() {
		return originalFileItem.getSize();
	}

	// public ItemLocation getLocationType() {
	// return originalFileItem.getLocationType();
	// }

	public void setSize(Long size) {
		((ItemHiddenFunction) originalFileItem).setSize(size);
	}

	public Long getCreateTime() {
		return originalFileItem.getCreateTime();
	}

	public void setCreateTime(Long createTime) {
		((ItemHiddenFunction) originalFileItem).setCreateTime(createTime);
	}

	public Itemspace getItemspace() {
		return originalFileItem.getItemspace();
	}

	public Long getLastUpdateTime() {
		return originalFileItem.getLastUpdateTime();
	}

	public Itemspace[] getItemspaces() throws ServiceException {
		return originalFileItem.getItemspaces();
	}

	public void setLastUpdateTime(Long lastUpdateTime) {
		((ItemHiddenFunction) originalFileItem).setLastUpdateTime(lastUpdateTime);
	}

	public Long getLastAccessTime() {
		return originalFileItem.getLastAccessTime();
	}

	public boolean isSame(Item item) {
		return originalFileItem.isSame(item);
	}

	public void setLastAccessTime(Long lastAccessTime) {
		((ItemHiddenFunction) originalFileItem).setLastAccessTime(lastAccessTime);
	}

	public boolean isFromSameSystem(Item item) {
		return originalFileItem.isFromSameSystem(item);
	}

	public String getSystemName() {
		return originalFileItem.getSystemName();
	}

	public ContentHash getContentHash() {
		return originalFileItem.getContentHash();
	}

	public void setContentHash(ContentHash contentHash) {
		originalFileItem.setContentHash(contentHash);
	}

	@Override
	public String toString() {
		return originalFileItem.toString();
	}

}
