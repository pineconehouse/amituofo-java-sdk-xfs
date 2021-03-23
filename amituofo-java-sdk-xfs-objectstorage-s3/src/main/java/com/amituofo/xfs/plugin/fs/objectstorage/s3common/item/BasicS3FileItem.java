package com.amituofo.xfs.plugin.fs.objectstorage.s3common.item;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingResult;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.MetadataDirective;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.RestoreObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.VersionListing;
import com.amituofo.common.api.ObjectHandler;
import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.service.ContentHash;
import com.amituofo.xfs.service.ContentWriter;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemProperties;
import com.amituofo.xfs.service.ItemType;

public abstract class BasicS3FileItem<S3BUCKET extends BasicS3Bucketspace> extends BasicS3Item<S3BUCKET> implements S3FileItem {

	protected String versionId;
//	protected String etag;
	protected String owner;
	public String storageClass;

	public BasicS3FileItem(S3BUCKET bucket, String key) {
		super(bucket, key);
	}

	@Override
	public InputStream getContent() throws ServiceException {
		S3Object obj;
		try {
			obj = getS3Client().getObject(this.getBucketName(), this.getKey());
		} catch (AmazonServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (SdkClientException e) {
			throw new ServiceException(e);
		}
		InputStream in = obj.getObjectContent();

		return in;
	}

	@Override
	public ContentWriter getContentWriter() {
		return new BasicS3ContentWriter(this);
	}

	@Override
	public void rename(String newname) throws ServiceException {
		try {
			ValidUtils.invalidIfEmpty(newname, "A new name must be specified!");
			ValidUtils.invalidIfEqual(newname, this.getName(), "New name must be different with current name!");
		} catch (InvalidParameterException e) {
			throw new ServiceException(e);
		}

		BasicS3FileItem cloneItem = (BasicS3FileItem) this.clone();

		cloneItem.setName(newname);

		cloneItem.copy(this);

		if (cloneItem.exists()) {
			this.delete();
		}
	}

	@Override
	public URL generatePresignedUrl(HttpMethod method, Date expiration, ResponseHeaderOverrides responseHeader) throws ServiceException {
		URL url = null;
		try {
			// Set the presigned URL to expire after one hour.
			// java.util.Date expiration = new java.util.Date();
			// long expTimeMillis = expiration.getTime();
			// expTimeMillis += 1000 * 60 * 60;
			// expiration.setTime(expTimeMillis);

			// Generate the presigned URL.
			// System.out.println("Generating pre-signed URL.");
			GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(this.getBucketName(), this.getKey()).withMethod(method)
					.withExpiration(expiration);
			if (responseHeader != null) {
				generatePresignedUrlRequest.setResponseHeaders(responseHeader);
			}
			url = getS3Client().generatePresignedUrl(generatePresignedUrlRequest);

			// System.out.println("Pre-Signed URL: " + url.toString());
		} catch (AmazonServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

		return url;
	}

	@Override
	public void listVersions(ObjectHandler<Integer, OSDVersionFileItem> event) {
		try {
			VersionListing entrys = getS3Client().listVersions(this.getBucketName(), this.getPath());
			List<S3VersionSummary> versions = entrys.getVersionSummaries();
			for (S3VersionSummary version : versions) {
				event.handle(ItemEvent.ITEM_FOUND, createItem(version));
			}

			event.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			event.exceptionCaught(null, e);
		}
	}

	@Override
	public void copy(OSDFileItem source) throws ServiceException {
		String sourceBucketName = source.getItemspace().getName();
		String sourceKey = source.getPath();
		String targetBucketName = this.getItemspace().getName();
		String targetKey = URLUtils.catPath(this.getParent().getPath(), this.getName());
		CopyObjectRequest request = new CopyObjectRequest(sourceBucketName, sourceKey, targetBucketName, targetKey).withMetadataDirective(MetadataDirective.COPY);

		if (source instanceof OSDVersionFileItem) {
			request.setSourceVersionId(source.getVersionId());
		}

		try {
			getS3Client().copyObject(request);
		} catch (AmazonServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void putMetadata(ObjectMetadata metadata) throws ServiceException {
		try {
			// String oldVersionId = getVersionId();

			CopyObjectRequest req = new CopyObjectRequest(getItemspace().getName(), getKey(), getItemspace().getName(), getKey());
			req.withMetadataDirective(MetadataDirective.REPLACE).withNewObjectMetadata(metadata);

			getS3Client().copyObject(req);

			// 有些对象存储（tencent）对此支持不好导致当前版本被删除
			// if (StringUtils.isNotEmpty(oldVersionId)) {
			// getS3Client().deleteVersion(getBucketName(), getKey(), oldVersionId);
			// }
		} catch (AmazonServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public ObjectMetadata getMetadata() throws ServiceException {
		try {
			ObjectMetadata meta = getS3Client().getObjectMetadata(getBucketName(), getKey());
			return meta;
		} catch (AmazonServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void setObjectTagging(List<Tag> tags) throws ServiceException {
		try {
			SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(getBucketName(), getKey(), new ObjectTagging(tags));
			getS3Client().setObjectTagging(setObjectTaggingRequest);
		} catch (AmazonServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public List<Tag> getObjectTagging() throws ServiceException {
		try {
			GetObjectTaggingRequest getObjectTaggingRequest = new GetObjectTaggingRequest(getBucketName(), getKey());
			GetObjectTaggingResult result = getS3Client().getObjectTagging(getObjectTaggingRequest);
			return result.getTagSet();
		} catch (AmazonServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	public void listACL(ObjectHandler<Integer, Grant> event) {
		try {
			AccessControlList acl = getS3Client().getObjectAcl(getBucketName(), getKey());
			List<Grant> grants = acl.getGrantsAsList();
			for (Grant grant : grants) {
				// System.out.format("%s: %s\n", grant.getGrantee().getIdentifier(), grant.getPermission().toString());
				event.handle(ItemEvent.ITEM_FOUND, grant);
			}
			event.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			event.exceptionCaught(null, e);
		}
	}

	// public void setMetadata() throws ServiceException {
	// try {
	// ObjectMetadata meta = getS3Client().getObjectMetadata(bucketName, getActualPath());
	// return meta;
	// } catch (AmazonServiceException e) {
	// throw new ServiceException(e.getMessage(), e);
	// } catch (Exception e) {
	// throw new ServiceException(e);
	// }
	// }

	// @Override
	// public void copy(OSDFileItem source) throws ServiceException {
	// String sourceBucketName = source.getBucketName();
	// String sourceKey = source.getActualPath();
	// String targetBucketName = this.bucketName;
	// String targetKey = URLUtils.catPath(this.getActualPath(), this.getName());
	// CopyObjectRequest request = new CopyObjectRequest(sourceBucketName, sourceKey, targetBucketName, targetKey);
	//
	// try {
	// getS3Client().copyObject(request);
	// } catch (AmazonServiceException e) {
	// throw new ServiceException(e.getMessage(), e);
	// } catch (Exception e) {
	// throw new ServiceException(e);
	// }
	// }

	@Override
	public String getVersionId() {
		// 更新导致消耗大量时间
//		if (versionId == null) {
//			try {
//				upateProperties();
//			} catch (ServiceException e) {
//				e.printStackTrace();
//			}
//		}
		return versionId;
	}

	@Override
	public String getETag() {
		return this.getContentHash().getHashCode();
	}

	@Override
	public String getKey() {
		return this.getPath();
	}

	@Override
	public void setKey(String key) {
		this.setPath(key);
	}

	// @Override
	// public void setPath(String path) {
	// if (path.length() > 0 && path.charAt(0) == '/') {
	// path = path.substring(1);
	// }
	// super.setPath(path);
	// }

	// @Override
	// public String getVersionId() {
	// return versionId;
	// }

	@Override
	public String getOwner() {
		return owner;
	}

	// @Override
	public String getStorageClass() {
		return storageClass;
	}

	@Override
	public boolean delete() throws ServiceException {
		try {
			String key = (String) this.getPath();
			getS3Client().deleteObject(this.getBucketName(), key);
			return true;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	public void restoreObject(int expirationInDays) throws ServiceException {
		try {
			String key = (String) this.getPath();
			getS3Client().restoreObject(new RestoreObjectRequest(this.getBucketName(), key, expirationInDays<=0?-1:expirationInDays));
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean exists() throws ServiceException {
		try {
			return getS3Client().doesObjectExist(this.getBucketName(), this.getPath());
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void upateProperties() throws ServiceException {
		ObjectMetadata obj = getAndUpateProperties();
		((BasicS3FileItem) this).versionId = obj.getVersionId();
		((BasicS3FileItem) this).storageClass = obj.getStorageClass();
		((BasicS3FileItem) this).setContentHash(new ContentHash(obj.getETag()));
	}

	@Override
	public String getURL() {
		// https://song99.s3.ap-southeast-1.amazonaws.com/RUNNING.txt
		OSDFileSystemEntryConfig config = (OSDFileSystemEntryConfig) this.getFileSystemEntry().getEntryConfig();
		String endpoint = config.getEndpoint().toLowerCase();
		String url = (config.isUseSSL() ? "https://" : "http://") + this.getBucketName() + "." + endpoint + "/";
		url = URLUtils.catPath(url, this.getPath(), '/');

		return url;
	}

	protected ObjectMetadata getAndUpateProperties() throws ServiceException {
		try {
			ObjectMetadata obj = getS3Client().getObjectMetadata(getBucketName(), this.getPath());
			// getS3Client().getObjectMetadata(this.getBucketName(), this.getKey()).getVersionId();
			this.setSize(obj.getContentLength());
			((ItemHiddenFunction) this).setSize(obj.getContentLength());
			((ItemHiddenFunction) this).setLastUpdateTime(obj.getLastModified().getTime());
			((ItemHiddenFunction) this).setCreateTime(obj.getLastModified().getTime());

			return obj;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public ItemProperties getProperties() throws ServiceException {
		ItemProperties p = super.getProperties();
		if (this.isFile()) {
			ObjectMetadata om = getAndUpateProperties();
			p.add("extend:CacheControl", StringUtils.nullToString(om.getCacheControl(), "-"));
			p.add("extend:ContentDisposition", StringUtils.nullToString(om.getContentDisposition(), "-"));
			p.add("extend:ContentEncoding", StringUtils.nullToString(om.getContentEncoding(), "-"));
			p.add("extend:ContentLanguage", StringUtils.nullToString(om.getContentLanguage(), "-"));
			p.add("extend:ContentLength", StringUtils.nullToString(om.getContentLength(), "-"));
			p.add("extend:ContentMD5", StringUtils.nullToString(om.getContentMD5(), "-"));
			p.add("extend:ContentRange", StringUtils.nullToString(om.getContentRange(), "-"));
			p.add("extend:ContentType", StringUtils.nullToString(om.getContentType(), "-"));
			p.add("extend:ETag", StringUtils.nullToString(om.getETag(), "-"));
			p.add("extend:ExpirationTime", StringUtils.nullToString(om.getExpirationTime(), "-"));
			p.add("extend:ExpirationTimeRuleId", StringUtils.nullToString(om.getExpirationTimeRuleId(), "-"));
			p.add("extend:HttpExpiresDate", StringUtils.nullToString(om.getHttpExpiresDate(), "-"));
			p.add("extend:InstanceLength", StringUtils.nullToString(om.getInstanceLength(), "-"));
			p.add("extend:LastModified", StringUtils.nullToString(om.getLastModified(), "-"));
			p.add("extend:OngoingRestore", StringUtils.nullToString(om.getOngoingRestore(), "-"));
			p.add("extend:PartCount", StringUtils.nullToString(om.getPartCount(), "-"));
			// p.add(" extend:RawMetadata", StringUtils.nullToString(om.getRawMetadata() ,"-"));
			// p.add(" extend:RawMetadataValue(String StringUtils.nullToString(om.getRawMetadataValue(String) ,"-"));
			p.add("extend:ReplicationStatus", StringUtils.nullToString(om.getReplicationStatus(), "-"));
			p.add("extend:RestoreExpirationTime", StringUtils.nullToString(om.getRestoreExpirationTime(), "-"));
			p.add("extend:ServerSideEncryption", StringUtils.nullToString(om.getServerSideEncryption(), "-"));
			p.add("extend:SSEAlgorithm", StringUtils.nullToString(om.getSSEAlgorithm(), "-"));
			p.add("extend:SSEAwsKmsKeyId", StringUtils.nullToString(om.getSSEAwsKmsKeyId(), "-"));
			p.add("extend:SSECustomerAlgorithm", StringUtils.nullToString(om.getSSECustomerAlgorithm(), "-"));
			p.add("extend:SSECustomerKeyMd5", StringUtils.nullToString(om.getSSECustomerKeyMd5(), "-"));
		}
		return p;
	}

}
