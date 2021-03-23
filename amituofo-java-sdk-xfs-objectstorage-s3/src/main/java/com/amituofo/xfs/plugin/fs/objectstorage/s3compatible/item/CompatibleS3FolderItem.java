package com.amituofo.xfs.plugin.fs.objectstorage.s3compatible.item;

import com.amituofo.xfs.plugin.fs.objectstorage.s3common.item.BasicS3FolderItem;
import com.amituofo.xfs.plugin.fs.objectstorage.s3compatible.CompatibleS3FileSystemEntryConfig;

public class CompatibleS3FolderItem extends BasicS3FolderItem<CompatibleS3Bucketspace> {

	public CompatibleS3FolderItem(CompatibleS3Bucketspace bucket, String key) {
		super(bucket, key);
	}

	@Override
	public String getSystemName() {
		return CompatibleS3FileSystemEntryConfig.SYSTEM_NAME;
	}
}
