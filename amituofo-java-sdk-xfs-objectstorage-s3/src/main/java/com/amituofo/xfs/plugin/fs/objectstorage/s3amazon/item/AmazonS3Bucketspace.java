package com.amituofo.xfs.plugin.fs.objectstorage.s3amazon.item;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.s3amazon.AmazonS3FileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.item.BasicS3Bucketspace;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;

public class AmazonS3Bucketspace extends BasicS3Bucketspace {

	public AmazonS3Bucketspace(AmazonS3FileSystemEntry entry, AmazonS3 s3Client, Bucket bucket) {
		super(entry, s3Client, bucket);
	}

	@Override
	public OSDVersionFileItem newVersionFileItemInstance(String key, String versionId) {
		return new AmazonS3VersionFileItem(this, key, versionId);
	}

	@Override
	public FolderItem newFolderItemInstance(String key) {
		return new AmazonS3FolderItem(this, key);
	}

	@Override
	public FileItem newFileItemInstance(String key) {
		return new AmazonS3FileItem(this, key);
	}

}
