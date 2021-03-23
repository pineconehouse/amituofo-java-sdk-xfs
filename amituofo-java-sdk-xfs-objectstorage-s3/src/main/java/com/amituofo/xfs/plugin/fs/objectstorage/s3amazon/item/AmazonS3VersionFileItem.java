package com.amituofo.xfs.plugin.fs.objectstorage.s3amazon.item;

import com.amituofo.xfs.plugin.fs.objectstorage.s3amazon.AmazonS3FileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.item.BasicS3VersionFileItem;

public class AmazonS3VersionFileItem extends BasicS3VersionFileItem<AmazonS3Bucketspace> {

	public AmazonS3VersionFileItem(AmazonS3Bucketspace bucket, String key, String versionId) {
		super(bucket, key, versionId);
	}

	@Override
	public String getSystemName() {
		return AmazonS3FileSystemEntryConfig.SYSTEM_NAME;
	}

}
