package com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2.item.B2Bucketspace;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.Itemspace;
import com.amituofo.xfs.service.ItemspaceConfig;
import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2AccountAuthorization;
import com.backblaze.b2.client.structures.B2Bucket;
import com.backblaze.b2.client.structures.B2CreateBucketRequest;

public class B2FileSystemEntry extends OSDFileSystemEntry<B2FileSystemEntryConfig, B2FileSystemPreference, B2Bucketspace> {

	private B2Bucketspace defaultB2Bucketspace = null;
	private final Map<String, B2StorageClient> b2ClientCache = new HashMap<String, B2StorageClient>();
	private List<B2Bucketspace> tempBucketList = null;
	private B2AccountAuthorization accountAut = null;

	public B2FileSystemEntry(B2FileSystemEntryConfig entryConfig, B2FileSystemPreference preference) {
		super(entryConfig, preference);
	}

	@Override
	public void createItemSpace(ItemspaceConfig config) throws ServiceException {
		B2StorageClient b2Client = getB2Client(entryConfig.getBucketName());

		B2BucketType bucketType = B2BucketType.allPrivate;
		if (config instanceof B2BucketspaceConfig) {
			bucketType = ((B2BucketspaceConfig) config).getBucketType();
		}
		B2CreateBucketRequest createBucketReq = B2CreateBucketRequest.builder(config.getName(), bucketType.toString()).build();
		try {
			b2Client.createBucket(createBucketReq);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void modifyItemSpace(ItemspaceConfig itemspaceConfig) throws ServiceException {
		B2Bucketspace itemspace = (B2Bucketspace)this.getItemspace(entryConfig.getBucketName());
		if (itemspace != null) {
			B2StorageClient b2Client = getB2Client(entryConfig.getBucketName());
			try {
				B2Bucket bucket = b2Client.updateBucket(((B2BucketspaceConfig) itemspaceConfig).getUpdateRequest());
				itemspace.setBucket(bucket);
			} catch (B2Exception e) {
				throw new ServiceException(e);
			}
		}
	}

	@Override
	public void close() throws ServiceException {
		super.close();

		for (Iterator<B2StorageClient> it = b2ClientCache.values().iterator(); it.hasNext();) {
			B2StorageClient client = it.next();
			if (client != null) {
				client.close();
			}
		}
		b2ClientCache.clear();
	}

	@Override
	public void deleteItemSpace(String bucketName) throws ServiceException {
		B2Bucketspace b2Bucket = ((B2Bucketspace) super.getItemspace(bucketName));
		if (b2Bucket == null) {
			// throw new ServiceException();
			return;
		}
		B2StorageClient b2Client = getB2Client(bucketName);
		try {
			String bucketId = b2Bucket.getBucket().getBucketId();
			// b2Client.deleteAllFilesInBucket(bucketId);
			b2Client.deleteBucket(bucketId);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public FileSystemEntry refresh() throws ServiceException {
		defaultB2Bucketspace = null;
		return super.refresh();
	}

	@Override
	protected B2Bucketspace createDefaultItemspace() throws ServiceException {
		if (defaultB2Bucketspace != null) {
			return defaultB2Bucketspace;
		}

		String bucketName = entryConfig.getBucketName();
		if (StringUtils.isNotEmpty(bucketName)) {
			B2StorageClient b2Client = getB2Client(bucketName);
			B2Bucket bucket = null;
			try {
				bucket = b2Client.getBucketOrNullByName(bucketName);
			} catch (B2Exception e) {
				throw new ServiceException(e);
			}

			if (bucket != null) {
				defaultB2Bucketspace = new B2Bucketspace(this, b2Client, bucket);
			} else {
				throw new ServiceException("Bucket " + bucketName + " not found!");
			}
		}

		if (defaultB2Bucketspace == null) {
			tempBucketList = listAccessibleItemspaces();
			if (tempBucketList.size() == 0) {
				throw new ServiceException("Unable to list buckets!");
			}

			defaultB2Bucketspace = tempBucketList.get(0);
		}

		return defaultB2Bucketspace;
	}

	@Override
	protected List<B2Bucketspace> listAccessibleItemspaces() throws ServiceException {
		// List<B2Bucketspace> buckets = new ArrayList<B2Bucketspace>();
		if (tempBucketList != null) {
			tempBucketList.clear();
		} else {
			tempBucketList = new ArrayList<B2Bucketspace>();
		}

		try {
			B2StorageClient b2Client = getB2Client(entryConfig.getBucketName());
			List<B2Bucket> namespaces = b2Client.buckets();
			for (int i = 0; i < namespaces.size(); i++) {
				B2Bucket namespace = namespaces.get(i);
				b2Client = getB2Client(namespace.getBucketName());

				B2Bucketspace bucket = new B2Bucketspace(this, b2Client, namespace);

				tempBucketList.add(bucket);
			}

			return tempBucketList;
		} catch (B2Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	public B2AccountAuthorization getAccountAuthorization() throws ServiceException {
		if (accountAut == null) {
			B2StorageClient b2Client = this.getDefaultItemspace().getB2Client();
			try {
				accountAut = b2Client.getAccountAuthorization();
			} catch (B2Exception e) {
				throw new ServiceException(e);
			}
		}

		return accountAut;
	}

	@Override
	public boolean isAvailable() {
		try {
			// Create connection
			B2StorageClient b2Client = super.getDefaultItemspace().getB2Client();
			b2Client.getFilePolicy();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private B2StorageClient getB2Client(String bucketName) throws ServiceException {
		bucketName = "default";
		B2StorageClient b2Client = b2ClientCache.get(bucketName);
		if (b2Client == null) {
			B2FileSystemEntryConfig s3EntryConfig = ((B2FileSystemEntryConfig) entryConfig);
			//
			// if (ProxyStatus.DISABLED != s3EntryConfig.getProxyStatus()) {
			// if (ProxyStatus.USE_PRIVATE_SETTING == s3EntryConfig.getProxyStatus()) {
			// clientConfig.setProxyHost(s3EntryConfig.getProxyHost());
			// clientConfig.setProxyPort(s3EntryConfig.getProxyPort());
			// // 强制配置为忽略认证，否则错误
			// s3EntryConfig.setIgnoreSSLCertification(true);
			// if (s3EntryConfig.isProxyAuthenticationRequired()) {
			// clientConfig.setProxyUsername(s3EntryConfig.getProxyUsername());
			// clientConfig.setProxyPassword(s3EntryConfig.getProxyPassword());
			// }
			// } else if (ProxyStatus.USE_GLOBAL_SETTING == s3EntryConfig.getProxyStatus()) {
			// // 强制配置为忽略认证，否则错误
			// s3EntryConfig.setIgnoreSSLCertification(true);
			// // String host = (s3EntryConfig.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyHost") :
			// // System.getProperty("http.proxyHost"));
			// // String port = (s3EntryConfig.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyPort") :
			// // System.getProperty("http.proxyPort"));
			// // String usr = (s3EntryConfig.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyUser") :
			// // System.getProperty("http.proxyUser"));
			// // String pwd = (s3EntryConfig.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyPassword") :
			// // System.getProperty("http.proxyPassword"));
			// //
			// // if (StringUtils.isNotEmpty(host) && StringUtils.isNotEmpty(port)) {
			// // clientConfig.setProxyHost(host);
			// // clientConfig.setProxyPort(Integer.parseInt(port));
			// // if (StringUtils.isNotEmpty(usr) && StringUtils.isNotEmpty(pwd)) {
			// // clientConfig.setProxyUsername(usr);
			// // clientConfig.setProxyPassword(pwd);
			// // }
			// // }
			// }
			// }

			b2Client = B2ClientFactory.getInstance().getB2StorageClient(s3EntryConfig.getApplicationKeyId(), s3EntryConfig.getApplicationKey());
			b2ClientCache.put(bucketName, b2Client);
		}

		return b2Client;
	}
}
