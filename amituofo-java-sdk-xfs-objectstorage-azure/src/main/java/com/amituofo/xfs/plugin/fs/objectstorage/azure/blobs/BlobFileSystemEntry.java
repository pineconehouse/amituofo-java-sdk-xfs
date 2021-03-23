package com.amituofo.xfs.plugin.fs.objectstorage.azure.blobs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.blobs.item.BlobContainerspace;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.common.AzureStorageFileSystemEntry;
import com.amituofo.xfs.service.ItemspaceConfig;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceVersion;
import com.azure.storage.blob.models.BlobContainerItem;
import com.azure.storage.blob.models.BlobContainerProperties;
import com.azure.storage.blob.models.StorageAccountInfo;

public class BlobFileSystemEntry extends AzureStorageFileSystemEntry<BlobServiceClient, BlobContainerClient, BlobFileSystemEntryConfig, BlobFileSystemPreference, BlobContainerspace> {

	public BlobFileSystemEntry(BlobFileSystemEntryConfig entryConfig, BlobFileSystemPreference preference) {
		super(entryConfig, preference, BlobClientFactory.getInstance());
	}

	@Override
	protected BlobContainerspace createDefaultItemspace() throws ServiceException {
		BlobContainerClient containerClient = getContainerClient(entryConfig.getBucketName());
		BlobContainerspace root = new BlobContainerspace(this, containerClient);

		return root;
	}

	@Override
	public void modifyItemSpace(ItemspaceConfig itemspaceConfig) throws ServiceException {
	}
	
	@Override
	protected List<BlobContainerspace> listAccessibleItemspaces() throws ServiceException {
		List<BlobContainerspace> itemspaces = new ArrayList<BlobContainerspace>();
		PagedIterable<BlobContainerItem> namespaces;

		try {
			namespaces = getServiceClient().listBlobContainers();

			for (Iterator<BlobContainerItem> it = namespaces.iterator(); it.hasNext();) {
				BlobContainerItem blobItem = (BlobContainerItem) it.next();
				BlobContainerClient namespace = getContainerClient(blobItem.getName());

				BlobContainerspace root = new BlobContainerspace(this, namespace);

				itemspaces.add(root);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// throw new ServiceException("Failed to list blob containers!", e);
			throw new ServiceException(e);
		}

		// 避免两个桶一个http 一个https，会导致getroot失败
		return itemspaces;
	}

	@Override
	public boolean isAvailable() {
		try {
			return getContainerClient(entryConfig.getBucketName()).exists();
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void createItemSpace(ItemspaceConfig config) throws ServiceException {
		try {
			verifyConfig(config);
			String name = config.getName().toLowerCase();
			getServiceClient().createBlobContainer(name);
		} catch (Exception e) {
			// String msg = e.getMessage();
			throw new ServiceException("Unable to create container " + config.getName(), e);
		}
	}

	@Override
	public void deleteItemSpace(String name) throws ServiceException {
		getServiceClient().deleteBlobContainer(name);
	}

}
