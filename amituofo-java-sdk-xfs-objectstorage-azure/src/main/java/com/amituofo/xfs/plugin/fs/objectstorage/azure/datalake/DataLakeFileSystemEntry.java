package com.amituofo.xfs.plugin.fs.objectstorage.azure.datalake;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.common.AzureStorageFileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.datalake.item.DataLakeContainerspace;
import com.amituofo.xfs.service.ItemspaceConfig;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.file.datalake.DataLakeFileSystemClient;
import com.azure.storage.file.datalake.DataLakeServiceClient;
import com.azure.storage.file.datalake.models.FileSystemItem;

public class DataLakeFileSystemEntry
		extends AzureStorageFileSystemEntry<DataLakeServiceClient, DataLakeFileSystemClient, DataLakeFileSystemEntryConfig, DataLakeFileSystemPreference, DataLakeContainerspace> {

	public DataLakeFileSystemEntry(DataLakeFileSystemEntryConfig entryConfig, DataLakeFileSystemPreference preference) {
		super(entryConfig, preference, DataLakeClientFactory.getInstance());
	}

	@Override
	protected DataLakeContainerspace createDefaultItemspace() throws ServiceException {
		DataLakeFileSystemClient containerClient = getContainerClient(entryConfig.getBucketName());
//		containerClient.getProperties().
		DataLakeContainerspace root = new DataLakeContainerspace(this, containerClient);
		return root;
	}

	@Override
	protected List<DataLakeContainerspace> listAccessibleItemspaces() throws ServiceException {
		List<DataLakeContainerspace> itemspaces = new ArrayList<DataLakeContainerspace>();
		PagedIterable<FileSystemItem> namespaces;

		try {
			namespaces = getServiceClient().listFileSystems();

			for (Iterator<FileSystemItem> it = namespaces.iterator(); it.hasNext();) {
				FileSystemItem blobItem = (FileSystemItem) it.next();
				DataLakeFileSystemClient namespace = getContainerClient(blobItem.getName());
				DataLakeContainerspace root = new DataLakeContainerspace(this, namespace);

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
			getContainerClient(entryConfig.getBucketName()).getProperties();
			// getDataLakeFileSystemClient(entryConfig.getNamespace()).getServiceVersion();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void createItemSpace(ItemspaceConfig config) throws ServiceException {
		try {
			String msg = "This name may only contain lowercase letters, numbers, and hyphens, and must begin with a letter or a number. \r\n"
					+ "Each hyphen must be preceded and followed by a non-hyphen character. The name must also be between 3 and 63 characters long.";
			ValidUtils.invalidIfNotRangeOfLength(config.getName(), 3, 63, msg);
			ValidUtils.invalidIfNotLettersNumbersHyphens(config.getName(), msg);

			String name = config.getName().toLowerCase();
			getServiceClient().createFileSystem(name);
			// Map<String, String> metadata = null;
			// Context context = null;
			// PublicAccessType accessType = null;
			// Response<DataLakeFileSystemClient> response = getDataLakeServiceClient().createDataLakeContainerWithResponse(config.getName(), metadata,
			// accessType,
			// context);
			// response.getValue();
		} catch (Exception e) {
			// String msg = e.getMessage();
			throw new ServiceException("Unable to create container " + config.getName(), e);
		}
	}

	@Override
	public void modifyItemSpace(ItemspaceConfig itemspaceConfig) throws ServiceException {
		
	}
	
	@Override
	public void deleteItemSpace(String name) throws ServiceException {
		getServiceClient().deleteFileSystem(name);
	}

}
