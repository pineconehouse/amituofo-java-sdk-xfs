package com.amituofo.xfs.plugin.fs.objectstorage.azure.datalake;

import com.amituofo.xfs.plugin.fs.objectstorage.azure.common.AzureStorageFileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.common.AzureStorageServiceKind;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;

public class DataLakeFileSystemEntryConfig extends AzureStorageFileSystemEntryConfig {
	public static final String SYSTEM_NAME = "Microsoft Azure Data Lake Gen2";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP093852759";

	public DataLakeFileSystemEntryConfig(AzureStorageFileSystemEntryConfig config) {
		super(config, DataLakeFileSystemEntryConfig.class);
	}

	public DataLakeFileSystemEntryConfig(String name) {
		super(SYSTEM_ID, DataLakeFileSystemEntryConfig.class, name);
	}

	public DataLakeFileSystemEntryConfig() {
		super(SYSTEM_ID, DataLakeFileSystemEntryConfig.class);
	}

	// @Override
	// protected void validate() throws InvalidParameterException {
	// super.validate();
	// ValidUtils.invalidIfEmpty(this.getContainerName(), "Data lake container name must be specificed!");
	// }

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new DataLakeFileSystemEntry(this, (DataLakeFileSystemPreference) perference);
	}

	@Override
	public FileSystemPreference createPreference() {
		return new DataLakeFileSystemPreference();
	}

	@Override
	public AzureStorageServiceKind getAzureStorageServiceType() {
		return AzureStorageServiceKind.DataLakeStorage;
	}

}
