package com.amituofo.xfs.plugin.fs.objectstorage.azure.common.item;

import java.net.URL;
import java.util.Date;
import java.util.Map;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.service.FileItem;

public interface AzureStorageFileItem extends FileItem {
	Map<String, String> getMetadata();

	void setMetadata(Map<String, String> metadata) throws ServiceException;

	URL generateSasUrl(boolean readPermission, boolean writePermission, boolean deletePermission, Date expiration) throws ServiceException;

	String getURL();

	String getETag();

	String getOwner();
}
