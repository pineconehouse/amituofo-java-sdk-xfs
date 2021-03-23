package com.amituofo.xfs.plugin.fs.objectstorage.s3compatible.item;

import com.amituofo.xfs.plugin.fs.objectstorage.s3common.item.BasicS3FileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.s3compatible.CompatibleS3FileSystemEntryConfig;

public class CompatibleS3FileItem extends BasicS3FileItem<CompatibleS3Bucketspace> {

	public CompatibleS3FileItem(CompatibleS3Bucketspace bucket, String key) {
		super(bucket, key);
	}

	@Override
	public String getSystemName() {
		return CompatibleS3FileSystemEntryConfig.SYSTEM_NAME;
	}

}
