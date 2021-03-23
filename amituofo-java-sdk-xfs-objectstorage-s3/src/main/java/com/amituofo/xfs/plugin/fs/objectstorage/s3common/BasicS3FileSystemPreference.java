package com.amituofo.xfs.plugin.fs.objectstorage.s3common;

import com.amazonaws.services.s3.internal.SkipMd5CheckStrategy;
import com.amazonaws.services.s3.model.StorageClass;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemPreference;

public class BasicS3FileSystemPreference extends OSDFileSystemPreference {
	private String endpoint;
	private StorageClass defaultStorageClass = null;
	private boolean decodePathEnabled = true;

	public BasicS3FileSystemPreference(String endpoint) {
		super();
		this.endpoint = endpoint;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public StorageClass getDefaultStorageClass() {
		return defaultStorageClass;
	}

	public void setDefaultStorageClass(StorageClass defaultStorageClass) {
		this.defaultStorageClass = defaultStorageClass;
	}

	public boolean isDecodePathEnabled() {
		return decodePathEnabled;
	}

	public void setDecodePathEnabled(boolean enabled) {
		this.decodePathEnabled = enabled;
	}

	public void setVerifyPutObjectContent(boolean contentVerify) {
		super.setVerifyPutObjectContent(contentVerify);

		// for S3
		if (contentVerify) {
			System.clearProperty(SkipMd5CheckStrategy.DISABLE_PUT_OBJECT_MD5_VALIDATION_PROPERTY);
		} else {
			System.setProperty(SkipMd5CheckStrategy.DISABLE_PUT_OBJECT_MD5_VALIDATION_PROPERTY, "true");
		}
	}

}
