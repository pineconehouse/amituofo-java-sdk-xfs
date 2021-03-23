package com.amituofo.xfs.service;

import com.amituofo.common.ex.ServiceException;

public interface FileSystem {

	String getSystemName();

//	String[] getSupportVersion();

	boolean exists() throws ServiceException;
	
	boolean delete() throws ServiceException;

	void rename(String newname) throws ServiceException;

	FileSystemPreference getOperationPreference();

	FileSystemEntry getFileSystemEntry();
}
