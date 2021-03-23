package com.amituofo.xfs.plugin.fs.objectstorage.s3compatible;

import com.amituofo.xfs.config.EntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.BasicS3FileSystemEntryConfig;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;

public class CompatibleS3FileSystemEntryConfig extends BasicS3FileSystemEntryConfig {
	public static final String SYSTEM_NAME = "S3 Compatible Storage";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP90838399";

	public static final String S3SIGNER_TYPE = "S3SIGNER_TYPE";
	// public static final String[] SIGNER_TYPES = new String[] { "Default", "AWSS3V4SignerType", "S3SignerType" };
	// public static final SignerType SIGNER_TYPE = new String[] { "Default", "AWSS3V4SignerType", "S3SignerType" };

	public CompatibleS3FileSystemEntryConfig() {
		this("");
	}

	public CompatibleS3FileSystemEntryConfig(String name) {
		super(CompatibleS3FileSystemEntryConfig.class, SYSTEM_ID, name, URL_ROOT_PATH);
		// this.setSignerType(SignerType.SignatureVersion2);
	}

	public CompatibleS3FileSystemEntryConfig(Class<? extends EntryConfig> connConfigClass, String fileSystemId, String name, String rootPath) {
		super(connConfigClass, fileSystemId, name, rootPath);
		// this.setSignerType(SignerType.SignatureVersion2);
	}

	public String getSignerType() {
		return config.getString(S3SIGNER_TYPE);
	}

	// public int getSignerTypeIndex() {
	// return config.getInteger(SIGNER_TYPE);
	// }

	public void setSignerType(SignerType signerType) {
		config.set(S3SIGNER_TYPE, signerType.getSignerType());
	}

	public void setSignerType(String signerType) {
		config.set(S3SIGNER_TYPE, signerType);
	}

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference preference) {
		return new CompatibleS3FileSystemEntry(this, (CompatibleS3FileSystemPreference) preference);
	}

	@Override
	public FileSystemPreference createPreference() {
		return new CompatibleS3FileSystemPreference(this.getEndpoint());
	}
}
