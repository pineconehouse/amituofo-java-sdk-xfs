package com.amituofo.xfs.plugin.fs.objectstorage.azure.common;

import java.util.HashMap;
import java.util.Map;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDBucketspace;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemPreference;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.blobs.BlobClientFactory;
import com.amituofo.xfs.service.ItemspaceConfig;

public abstract class AzureStorageFileSystemEntry<SERVICE_CLIENT, CLIENT, CONFIG extends OSDFileSystemEntryConfig, PREFERENCE extends OSDFileSystemPreference, ITEMSPACE extends OSDBucketspace>
		extends OSDFileSystemEntry<CONFIG, PREFERENCE, ITEMSPACE> {
	private SERVICE_CLIENT storageClient = null;
	private final Map<String, CLIENT> hcpClientMap = new HashMap<String, CLIENT>();
	private AzureStorageClientFactory<SERVICE_CLIENT, CLIENT> clientFactory;

	public AzureStorageFileSystemEntry(CONFIG entryConfig, PREFERENCE preference, AzureStorageClientFactory<SERVICE_CLIENT, CLIENT> clientFactory) {
		super(entryConfig, preference);
		this.clientFactory = clientFactory;
	}

	@Override
	public void close() throws ServiceException {
		storageClient = null;
		hcpClientMap.clear();
		BlobClientFactory.getInstance().clearCache();
	}

	public CLIENT getContainerClient(String namespace) throws ServiceException {
		CLIENT hcpNamespace = hcpClientMap.get(namespace);
		if (hcpNamespace == null) {
			AzureStorageFileSystemEntryConfig conf = (AzureStorageFileSystemEntryConfig) entryConfig;
			hcpNamespace = clientFactory.getContainerClient(namespace, entryConfig.getAccesskey(), entryConfig.getSecretkey(), conf.getEndpoint(), false);

			hcpClientMap.put(namespace, hcpNamespace);
		}

		return hcpNamespace;
	}

	public SERVICE_CLIENT getServiceClient() throws ServiceException {
		if (storageClient == null) {
			// BlobClientFactory.getInstance().setEndpoint(entryConfig.getEndpoint());
			AzureStorageFileSystemEntryConfig conf = (AzureStorageFileSystemEntryConfig) entryConfig;
			if (AuthenticateType.STORAGE_SHARED_KEY_CREDENTIAL == conf.getAuthenticateType()) {
				storageClient = clientFactory.getServiceClient(conf.getAccountName(), entryConfig.getSecretkey(), conf.getEndpoint(), false);
			} else {
				storageClient = clientFactory.getServiceClient(conf.getEndpoint(), false);
			}
		}

		return storageClient;
	}

	protected void verifyConfig(ItemspaceConfig config) throws ServiceException, InvalidParameterException {
		String msg = "This name may only contain lowercase letters, numbers, and hyphens, and must begin with a letter or a number. \r\n"
				+ "Each hyphen must be preceded and followed by a non-hyphen character. The name must also be between 3 and 63 characters long.";
		ValidUtils.invalidIfNotRangeOfLength(config.getName(), 3, 63, msg);
		ValidUtils.invalidIfNotLettersNumbersHyphens(config.getName(), msg);
	}

}
