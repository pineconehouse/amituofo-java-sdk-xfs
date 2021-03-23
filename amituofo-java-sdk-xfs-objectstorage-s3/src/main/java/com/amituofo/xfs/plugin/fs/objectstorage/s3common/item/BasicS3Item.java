package com.amituofo.xfs.plugin.fs.objectstorage.s3common.item;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDItemBase;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDItemInstanceCreator;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.service.ContentHash;
import com.amituofo.xfs.service.FileSystem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;

public abstract class BasicS3Item<S3BUCKET extends BasicS3Bucketspace> extends OSDItemBase<S3BUCKET> implements FileSystem {

	public BasicS3Item(S3BUCKET bucket, String key) {
		super(bucket, key);
	}

	public AmazonS3 getS3Client() {
		return itemspace.getS3Client();
	}

	public String getBucketName() {
		return itemspace.getName();
	}

	public String getEndpoint() {
		return this.itemspace.getEndpoint();
	}

	@Override
	public char getPathSeparator() {
		return '/';
	}

	@Override
	public void setPath(String path) {
		int len = path.length();
		if (len > 0 && path.charAt(0) == '/') {
			path = path.substring(1);
		}
		super.setPath(path);
	}
	
	protected Item createItem(S3ObjectSummary s3ObjectSummary) throws ServiceException {
		String key = getPathSeparator() + s3ObjectSummary.getKey();
		
		Item item;
		if (s3ObjectSummary.getETag() == null || (s3ObjectSummary.getSize() == 0 && key.charAt(key.length() - 1) == '/')) {
			item = ((ItemInstanceCreator) itemspace).newFolderItemInstance(key);
		} else {
			item = ((ItemInstanceCreator) itemspace).newFileItemInstance(key);
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
		String key = getPathSeparator() + s3VersionSummary.getKey();
		String versionId = s3VersionSummary.getVersionId();

		OSDVersionFileItem item = ((OSDItemInstanceCreator) itemspace).newVersionFileItemInstance(key, versionId);
		((ItemHiddenFunction) item).setName(URLUtils.getLastNameFromPath(s3VersionSummary.getKey()));
		((ItemHiddenFunction) item).setSize(s3VersionSummary.getSize());
		((ItemHiddenFunction) item).setLastUpdateTime(s3VersionSummary.getLastModified().getTime());
		((ItemHiddenFunction) item).setCreateTime(s3VersionSummary.getLastModified().getTime());
		
		((BasicS3VersionFileItem) item).isDeleteMarker = s3VersionSummary.isDeleteMarker();
		((BasicS3VersionFileItem) item).setContentHash(new ContentHash(s3VersionSummary.getETag()));
		((BasicS3VersionFileItem) item).versionId = s3VersionSummary.getVersionId();
		((BasicS3VersionFileItem) item).owner = s3VersionSummary.getOwner().getDisplayName();// .toString();
		((BasicS3VersionFileItem) item).storageClass = s3VersionSummary.getStorageClass();
		
		return item;
	}

	protected FolderItem createDirItem(String dirKey) throws ServiceException {
		String key = getPathSeparator() + dirKey;

		FolderItem item = ((ItemInstanceCreator) itemspace).newFolderItemInstance(key);
		((ItemHiddenFunction) item).setName(URLUtils.getLastNameFromPath(key));
		// item.setData(hcpObjectEntry.getKey());
		// ((ItemInnerFunc)item).setPath(fileSystemEntry.getEntryConfig().getRootPath() + dirKey);
		// item.setParent(this);
		// item.setType(ItemType.Directory);
		// item.setSize(null);

		// item.setSummary(hcpObjectEntry);
		return item;
	}

	@Override
	public boolean isFromSameSystem(Item item) {
		if (!(item instanceof BasicS3Item)) {
			return false;
		}

		if (!(item.getSystemName().equals(this.getSystemName()))) {
			return false;
		}

		BasicS3Item item1 = (BasicS3Item) item;
		if (!this.getEndpoint().equalsIgnoreCase(item1.getEndpoint())) {
			return false;
		}
		return true;
	}


}
