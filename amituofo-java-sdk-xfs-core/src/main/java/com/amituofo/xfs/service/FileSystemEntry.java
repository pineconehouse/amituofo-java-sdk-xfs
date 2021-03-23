package com.amituofo.xfs.service;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.config.EntryConfig;

public interface FileSystemEntry extends Itemspaces {

	FileSystemEntry open() throws ServiceException;

	void close() throws ServiceException;

	FileSystemEntry refresh() throws ServiceException;

	FolderItem getDefaultRoot() throws ServiceException;

	boolean isAvailable();

	void test() throws ServiceException;

	String getName();

	String getRootPath();

	String getPluginName();

	FileSystemType getFileSystemType();

	// FolderItem parsePath(FolderItem workingDirectory, String path) throws ServiceException;
	// FolderItem parsePath(String path) throws ServiceException;

	// FileItem createFile(String fullPath) throws ServiceException;

	// FileItem linkFile(String spaceId, String fullpath) throws ServiceException;

	// FolderItem linkFolder(String spaceId, String fullpath) throws ServiceException;

	char getSeparatorChar();

	EntryConfig getEntryConfig();

	FileSystemPreference getPreference();

	boolean hasFeature(int featureId);

}
