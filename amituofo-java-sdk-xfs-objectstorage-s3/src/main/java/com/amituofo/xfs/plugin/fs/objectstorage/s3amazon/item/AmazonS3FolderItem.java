package com.amituofo.xfs.plugin.fs.objectstorage.s3amazon.item;

import com.amituofo.xfs.plugin.fs.objectstorage.s3amazon.AmazonS3FileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.item.BasicS3FolderItem;

public class AmazonS3FolderItem extends BasicS3FolderItem<AmazonS3Bucketspace> {

	public AmazonS3FolderItem(AmazonS3Bucketspace bucket, String key) {
		super(bucket, key);
	}

	@Override
	public String getSystemName() {
		return AmazonS3FileSystemEntryConfig.SYSTEM_NAME;
	}

}
