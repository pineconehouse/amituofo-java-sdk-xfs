package com.amituofo.xfs.plugin.fs.objectstorage.s3common.item;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketAccelerateConfiguration;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Rule;
import com.amazonaws.services.s3.model.BucketLoggingConfiguration;
import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.model.BucketReplicationConfiguration;
import com.amazonaws.services.s3.model.BucketTaggingConfiguration;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CORSRule;
import com.amazonaws.services.s3.model.GetBucketAclRequest;
import com.amazonaws.services.s3.model.GetBucketAnalyticsConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketAnalyticsConfigurationResult;
import com.amazonaws.services.s3.model.GetBucketEncryptionResult;
import com.amazonaws.services.s3.model.GetBucketInventoryConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketInventoryConfigurationResult;
import com.amazonaws.services.s3.model.GetBucketLifecycleConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.GetBucketMetricsConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketNotificationConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketPolicyRequest;
import com.amazonaws.services.s3.model.GetBucketReplicationConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketTaggingConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketVersioningConfigurationRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.ServerSideEncryptionByDefault;
import com.amazonaws.services.s3.model.ServerSideEncryptionConfiguration;
import com.amazonaws.services.s3.model.ServerSideEncryptionRule;
import com.amazonaws.services.s3.model.VersionListing;
import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDItemInstanceCreator;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.BasicS3FileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.BasicS3FileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.BasicS3FileSystemPreference;
import com.amituofo.xfs.service.ContentHash;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;
import com.amituofo.xfs.service.ItemspaceBase;
import com.amituofo.xfs.service.ListOption;

public abstract class BasicS3Bucketspace extends ItemspaceBase<BasicS3FileSystemEntry, BasicS3FileSystemPreference> implements S3Bucketspace, OSDItemInstanceCreator {
	protected final AmazonS3 s3Client;
	protected final String bucketName;
	protected final Bucket bucket;

	public BasicS3Bucketspace(BasicS3FileSystemEntry entry, AmazonS3 s3Client, Bucket bucket) {
		super(entry);
		this.s3Client = s3Client;
		this.bucketName = bucket.getName();
		this.bucket = bucket;
	}

	@Override
	public String getName() {
		return bucketName;
	}

	public AmazonS3 getS3Client() {
		return s3Client;
	}

	@Override
	public FileItem linkFile(String fullPath) throws ServiceException {
		if (StringUtils.isEmpty(fullPath) || "/".equals(fullPath)) {
			return null;
		} else {
			int last = fullPath.length() - 1;
			if (fullPath.charAt(last) == entry.getSeparatorChar()) {
				fullPath = fullPath.substring(0, last);
			}

			if (fullPath.charAt(0) == entry.getSeparatorChar()) {
				fullPath = fullPath.substring(1);
			}
		}

		return super.linkFile(fullPath);
	}

	@Override
	public FolderItem linkFolder(String fullPath) throws ServiceException {
		if (StringUtils.isEmpty(fullPath) || "/".equals(fullPath)) {
			fullPath = "";
		} else {
			int last = fullPath.length() - 1;
			if (fullPath.charAt(last) != entry.getSeparatorChar()) {
				fullPath += entry.getSeparatorChar();
			}

			if (fullPath.charAt(0) == entry.getSeparatorChar()) {
				fullPath = fullPath.substring(1);
			}
		}

		return super.linkFolder(fullPath);
	}

	@Override
	public String getEndpoint() {
		return ((BasicS3FileSystemEntryConfig) entry.getEntryConfig()).getEndpoint();
	}

	@Override
	public void list(final ListOption listOption, final ItemHandler handler) throws ServiceException {
		try {
			ItemFilter filter = listOption.getFilter();
			AmazonS3 s3Client = getS3Client();
			// Delete all objects from the bucket. This is sufficient
			// for unversioned buckets. For versioned buckets, when you attempt to delete objects, Amazon S3 inserts
			// delete markers for all objects, but doesn't delete the object versions.
			// To delete objects from versioned buckets, delete all of the object versions before deleting
			// the bucket (see below for an example).
			ListObjectsRequest listrequest = new ListObjectsRequest().withBucketName(bucketName);
			if (StringUtils.isNotEmpty(listOption.getPrefix())) {
				listrequest.withPrefix(listOption.getPrefix());
			}
			if (!listOption.isWithSubDirectory()) {
				listrequest.withDelimiter("/");
			}

			ObjectListing objectListing = s3Client.listObjects(listrequest);
			while (true) {
				Iterator<S3ObjectSummary> objIter = objectListing.getObjectSummaries().iterator();
				while (objIter.hasNext()) {
					// s3Client.deleteObject(bucketName, key);
					// Item item = newFileItemInstance(objIter.next().getKey());
					Item item = createItem(objIter.next());

					if (filter != null && !filter.accept(item)) {
						continue;
					}

					HandleFeedback result = handler.handle(ItemEvent.ITEM_FOUND, item);
					if (result == HandleFeedback.interrupted) {
						return;
					}
				}

				// If the bucket contains many objects, the listObjects() call
				// might not return all of the objects in the first listing. Check to
				// see whether the listing was truncated. If so, retrieve the next page of objects
				// and delete them.
				if (objectListing.isTruncated()) {
					objectListing = s3Client.listNextBatchOfObjects(objectListing);
				} else {
					break;
				}
			}

			if (listOption.isWithVersion()) {
				// Delete all object versions (required for versioned buckets).
				VersionListing versionList = s3Client.listVersions(new ListVersionsRequest().withBucketName(bucketName));
				while (true) {
					Iterator<S3VersionSummary> versionIter = versionList.getVersionSummaries().iterator();
					while (versionIter.hasNext()) {
						S3VersionSummary vs = versionIter.next();
						// s3Client.deleteVersion(bucketName, key, versionId);

						// Item item = newVersionFileItemInstance(vs.getKey(), vs.getVersionId());
						Item item = createItem(vs);

						if (filter != null && !filter.accept(item)) {
							continue;
						}

						HandleFeedback result = handler.handle(ItemEvent.ITEM_FOUND, item);
						if (result == HandleFeedback.interrupted) {
							return;
						}
					}

					if (versionList.isTruncated()) {
						versionList = s3Client.listNextBatchOfVersions(versionList);
					} else {
						break;
					}
				}
			}

			// After all objects and object versions are deleted, delete the bucket.
			// s3Client.deleteBucket(bucketName);
		} catch (AmazonServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			handler.handle(ItemEvent.EXEC_END, null);
		}
	}

	protected Item createItem(S3ObjectSummary s3ObjectSummary) {
		String key = entry.getSeparatorChar() + s3ObjectSummary.getKey();

		Item item;
		if (s3ObjectSummary.getETag() == null || (s3ObjectSummary.getSize() == 0 && key.charAt(key.length() - 1) == '/')) {
			item = ((ItemInstanceCreator) this).newFolderItemInstance(key);
		} else {
			item = ((ItemInstanceCreator) this).newFileItemInstance(key);
			((BasicS3FileItem) item).setContentHash(new ContentHash(s3ObjectSummary.getETag()));
			((BasicS3FileItem) item).versionId = null;
			((BasicS3FileItem) item).owner = s3ObjectSummary.getOwner().getDisplayName();// .toString();
			((BasicS3FileItem) item).storageClass = s3ObjectSummary.getStorageClass();
		}
		((ItemHiddenFunction) item).setName(URLUtils.getLastNameFromPath(key));
		((ItemHiddenFunction) item).setSize(s3ObjectSummary.getSize());
		((ItemHiddenFunction) item).setLastUpdateTime(s3ObjectSummary.getLastModified().getTime());
		((ItemHiddenFunction) item).setCreateTime(s3ObjectSummary.getLastModified().getTime());

		return item;
	}

	protected OSDVersionFileItem createItem(S3VersionSummary s3VersionSummary) {
		String key = entry.getSeparatorChar() + s3VersionSummary.getKey();
		String versionId = s3VersionSummary.getVersionId();

		OSDVersionFileItem item = ((OSDItemInstanceCreator) this).newVersionFileItemInstance(key, versionId);
		((ItemHiddenFunction) item).setName(URLUtils.getLastNameFromPath(s3VersionSummary.getKey()));
		((ItemHiddenFunction) item).setSize(s3VersionSummary.getSize());
		((ItemHiddenFunction) item).setLastUpdateTime(s3VersionSummary.getLastModified().getTime());
		((ItemHiddenFunction) item).setCreateTime(s3VersionSummary.getLastModified().getTime());

		((BasicS3VersionFileItem) item).setContentHash(new ContentHash(s3VersionSummary.getETag()));
		// ((S3DefaultVersionFileItem)item).versionId=s3VersionSummary;
		((BasicS3VersionFileItem) item).owner = s3VersionSummary.getOwner().getDisplayName();// .toString();
		((BasicS3VersionFileItem) item).storageClass = s3VersionSummary.getStorageClass();
		
		return item;
	}

	public BucketVersioningConfiguration getBucketVersioningConfiguration() {
		BucketVersioningConfiguration bucketVersioningConfiguration = this.getS3Client().getBucketVersioningConfiguration(new GetBucketVersioningConfigurationRequest(bucketName));
		return bucketVersioningConfiguration;
	}

	public BucketLoggingConfiguration getBucketLoggingConfiguration() {
		BucketLoggingConfiguration bucketLoggingConfiguration = this.getS3Client().getBucketLoggingConfiguration(bucketName);
		return bucketLoggingConfiguration;
	}

	public BucketReplicationConfiguration getBucketReplicationConfiguration() {
		BucketReplicationConfiguration config = this.getS3Client().getBucketReplicationConfiguration(new GetBucketReplicationConfigurationRequest(bucketName));
		return config;
	}

	public BucketAccelerateConfiguration getBucketAccelerateConfiguration() {
		BucketAccelerateConfiguration bucketAccelerateConfiguration = this.getS3Client().getBucketAccelerateConfiguration(bucketName);
		return bucketAccelerateConfiguration;
	}

	public ServerSideEncryptionConfiguration getServerSideEncryptionConfiguration() {
		ServerSideEncryptionConfiguration bucketEncryption = this.getS3Client().getBucketEncryption(bucketName).getServerSideEncryptionConfiguration();
		return bucketEncryption;
	}

	public String getBucketLocation() {
		String bucketLocation = this.getS3Client().getBucketLocation(new GetBucketLocationRequest(bucketName));
		return bucketLocation;
	}

	public Owner getOwner() {
		return bucket.getOwner();
	}

	public Date getCreationDate() {
		return bucket.getCreationDate();
	}

	public boolean isRequesterPaysEnabled() {
		return this.getS3Client().isRequesterPaysEnabled(bucketName);
	}

	public void getConfiguration() {
		System.out.println(bucketName);
		try {
			System.out.println("-BucketVersioningConfiguration-------------------------------------------------------------------");
			BucketVersioningConfiguration bucketVersioningConfiguration = this.getS3Client()
					.getBucketVersioningConfiguration(new GetBucketVersioningConfigurationRequest(bucketName));
			if (bucketVersioningConfiguration != null) {
				System.out.println(bucketVersioningConfiguration.isMfaDeleteEnabled());
				System.out.println(bucketVersioningConfiguration.getStatus());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("-BucketAccelerateConfiguration-------------------------------------------------------------------");
			BucketAccelerateConfiguration bucketAccelerateConfiguration = this.getS3Client().getBucketAccelerateConfiguration(bucketName);
			if (bucketAccelerateConfiguration != null) {
				System.out.println(bucketAccelerateConfiguration.getStatus());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("-BucketLifecycleConfiguration-------------------------------------------------------------------");
			BucketLifecycleConfiguration bucketLifecycleConfiguration = this.getS3Client().getBucketLifecycleConfiguration(new GetBucketLifecycleConfigurationRequest(bucketName));
			if (bucketLifecycleConfiguration != null) {
				List<Rule> lifecycleRules = bucketLifecycleConfiguration.getRules();
				if (lifecycleRules != null) {
					for (Rule rule : lifecycleRules) {
						System.out.println(rule.getExpirationDate());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("-BucketCrossOriginConfiguration-------------------------------------------------------------------");
			BucketCrossOriginConfiguration bucketCrossOriginConfiguration = this.getS3Client().getBucketCrossOriginConfiguration(bucketName);
			if (bucketCrossOriginConfiguration != null) {
				List<CORSRule> corsRules = bucketCrossOriginConfiguration.getRules();
				if (corsRules != null) {
					for (CORSRule corsRule : corsRules) {
						System.out.println(corsRule.getId());
						System.out.println(corsRule.getMaxAgeSeconds());
						System.out.println(corsRule.getAllowedHeaders());
						System.out.println(corsRule.getAllowedMethods());
						System.out.println(corsRule.getAllowedOrigins());
						System.out.println(corsRule.getExposedHeaders());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("-BucketWebsiteConfiguration-------------------------------------------------------------------");
			BucketWebsiteConfiguration bucketWebsiteConfiguration = this.getS3Client().getBucketWebsiteConfiguration(bucketName);
			if (bucketWebsiteConfiguration != null) {
				System.out.println(bucketWebsiteConfiguration.getErrorDocument());
				System.out.println(bucketWebsiteConfiguration.getIndexDocumentSuffix());
				System.out.println(bucketWebsiteConfiguration.getRedirectAllRequestsTo());
				System.out.println(bucketWebsiteConfiguration.getRoutingRules());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("-GetBucketEncryptionResult-------------------------------------------------------------------");
			GetBucketEncryptionResult bucketEncryption = this.getS3Client().getBucketEncryption(bucketName);
			List<ServerSideEncryptionRule> encryptionRules = bucketEncryption.getServerSideEncryptionConfiguration().getRules();
			if (encryptionRules != null) {
				for (ServerSideEncryptionRule serverSideEncryptionRule : encryptionRules) {
					ServerSideEncryptionByDefault setting = serverSideEncryptionRule.getApplyServerSideEncryptionByDefault();
					System.out.println(setting.getKMSMasterKeyID());
					System.out.println(setting.getSSEAlgorithm());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("-BucketLoggingConfiguration-------------------------------------------------------------------");
			BucketLoggingConfiguration bucketLoggingConfiguration = this.getS3Client().getBucketLoggingConfiguration(bucketName);
			if (bucketLoggingConfiguration != null) {
				bucketLoggingConfiguration.getDestinationBucketName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("-GetBucketInventoryConfigurationResult-------------------------------------------------------------------");
			GetBucketInventoryConfigurationResult bucketInventoryConfigurationResult = this.getS3Client()
					.getBucketInventoryConfiguration(new GetBucketInventoryConfigurationRequest(bucketName, "111"));
			if (bucketInventoryConfigurationResult != null) {
				bucketInventoryConfigurationResult.getInventoryConfiguration();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("-BucketLocation-------------------------------------------------------------------");
			String bucketLocation = this.getS3Client().getBucketLocation(new GetBucketLocationRequest(bucketName));
			System.out.println(bucketLocation);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("-BucketNotificationConfiguration-------------------------------------------------------------------");
			BucketNotificationConfiguration bucketNotificationConfiguration = this.getS3Client()
					.getBucketNotificationConfiguration(new GetBucketNotificationConfigurationRequest(bucketName));
			bucketNotificationConfiguration.getConfigurations();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("-MetricsConfiguration-------------------------------------------------------------------");
			this.getS3Client().getBucketMetricsConfiguration(new GetBucketMetricsConfigurationRequest(bucketName, "111"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("-BucketPolicy-------------------------------------------------------------------");
			this.getS3Client().getBucketPolicy(new GetBucketPolicyRequest(bucketName));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("-BucketAcl-------------------------------------------------------------------");
			this.getS3Client().getBucketAcl(new GetBucketAclRequest(bucketName));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("-ReplicationConfiguration-------------------------------------------------------------------");
			this.getS3Client().getBucketReplicationConfiguration(new GetBucketReplicationConfigurationRequest(bucketName));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("-BucketTaggingConfiguration-------------------------------------------------------------------");
			BucketTaggingConfiguration config = this.getS3Client().getBucketTaggingConfiguration(new GetBucketTaggingConfigurationRequest(bucketName));
			if (config != null) {
				System.out.println(config.getAllTagSets());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println("-AnalyticsConfiguration-------------------------------------------------------------------");
			GetBucketAnalyticsConfigurationResult getBucketAnalyticsConfigurationResult = this.getS3Client()
					.getBucketAnalyticsConfiguration(new GetBucketAnalyticsConfigurationRequest(bucketName, "111"));
			getBucketAnalyticsConfigurationResult.getAnalyticsConfiguration().getStorageClassAnalysis();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("--------------------------------------------------------------------");
	}

	// protected FolderItem createDirItem(String dirKey) throws ServiceException {
	// String key = getPathSeparator() + dirKey;
	//
	// FolderItem item = ((ItemInstanceCreator)itemspace).newFolderItemInstance(key);
	// ((ItemHiddenFunction)item).setName(URLUtils.getLastNameFromPath(key));
	// // item.setData(hcpObjectEntry.getKey());
	//// ((ItemInnerFunc)item).setPath(fileSystemEntry.getEntryConfig().getRootPath() + dirKey);
	// // item.setParent(this);
	// // item.setType(ItemType.Directory);
	// // item.setSize(null);
	//
	// // item.setSummary(hcpObjectEntry);
	// return item;
	// }

}
