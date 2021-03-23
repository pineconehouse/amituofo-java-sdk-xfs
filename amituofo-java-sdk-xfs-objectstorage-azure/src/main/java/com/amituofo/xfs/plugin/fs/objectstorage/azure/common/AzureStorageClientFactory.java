package com.amituofo.xfs.plugin.fs.objectstorage.azure.common;

import java.util.HashMap;
import java.util.Map;

import com.amituofo.common.util.DigestUtils;
import com.azure.storage.common.StorageSharedKeyCredential;

public abstract class AzureStorageClientFactory<SERVICE_CLIENT, CLIENT> {
	private Map<String, CLIENT> clientMap = new HashMap<String, CLIENT>();
	private Map<String, SERVICE_CLIENT> serviceClientMap = new HashMap<String, SERVICE_CLIENT>();

	abstract protected SERVICE_CLIENT createServiceClient(String endpointurl, StorageSharedKeyCredential credential);

	abstract protected SERVICE_CLIENT createServiceClient(String endpointurl);

	abstract protected CLIENT getContainerClient(SERVICE_CLIENT storageClient, String containerName);

	public void clearCache() {
		clientMap.clear();
		serviceClientMap.clear();
	}

	public SERVICE_CLIENT getServiceClient(String accountName, String accountKey, String endpointurl, boolean forceNewClient) {
		String key1 = DigestUtils.format2Hex(DigestUtils.calcMD5(accountName + accountKey + endpointurl));
		SERVICE_CLIENT storageClient = serviceClientMap.get(key1);
		if (storageClient == null) {
			StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);

			/*
			 * Create a ServiceClient object that wraps the service endpoint, credential and a request pipeline.
			 */
			storageClient = createServiceClient(endpointurl, credential);
			serviceClientMap.put(key1, storageClient);
		}

		return storageClient;
	}
	
	public SERVICE_CLIENT getServiceClient(String endpointurl, boolean forceNewClient) {
		String key1 = DigestUtils.format2Hex(DigestUtils.calcMD5(endpointurl));
		SERVICE_CLIENT storageClient = serviceClientMap.get(key1);
		if (storageClient == null) {
			storageClient = createServiceClient(endpointurl);
			serviceClientMap.put(key1, storageClient);
		}

		return storageClient;
	}

	public CLIENT getContainerClient(String containerName, String accountName, String accountKey, String endpointurl, boolean forceNewClient) {
		String key = DigestUtils.format2Hex(DigestUtils.calcMD5(containerName + accountName + accountKey + endpointurl));

		SERVICE_CLIENT storageClient = getServiceClient(accountName, accountKey, endpointurl, false);

		CLIENT containerClient = null;

		if (!forceNewClient) {
			containerClient = clientMap.get(key);
		}

		if (containerClient == null) {
			// if(StringUtils.isEmpty(containerName)) {
			// storageClient.list
			// }
			containerClient = getContainerClient(storageClient, containerName);
			clientMap.put(key, containerClient);
		}

		return containerClient;
	}

	/**
	 * Authenticate using Azure Identity https://azure.microsoft.com/en-us/product-categories/identity/
	 * 
	 * @param accountName
	 * @param forceNewClient
	 * @return
	 */
//	public SERVICE_CLIENT getServiceClient(String accountName, String endpointurl, boolean forceNewClient) {
//		String key1 = DigestUtils.format2Hex(DigestUtils.calcMD5(accountName + endpointurl));
//		SERVICE_CLIENT storageClient = serviceClientMap.get(key1);
//		if (storageClient == null) {
//			/*
//			 * Create a SERVICE_CLIENT object that wraps the service endpoint, credential and a request pipeline.
//			 */
//			storageClient = createServiceClient(endpointurl);
//			serviceClientMap.put(key1, storageClient);
//		}
//
//		return storageClient;
//	}
}
