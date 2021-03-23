package com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2;

import com.amituofo.common.ex.ServiceException;
import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.B2StorageClientFactory;

public class B2ClientFactory {
	private static B2ClientFactory instance = new B2ClientFactory();

	private B2ClientFactory() {
	}

	public static B2ClientFactory getInstance() {
		return instance;
	}

	public B2StorageClient getB2StorageClient(String applicationKeyId, String applicationKey) throws ServiceException {
		B2StorageClient client = B2StorageClientFactory.createDefaultFactory().create(applicationKeyId, applicationKey, "amituofo-xfs-sdk");
//		B2ClientConfig config=B2ClientConfig.builder(applicationKeyId, applicationKeyId, "").setMasterUrl(masterUrl);
//		B2StorageClientFactory.createDefaultFactory().create(config);
		
		return client;
	}

}
