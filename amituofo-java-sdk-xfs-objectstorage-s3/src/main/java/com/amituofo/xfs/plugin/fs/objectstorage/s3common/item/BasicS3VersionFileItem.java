package com.amituofo.xfs.plugin.fs.objectstorage.s3common.item;

import java.io.InputStream;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.service.Item;

public abstract class BasicS3VersionFileItem<S3BUCKET extends BasicS3Bucketspace> extends BasicS3FileItem<S3BUCKET> implements OSDVersionFileItem {

	public boolean isDeleteMarker;

	public BasicS3VersionFileItem(S3BUCKET bucket, String key, String versionId) {
		super(bucket, key);
		this.versionId = versionId;
	}

	@Override
	public int getStatus() {
		return isDeleteMarker ? Item.ITEM_STATUS_DELETED : 0;
	}

	@Override
	public InputStream getContent() throws ServiceException {
		S3Object obj;
		try {
			obj = getS3Client().getObject(new GetObjectRequest(this.getBucketName(), this.getPath(), versionId));
		} catch (AmazonServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (SdkClientException e) {
			throw new ServiceException(e);
		}
		InputStream in = obj.getObjectContent();

		return in;
	}

	// @Override
	// public void copy(OSDFileItem source) throws ServiceException {
	// String sourceBucketName = source.getBucketName();
	// String sourceKey = source.getPath();
	// String targetBucketName = this.getBucketName();
	// String targetKey = URLUtils.catPath(this.getParent().getPath(), this.getName());
	// CopyObjectRequest request = new CopyObjectRequest(sourceBucketName, sourceKey, targetBucketName, targetKey)
	// .withSourceVersionId(versionId)
	// .withMetadataDirective(MetadataDirective.COPY);
	//
	// if (source instanceof OSDVersionFileItem) {
	// request.setSourceVersionId(source.getVersionId());
	// }
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
	public ObjectMetadata getMetadata() throws ServiceException {
		try {
			ObjectMetadata meta = getS3Client().getObjectMetadata(new GetObjectMetadataRequest(getBucketName(), getPath(), versionId));
			return meta;
		} catch (AmazonServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete() throws ServiceException {
		try {
			String key = (String) this.getPath();

			getS3Client().deleteVersion(getBucketName(), key, versionId);

			return true;
		} catch (AmazonServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	// @Override
	// public void upateProperties() throws ServiceException {
	// ObjectMetadata obj = getAndUpateProperties();
	// ((S3DefaultFileItem) this).etag = obj.getETag();
	// ((S3DefaultFileItem) this).storageClass = obj.getStorageClass();
	// ((S3DefaultVersionFileItem) this).versionId = obj.getVersionId();
	// }

}
