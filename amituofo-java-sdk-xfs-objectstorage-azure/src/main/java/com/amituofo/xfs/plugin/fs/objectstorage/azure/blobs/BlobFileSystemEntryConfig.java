package com.amituofo.xfs.plugin.fs.objectstorage.azure.blobs;

import com.amituofo.xfs.plugin.fs.objectstorage.azure.common.AzureStorageFileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.common.AzureStorageServiceKind;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;

public class BlobFileSystemEntryConfig extends AzureStorageFileSystemEntryConfig {

	public static final String SYSTEM_NAME = "Microsoft Azure Blob Storage";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP093852758";

	public BlobFileSystemEntryConfig(AzureStorageFileSystemEntryConfig config) {
		super(config, BlobFileSystemEntryConfig.class);
	}

	public BlobFileSystemEntryConfig(String name) {
		super(SYSTEM_ID, BlobFileSystemEntryConfig.class, name);
	}

	public BlobFileSystemEntryConfig() {
		super(SYSTEM_ID, BlobFileSystemEntryConfig.class);
	}

	// @Override
	// protected void validate() throws InvalidParameterException {
	// super.validate();
	// ValidUtils.invalidIfEmpty(this.getContainerName(), "Blob container name must be specificed!");
	// }

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new BlobFileSystemEntry(this, (BlobFileSystemPreference) perference);
	}

	@Override
	public FileSystemPreference createPreference() {
		return new BlobFileSystemPreference();
	}

	@Override
	public AzureStorageServiceKind getAzureStorageServiceType() {
		return AzureStorageServiceKind.BlobService;
	}

}
