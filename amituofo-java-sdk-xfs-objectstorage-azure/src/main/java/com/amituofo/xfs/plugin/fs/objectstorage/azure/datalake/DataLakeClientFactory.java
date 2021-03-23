package com.amituofo.xfs.plugin.fs.objectstorage.azure.datalake;

import com.amituofo.xfs.plugin.fs.objectstorage.azure.common.AzureStorageClientFactory;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.azure.storage.file.datalake.DataLakeFileSystemClient;
import com.azure.storage.file.datalake.DataLakeServiceClient;
import com.azure.storage.file.datalake.DataLakeServiceClientBuilder;

public class DataLakeClientFactory extends AzureStorageClientFactory<DataLakeServiceClient, DataLakeFileSystemClient> {
	private static DataLakeClientFactory instance = new DataLakeClientFactory();

	private DataLakeClientFactory() {
		// super(AzureStorageServiceKind.DataLakeStorage);
	}

	public static DataLakeClientFactory getInstance() {
		return instance;
	}

	@Override
	protected DataLakeServiceClient createServiceClient(String endpointurl, StorageSharedKeyCredential credential) {
		// new DataLakeServiceClientBuilder().
		DataLakeServiceClient storageClient = new DataLakeServiceClientBuilder().endpoint(endpointurl).credential(credential).buildClient();
		return storageClient;
	}

	@Override
	protected DataLakeServiceClient createServiceClient(String endpointurl) {
		DataLakeServiceClient storageClient = new DataLakeServiceClientBuilder().endpoint(endpointurl).credential(new DefaultAzureCredentialBuilder().build()).buildClient();
		return storageClient;
	}

	@Override
	protected DataLakeFileSystemClient getContainerClient(DataLakeServiceClient storageClient, String containerName) {
		DataLakeFileSystemClient containerClient = storageClient.getFileSystemClient(containerName);
		return containerClient;
	}

}
