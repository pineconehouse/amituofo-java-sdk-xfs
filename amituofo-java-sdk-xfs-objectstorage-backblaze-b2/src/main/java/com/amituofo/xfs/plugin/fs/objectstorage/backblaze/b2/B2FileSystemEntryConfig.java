package com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.config.EntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemEntryConfig;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;

public class B2FileSystemEntryConfig extends OSDFileSystemEntryConfig {
	public static final String SYSTEM_NAME = "Backblaze B2 Cloud Storage";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP746284938";

	public B2FileSystemEntryConfig() {
		this("");
	}

	public B2FileSystemEntryConfig(String name) {
		this(B2FileSystemEntryConfig.class, SYSTEM_ID, name, URL_ROOT_PATH);
	}

	public B2FileSystemEntryConfig(B2FileSystemEntryConfig config) {
		this(config.getName());
		super.config.setSimpleConfiguration(config.getSimpleConfiguration());
		super.config.set(FILE_SYSTEM_ID, SYSTEM_ID);
	}

	public B2FileSystemEntryConfig(Class<? extends EntryConfig> connConfigClass, String fileSystemId, String name, String rootPath) {
		super(connConfigClass, fileSystemId, name, rootPath);
		super.setHost(getEndpoint());
//		super.setUseSSL(true);
	}

	@Override
	public String getEndpoint() {
		return "https://api.backblazeb2.com";
	}

//	public String getBucketName() {
//		return super.getNamespace();
//	}
//
//	public void setBucketName(String bucketName) {
//		super.setNamespace(bucketName);
//	}

	public void setApplicationKey(String applicationKey) throws ServiceException {
		super.setSecretkey(applicationKey);
	}

	public String getApplicationKey() throws ServiceException {
		return super.getSecretkey();
	}

	public void setApplicationKeyId(String applicationKeyId) throws ServiceException {
		super.setAccesskey(applicationKeyId);
	}

	public String getApplicationKeyId() {
		return super.getAccesskey();
	}

	@Override
	protected void validate() throws InvalidParameterException {
		super.validate();
		ValidUtils.invalidIfEmpty(this.getEndpoint(), "Endpoint must be specificed!");
	}

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new B2FileSystemEntry(this, (B2FileSystemPreference) perference);
	}

	@Override
	public FileSystemPreference createPreference() {
		return new B2FileSystemPreference();
	}

}
