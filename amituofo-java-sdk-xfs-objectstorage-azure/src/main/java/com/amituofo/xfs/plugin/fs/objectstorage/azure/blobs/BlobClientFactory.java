package com.amituofo.xfs.plugin.fs.objectstorage.azure.blobs;

import com.amituofo.xfs.plugin.fs.objectstorage.azure.common.AzureStorageClientFactory;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;

public class BlobClientFactory extends AzureStorageClientFactory<BlobServiceClient, BlobContainerClient> {
	private static BlobClientFactory instance = new BlobClientFactory();

	private BlobClientFactory() {
		// super(AzureStorageServiceKind.BlobService);
	}

	public static BlobClientFactory getInstance() {
		return instance;
	}

	@Override
	protected BlobServiceClient createServiceClient(String endpointurl, StorageSharedKeyCredential credential) {
		BlobServiceClient storageClient = new BlobServiceClientBuilder().endpoint(endpointurl).credential(credential).buildClient();
		return storageClient;
	}

	@Override
	protected BlobServiceClient createServiceClient(String endpointurl) {
		BlobServiceClient storageClient = new BlobServiceClientBuilder().endpoint(endpointurl).credential(new DefaultAzureCredentialBuilder().build()).buildClient();
		// BlobServiceClient storageClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
		return storageClient;
	}

	@Override
	protected BlobContainerClient getContainerClient(BlobServiceClient storageClient, String containerName) {
		BlobContainerClient containerClient = storageClient.getBlobContainerClient(containerName);
		return containerClient;
	}

}
