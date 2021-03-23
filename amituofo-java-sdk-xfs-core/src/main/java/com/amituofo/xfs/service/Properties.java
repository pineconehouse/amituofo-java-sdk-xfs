package com.amituofo.xfs.service;

import com.amituofo.common.ex.ServiceException;

public interface Properties {
	public final static int ITEM_STATUS_DELETED = 2;
	public final static int ITEM_STATUS_HIDDEN = 4;
	public final static int ITEM_STATUS_SYMLINK = 8;

	String getName();

	// void setName(String name);

	String getExt();

	// String getCatelog();
	//
	// void setCatelog(String catelog);

	String getPath();

	ItemType getType();

	int getStatus();

	Long getSize();

	Long getCreateTime();

	Long getLastUpdateTime();

	Long getLastAccessTime();

	// boolean isVirtual();

//	void updateMetadata() throws ServiceException;
	
	ItemProperties getProperties() throws ServiceException;

}
