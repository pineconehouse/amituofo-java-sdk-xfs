package com.amituofo.xfs.plugin.fs.objectstorage.s3common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.HeadBucketRequest;
import com.amazonaws.services.s3.model.HeadBucketResult;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.item.BasicS3Bucketspace;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.ItemspaceConfig;
import com.amituofo.xfs.service.ProxyStatus;

public abstract class BasicS3FileSystemEntry<CONFIG extends BasicS3FileSystemEntryConfig, PREFERENCE extends BasicS3FileSystemPreference, BUCKETSPACE extends BasicS3Bucketspace>
		extends OSDFileSystemEntry<CONFIG, PREFERENCE, BUCKETSPACE> {

	private BUCKETSPACE defaultBucketSpace = null;
	private final Map<String, AmazonS3> s3ClientCache = new HashMap<String, AmazonS3>();
	private List<BUCKETSPACE> tempBucketList = null;

	@Override
	public void createItemSpace(ItemspaceConfig config) throws ServiceException {
		AmazonS3 s3Client = getS3Client(entryConfig.getBucketName());
		CreateBucketRequest createBucketReq = new CreateBucketRequest(config.getName());
		try {
			s3Client.createBucket(createBucketReq);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void modifyItemSpace(ItemspaceConfig itemspaceConfig) throws ServiceException {
		
	}
	
	@Override
	public void close() throws ServiceException {
		super.close();

		for (Iterator<AmazonS3> it = s3ClientCache.values().iterator(); it.hasNext();) {
			AmazonS3 client = it.next();
			if (client != null) {
				client.shutdown();
			}
		}
		s3ClientCache.clear();
	}

	@Override
	public void deleteItemSpace(String bucketName) throws ServiceException {
		AmazonS3 s3Client = getS3Client(bucketName);
		try {
			// BucketVersioningConfiguration versionconfig = s3Client.getBucketVersioningConfiguration(name);
			// versionconfig.setStatus(BucketVersioningConfiguration.OFF);
			// SetBucketVersioningConfigurationRequest versionReq = new SetBucketVersioningConfigurationRequest(name,versionconfig);
			// s3Client.setBucketVersioningConfiguration(versionReq );
			s3Client.deleteBucket(bucketName);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

		// try {
		// // Delete all objects from the bucket. This is sufficient
		// // for unversioned buckets. For versioned buckets, when you attempt to delete objects, Amazon S3 inserts
		// // delete markers for all objects, but doesn't delete the object versions.
		// // To delete objects from versioned buckets, delete all of the object versions before deleting
		// // the bucket (see below for an example).
		// ObjectListing objectListing = s3Client.listObjects(bucketName);
		// while (true) {
		// Iterator<S3ObjectSummary> objIter = objectListing.getObjectSummaries().iterator();
		// while (objIter.hasNext()) {
		// s3Client.deleteObject(bucketName, objIter.next().getKey());
		// }
		//
		// // If the bucket contains many objects, the listObjects() call
		// // might not return all of the objects in the first listing. Check to
		// // see whether the listing was truncated. If so, retrieve the next page of objects
		// // and delete them.
		// if (objectListing.isTruncated()) {
		// objectListing = s3Client.listNextBatchOfObjects(objectListing);
		// } else {
		// break;
		// }
		// }
		//
		// // Delete all object versions (required for versioned buckets).
		// VersionListing versionList = s3Client.listVersions(new ListVersionsRequest().withBucketName(bucketName));
		// while (true) {
		// Iterator<S3VersionSummary> versionIter = versionList.getVersionSummaries().iterator();
		// while (versionIter.hasNext()) {
		// S3VersionSummary vs = versionIter.next();
		// s3Client.deleteVersion(bucketName, vs.getKey(), vs.getVersionId());
		// }
		//
		// if (versionList.isTruncated()) {
		// versionList = s3Client.listNextBatchOfVersions(versionList);
		// } else {
		// break;
		// }
		// }
		//
		// // After all objects and object versions are deleted, delete the bucket.
		// s3Client.deleteBucket(bucketName);
		// } catch (AmazonServiceException e) {
		// // The call was transmitted successfully, but Amazon S3 couldn't process
		// // it, so it returned an error response.
		// e.printStackTrace();
		// } catch (SdkClientException e) {
		// // Amazon S3 couldn't be contacted for a response, or the client couldn't
		// // parse the response from Amazon S3.
		// e.printStackTrace();
		// }
	}

	// private AmazonS3 s3Client = null;

	public BasicS3FileSystemEntry(CONFIG entryConfig, PREFERENCE preference) {
		super(entryConfig, preference);
	}

	// public abstract FolderItem newFolderItemInstance(RootItem rootitem);

	// public abstract FileItem newFileItemInstance(RootItem rootitem);

	// public abstract OSDVersionFileItem newVersionFileItemInstance(RootItem rootitem);

	protected abstract AmazonS3 createClient(com.amazonaws.Protocol awsProtocol, ClientConfiguration clientConfig, CONFIG s3EntryConfig) throws ServiceException;

	@Override
	public FileSystemEntry refresh() throws ServiceException {
		defaultBucketSpace = null;
		return super.refresh();
	}

	@Override
	protected BUCKETSPACE createDefaultItemspace() throws ServiceException {
		if (defaultBucketSpace != null) {
			return defaultBucketSpace;
		}

		tempBucketList = listAccessibleItemspaces();
		if (StringUtils.isEmpty(entryConfig.getBucketName())) {
			if (tempBucketList.size() == 0) {
				throw new ServiceException("Unable to list buckets!");
			}
			defaultBucketSpace = tempBucketList.get(0);
		} else {
			// Create connection
			for (BUCKETSPACE bucketspace : tempBucketList) {
				if (bucketspace.getName().equalsIgnoreCase(entryConfig.getBucketName())) {
					defaultBucketSpace = bucketspace;
					break;
				}
			}

			if (defaultBucketSpace == null) {
				AmazonS3 s3Client = getS3Client(entryConfig.getBucketName());
				Bucket bucket = new Bucket();
				bucket.setName(entryConfig.getBucketName());
				defaultBucketSpace = createS3BucketItem(this, s3Client, bucket);
			}
		}

		return defaultBucketSpace;
	}

	protected abstract BUCKETSPACE createS3BucketItem(BasicS3FileSystemEntry<CONFIG, PREFERENCE, BUCKETSPACE> s3DefaultFileSystemEntry, AmazonS3 s3Client, Bucket bucket);

	@Override
	protected List<BUCKETSPACE> listAccessibleItemspaces() throws ServiceException {
		// List<BUCKETSPACE> buckets = new ArrayList<BUCKETSPACE>();
		if (tempBucketList != null) {
			tempBucketList.clear();
		} else {
			tempBucketList = new ArrayList<BUCKETSPACE>();
		}

		try {
			AmazonS3 s3Client = getS3Client(entryConfig.getBucketName());
			List<Bucket> namespaces;
//			try {
				namespaces = s3Client.listBuckets();
				for (int i = 0; i < namespaces.size(); i++) {
					Bucket namespace = namespaces.get(i);
					s3Client = getS3Client(namespace.getName());

					BUCKETSPACE bucket = createS3BucketItem(this, s3Client, namespace);

					tempBucketList.add(bucket);
				}
//			} catch (Exception e) {
//				BUCKETSPACE bucket = createS3BucketItem(this, s3Client, new Bucket(entryConfig.getBucketName()));
//				tempBucketList.add(bucket);
//			}

			return tempBucketList;
		} catch (AmazonServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean isAvailable() {
		try {
			// Create connection
			AmazonS3 s3Client = super.getDefaultItemspace().getS3Client();
			HeadBucketResult result = s3Client.headBucket(new HeadBucketRequest(entryConfig.getBucketName()));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private AmazonS3 getS3Client(String bucketName) throws ServiceException {
		bucketName = "default";
		AmazonS3 s3Client = s3ClientCache.get(bucketName);
		if (s3Client == null) {
			CONFIG s3EntryConfig = ((CONFIG) entryConfig);
			ClientConfiguration clientConfig = new ClientConfiguration();
			//
			// if (s3EntryConfig.getConnectionTimeout() > 0) {
			// // request timeout 导致传输大文件超时后失败
			// // clientConfig.setRequestTimeout(s3EntryConfig.getConnectionTimeout());
			// clientConfig.setConnectionTimeout(s3EntryConfig.getConnectionTimeout());
			// }

			com.amituofo.common.define.Protocol hcpProtocol = s3EntryConfig.getProtocol();
			com.amazonaws.Protocol awsProtocol = com.amazonaws.Protocol.valueOf(hcpProtocol.name());

			if (ProxyStatus.DISABLED != s3EntryConfig.getProxyStatus()) {
				if (ProxyStatus.USE_PRIVATE_SETTING == s3EntryConfig.getProxyStatus()) {
					clientConfig.setProxyHost(s3EntryConfig.getProxyHost());
					clientConfig.setProxyPort(s3EntryConfig.getProxyPort());
					// 强制配置为忽略认证，否则错误
					s3EntryConfig.setIgnoreSSLCertification(true);
					if (s3EntryConfig.isProxyAuthenticationRequired()) {
						clientConfig.setProxyUsername(s3EntryConfig.getProxyUsername());
						clientConfig.setProxyPassword(s3EntryConfig.getProxyPassword());
					}
				} else if (ProxyStatus.USE_GLOBAL_SETTING == s3EntryConfig.getProxyStatus()) {
					// 强制配置为忽略认证，否则错误
					s3EntryConfig.setIgnoreSSLCertification(true);
					// String host = (s3EntryConfig.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyHost") :
					// System.getProperty("http.proxyHost"));
					// String port = (s3EntryConfig.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyPort") :
					// System.getProperty("http.proxyPort"));
					// String usr = (s3EntryConfig.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyUser") :
					// System.getProperty("http.proxyUser"));
					// String pwd = (s3EntryConfig.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyPassword") :
					// System.getProperty("http.proxyPassword"));
					//
					// if (StringUtils.isNotEmpty(host) && StringUtils.isNotEmpty(port)) {
					// clientConfig.setProxyHost(host);
					// clientConfig.setProxyPort(Integer.parseInt(port));
					// if (StringUtils.isNotEmpty(usr) && StringUtils.isNotEmpty(pwd)) {
					// clientConfig.setProxyUsername(usr);
					// clientConfig.setProxyPassword(pwd);
					// }
					// }
				}
			}

			s3Client = createClient(awsProtocol, clientConfig, s3EntryConfig);
			s3ClientCache.put(bucketName, s3Client);
		}

		return s3Client;
	}
}
