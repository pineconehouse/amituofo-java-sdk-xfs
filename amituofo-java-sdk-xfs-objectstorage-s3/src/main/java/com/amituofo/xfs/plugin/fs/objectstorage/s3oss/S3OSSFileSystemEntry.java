package com.amituofo.xfs.plugin.fs.objectstorage.s3oss;

import com.amituofo.xfs.plugin.fs.objectstorage.s3compatible.CompatibleS3FileSystemEntry;

public class S3OSSFileSystemEntry extends CompatibleS3FileSystemEntry {
	public S3OSSFileSystemEntry(S3OSSFileSystemEntryConfig entryConfig, S3OSSFileSystemPreference preference) {
		super(entryConfig, preference);
	}

}
