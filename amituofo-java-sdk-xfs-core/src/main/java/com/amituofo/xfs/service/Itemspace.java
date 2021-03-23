package com.amituofo.xfs.service;

import com.amituofo.common.ex.ServiceException;

public interface Itemspace {
	FolderItem getRootFolder();

	FolderItem getHomeFolder();

	String getName();

	// String getId();

	FileSystemEntry getFileSystemEntry();

	FileSystemPreference getFileSystemPreference();

	ItemspaceSummary getSummary();

	// long getTotal();

	boolean isAvailable() throws ServiceException;

	FolderItem linkFolder(String fullPath) throws ServiceException;

	FileItem linkFile(String fullPath) throws ServiceException;

	void list(final ListOption listOption, final ItemHandler handler) throws ServiceException;

	void emptyingItemspace(final ItemHandler handler) throws ServiceException;
}
