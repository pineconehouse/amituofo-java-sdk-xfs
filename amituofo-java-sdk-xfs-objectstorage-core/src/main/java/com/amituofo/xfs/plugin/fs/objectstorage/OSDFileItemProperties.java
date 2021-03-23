package com.amituofo.xfs.plugin.fs.objectstorage;

import com.amituofo.xfs.service.Properties;

public interface OSDFileItemProperties extends Properties {

//	String getBucketName();

	String getETag();

	String getVersionId();

	String getOwner();
	
//	String getStorageClass();
	
	String getKey();

	void setKey(String key);
}
