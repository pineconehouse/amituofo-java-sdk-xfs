package com.amituofo.xfs.plugin.fs.objectstorage.s3amazon.item;

import com.amituofo.xfs.plugin.fs.objectstorage.s3amazon.AmazonS3FileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.item.BasicS3FileItem;

public class AmazonS3FileItem extends BasicS3FileItem<AmazonS3Bucketspace> {

	public AmazonS3FileItem(AmazonS3Bucketspace bucket, String key) {
		super(bucket, key);
	}

	@Override
	public String getSystemName() {
		return AmazonS3FileSystemEntryConfig.SYSTEM_NAME;
	}

}
