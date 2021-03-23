package com.amituofo.xfs.plugin.fs.objectstorage.s3compatible.item;

import com.amituofo.xfs.plugin.fs.objectstorage.s3common.item.BasicS3VersionFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.s3compatible.CompatibleS3FileSystemEntryConfig;

public class CompatibleS3VersionFileItem extends BasicS3VersionFileItem<CompatibleS3Bucketspace> {

	public CompatibleS3VersionFileItem(CompatibleS3Bucketspace bucket, String key, String versionId) {
		super(bucket, key, versionId);
	}

	@Override
	public String getSystemName() {
		return CompatibleS3FileSystemEntryConfig.SYSTEM_NAME;
	}

}
