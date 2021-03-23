package com.amituofo.xfs.plugin.fs.objectstorage.azure.blobs.item;

import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.amituofo.common.api.ObjectHandler;
import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.DigestUtils;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.common.item.AzureStorageFileItem;
import com.amituofo.xfs.service.ContentHash;
import com.amituofo.xfs.service.ContentWriter;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemProperties;
import com.amituofo.xfs.service.ItemType;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.models.AccessTier;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobItemProperties;
import com.azure.storage.blob.models.BlobListDetails;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.azure.storage.blob.options.BlobBeginCopyOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.sas.SasProtocol;

public class BlobFileItem extends BlobItemBase implements OSDFileItem, AzureStorageFileItem {
	private BlobItemProperties properties;
	protected String versionId = "";

	public BlobFileItem(BlobContainerspace namespace, String key) {
		super(namespace, key);
	}

	public BlobItemProperties getBlobProperties() {
		return properties;
	}

	@Override
	public void upateProperties() throws ServiceException {
		BlobClient client = this.getContainerClient().getBlobClient(this.getPath());
		upateProperties(client);
	}

	protected void upateProperties(BlobClient client) throws ServiceException {
		if (!client.exists()) {
			return;
		}

		BlobProperties prop = client.getProperties();
		OffsetDateTime time = prop.getLastModified();
		if (time != null) {
			this.setLastUpdateTime(time.toInstant().toEpochMilli());
		}
		time = prop.getCreationTime();
		if (time != null) {
			this.setCreateTime(time.toInstant().toEpochMilli());
		}

		this.setSize(prop.getBlobSize());
		this.properties = new BlobItemProperties();
		this.properties.setAccessTier(prop.getAccessTier());
		this.properties.setAccessTierChangeTime(prop.getAccessTierChangeTime());
		// this.properties. setAccessTierInferred( prop. );
		this.properties.setArchiveStatus(prop.getArchiveStatus());
		this.properties.setBlobSequenceNumber(prop.getBlobSequenceNumber());
		this.properties.setBlobType(prop.getBlobType());
		this.properties.setCacheControl(prop.getCacheControl());
		this.properties.setContentDisposition(prop.getContentDisposition());
		this.properties.setContentEncoding(prop.getContentEncoding());
		this.properties.setContentLanguage(prop.getContentLanguage());
		this.properties.setContentLength(prop.getBlobSize());
		this.properties.setContentMd5(prop.getContentMd5());
		this.properties.setContentType(prop.getContentType());
		this.properties.setCopyCompletionTime(prop.getCopyCompletionTime());
		this.properties.setCopyId(prop.getCopyId());
		this.properties.setCopyProgress(prop.getCopyProgress());
		this.properties.setCopySource(prop.getCopySource());
		this.properties.setCopyStatus(prop.getCopyStatus());
		this.properties.setCopyStatusDescription(prop.getCopyStatusDescription());
		this.properties.setCreationTime(prop.getCreationTime());
		this.properties.setCustomerProvidedKeySha256(prop.getEncryptionKeySha256());
		// this.properties. setDeletedTime( prop. );
		// this.properties. setDestinationSnapshot( prop. );
		this.properties.setEncryptionScope(prop.getEncryptionScope());
		this.properties.setETag(prop.getETag());
		// this.properties. setIncrementalCopy( prop. );
		this.properties.setLastModified(prop.getLastModified());
		this.properties.setLeaseDuration(prop.getLeaseDuration());
		this.properties.setLeaseState(prop.getLeaseState());
		this.properties.setLeaseStatus(prop.getLeaseStatus());
		// this.properties. setRehydratePriority( prop. );
		// this.properties. setRemainingRetentionDays( prop. );
		// this.properties. setSealed( prop. );
		// this.properties. setServerEncrypted( prop. );
		this.properties.setTagCount(prop.getTagCount() != null ? prop.getTagCount().intValue() : 0);
	}

	public Map<String, String> getTags() {
		BlobClient client = this.getContainerClient().getBlobClient(this.getPath());
		// BlobClient client = this.getBlobContainerClient().getBlobClient(this.getPath());
		Map<String, String> tags = client.getTags();
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		BlobClient client = this.getContainerClient().getBlobClient(this.getPath());
		client.setTags(tags);
	}

	@Override
	public Map<String, String> getMetadata() {
		BlobClient client = this.getContainerClient().getBlobClient(this.getPath());
		return client.getProperties().getMetadata();
	}

	@Override
	public void setMetadata(Map<String, String> metadata) throws ServiceException {
		try {
			BlobClient client = this.getContainerClient().getBlobClient(this.getPath());
			client.setMetadata(metadata);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	public void setAccessTier(AccessTier tier) {
		BlobClient client = this.getContainerClient().getBlobClient(this.getPath());
		client.setAccessTier(tier);
	}

//	public void setAccessTier(AccessTier tier) {
//		BlobClient client = this.getContainerClient().getBlobClient(this.getPath());
//		// BlobClient client = this.getBlobContainerClient().getBlobClient(this.getPath());
//		client.
//	}
	
	@Override
	public String getETag() {
		return getContentHash().getHashCode();
	}

	@Override
	public ContentHash getContentHash() {
		ContentHash ch = super.getContentHash();
		if (ch == null) {
			ch = new ContentHash(DigestUtils.format2Hex(properties.getContentMd5()));
			super.setContentHash(ch);
		}
		return ch;
	}

	@Override
	public String getVersionId() {
		return versionId;
	}

	@Override
	public String getKey() {
		return this.getPath();
	}

	@Override
	public void setKey(String key) {
		this.setPath(key);
	}

	@Override
	public String getOwner() {
		return "";
	}

	// @Override
	// public String getStorageClass() {
	// return "";
	// }

	@Override
	public InputStream getContent() throws ServiceException {
		BlobClient client = getContainerClient().getBlobClient(this.getPath());
		InputStream in = client.openInputStream();
		return in;
	}

	@Override
	public ContentWriter getContentWriter() {
		return new BlobContentWriter(this);
	}

	@Override
	public void listVersions(ObjectHandler<Integer, OSDVersionFileItem> event) {
		PagedIterable<BlobItem> itemversions = getContainerClient()
				.listBlobs(new ListBlobsOptions().setPrefix(this.getPath()).setDetails(new BlobListDetails().setRetrieveVersions(true)), null);
		for (Iterator<BlobItem> it = itemversions.iterator(); it.hasNext();) {
			BlobItem blobItem = (BlobItem) it.next();
			// System.out.println("\t" + blobItem.getName() + "\t" + blobItem.getVersionId() + "\t"+blobItem.getProperties().getContentLength());
			BlobVersionFileItem versionFileItem = (BlobVersionFileItem) itemspace.newVersionFileItemInstance(this.getPath(), blobItem.getVersionId());
			setItemProperties(versionFileItem, blobItem, blobItem.getProperties());

			event.handle(ItemEvent.ITEM_FOUND, versionFileItem);
		}

		event.handle(ItemEvent.EXEC_END, null);
	}

	// public void listACL(ObjectHandler<Integer, PermissionGrant> event) {
	// try {
	// // TODO
	// AccessControlList acl = getHcpClient().getObjectACL(new GetACLRequest(this.getPath())); // .withDeletedObject(isShowDeletedObjects())
	// Collection<PermissionGrant> allps = acl.getAllPermissions();
	// for (PermissionGrant permissionGrant : allps) {
	// event.handle(ItemEvent.ITEM_FOUND, permissionGrant);
	// }
	// event.handle(ItemEvent.EXEC_END, null);
	// } catch (Exception e) {
	// event.exceptionCaught(null, e);
	// }

	// BlobClient client = getBlobContainerClient().getBlobClient(this.getPath());
	// client.
	// }

	// public HCPMetadataItem createMetadata(String name) {
	// HCPMetadataItem metaItem =itemspace.newMetadataFileItemInstance(this.getKey(), new HCPMetadataSummary(name));
	// setHCPItemProperties(metaItem, this.getSummary());
	//
	// return metaItem;
	// }
	//
	// public void putMetadata(S3CompatibleMetadata s3CompatibleMetadata) throws ServiceException {
	// try {
	// getHcpClient().putMetadata(getKey(), s3CompatibleMetadata);
	// } catch (InvalidResponseException e) {
	// throw new ServiceException(e.getMessage(), e);
	// } catch (HSCException e) {
	// throw new ServiceException(e);
	// }
	// }

	// public HCPMetadataItem[] listMetadatas() throws ServiceException {
	// try {
	//
	// HCPMetadataSummarys metas = getHcpClient().listMetadatas(new
	// ListMetadataRequest(this.getKey()).withDeletedObject(itemspace.isShowDeletedObjects()));
	// Collection<HCPMetadataSummary> c = metas.getMetadatas();
	//
	// HCPMetadataItem[] metaItems = new HCPMetadataItem[c.size()];
	// int i = 0;
	// for (HCPMetadataSummary hcpMetadataSummary : c) {
	// metaItems[i] =itemspace.newMetadataFileItemInstance(this.getKey(), hcpMetadataSummary);
	// setHCPItemProperties(metaItems[i], this.getSummary());
	// i++;
	// }
	//
	// return metaItems;
	// } catch (InvalidResponseException e) {
	// throw new ServiceException(e.getMessage(), e);
	// } catch (HSCException e) {
	// throw new ServiceException(e);
	// }
	// }

	@Override
	public boolean delete() throws ServiceException {
		// try {
		getContainerClient().getBlobClient(this.getPath()).delete();
		return true;
		// } catch (InvalidResponseException e) {
		// throw new ServiceException(e.getMessage(), e);
		// } catch (HSCException e) {
		// throw new ServiceException(e);
		// }
	}

	@Override
	public void copy(OSDFileItem source) throws ServiceException {
		BlobFileItem sourceBlobItem = (BlobFileItem) source;
		String copySource = sourceBlobItem.getContainerClient().getBlobClient(sourceBlobItem.getPath()).getBlobUrl();

		BlobClient targetClient = getContainerClient().getBlobClient(this.getPath());
		targetClient.beginCopy(new BlobBeginCopyOptions(copySource));

		// String sourceNamespaceName = source.getBucketName();
		// String sourceKey = source.getKey();
		// // String targetNamespaceName = this.itemspace.getNamespace();
		// String targetKey = URLUtils.catPath(this.getParent().getPath(), this.getName());
		//
		// BlobFileItem sourceBlobItem = (BlobFileItem) source;
		// String copySource = sourceBlobItem.getBlobContainerClient().getBlobClient(sourceBlobItem.getPath()).getBlobUrl();
		//
		// BlobClient targetClient = getBlobContainerClient().getBlobClient(this.getPath());
		//
		//// targetClient.beginCopy(new BlobBeginCopyOptions("https://song99.blob.core.windows.net/quickstartblobs1/LGPL2.txt"));
		// targetClient.beginCopy(new BlobBeginCopyOptions(copySource));
	}

	@Override
	public void rename(String newname) throws ServiceException {
		try {
			ValidUtils.invalidIfEmpty(newname, "A new name must be specified!");
			ValidUtils.invalidIfEqual(newname, this.getName(), "New name must be different with current name!");
		} catch (InvalidParameterException e) {
			throw new ServiceException(e);
		}

		BlobFileItem cloneItem = (BlobFileItem) this.clone();

		String parentPath = URLUtils.getParentPath(this.getPath(), this.getPathSeparator(), getFileSystemEntry().getRootPath());
		cloneItem.setName(newname);
		cloneItem.setPath(parentPath + newname);

		cloneItem.copy(this);

		if (cloneItem.exists()) {
			this.delete();
		}
	}

	@Override
	public boolean exists() throws ServiceException {
		// Response<Boolean> response = getBlobContainerClient().existsWithResponse(null, null);
		// return response.getValue();
		BlobClient client = this.getContainerClient().getBlobClient(this.getPath());
		return client.exists();
	}

	@Override
	public Item clone() {
		BlobFileItem clone = (BlobFileItem) super.clone();
		clone.properties = this.properties;
		return clone;
	}

	 @Override
	public URL generateSasUrl(boolean readPermission, boolean writePermission, boolean deletePermission, Date expiration) throws ServiceException {
		URL url = null;
		try {
			// Set the presigned URL to expire after one hour.
			// java.util.Date expiration = new java.util.Date();
			// long expTimeMillis = expiration.getTime();
			// expTimeMillis += 1000 * 60 * 60;
			// expiration.setTime(expTimeMillis);

			// Generate the presigned URL.
			// System.out.println("Generating pre-signed URL.");

			BlobClient client = this.getContainerClient().getBlobClient(this.getPath());
			// OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(1);
			Instant instant = expiration.toInstant();
//			ZoneOffset current = ZoneOffset.systemDefault().getRules().getOffset(instant);
			ZoneOffset current = ZoneId.systemDefault().getRules().getOffset(instant);
			// OffsetDateTime expiryTime =instant.atOffset(ZoneOffset.UTC);
			OffsetDateTime expiryTime = instant.atOffset(current);
			// OffsetDateTime expiryTime = ZoneOffset.systemDefault().getRules().getOffset(expiration.toInstant());
			BlobSasPermission permission = new BlobSasPermission().setReadPermission(readPermission).setDeletePermission(deletePermission).setWritePermission(writePermission);

			BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(expiryTime, permission).setStartTime(OffsetDateTime.now()).setProtocol(SasProtocol.HTTPS_ONLY)
			// .setContentDisposition(contentDisposition)
			;

			String urlstr = client.generateSas(values); // Client must be authenticated via StorageSharedKeyCredential
			url = new URL(client.getBlobUrl() + "?" + urlstr);
			// System.out.println("Pre-Signed URL: " + url.toString());
			// } catch (AmazonServiceException e) {
			// throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

		return url;
	}

	public static BlobFileItem setItemProperties(BlobFileItem item, BlobItem blobItem, BlobItemProperties prop) {
		String name = URLUtils.getLastNameFromPath(blobItem.getName());
		((ItemHiddenFunction) item).setName(name);

		OffsetDateTime time = prop.getLastModified();
		if (time != null) {
			item.setLastUpdateTime(time.toInstant().toEpochMilli());
		}
		time = prop.getCreationTime();
		if (time != null) {
			item.setCreateTime(time.toInstant().toEpochMilli());
		}

		item.setSize(prop.getContentLength());

		item.properties = prop;
		return item;
	}

	@Override
	public String getURL() {
		// https://song99.blob.core.windows.net/quickstartblobs1/LGPL2.txt11
//		BlobFileSystemEntryConfig config = this.getItemspace().getFileSystemEntry().getEntryConfig();
//		return config.getProtocol().name().toLowerCase()
//				+ "://"
//				+ config.getAccesskey()
//				+ "."
//				+ config.getEndpoint()
//				+ "/"
//				+ this.getItemspace().getName()
//				+ URLUtils.catPath("/", this.getPath(), '/');
		
		BlobClient client = this.getContainerClient().getBlobClient(this.getPath());
		return client.getBlobUrl();
	}

	@Override
	public ItemProperties getProperties() throws ServiceException {
		ItemProperties p = super.getProperties();
		if (this.isFile()) {
			// properties = this.getItemProperties();
			upateProperties();
			p.add("extend:AccessTier", StringUtils.nullToString(properties.getAccessTier(), "-"));
			p.add("extend:AccessTierChangeTime", StringUtils.nullToString(properties.getAccessTierChangeTime(), "-"));
			p.add("extend:ArchiveStatus", StringUtils.nullToString(properties.getArchiveStatus(), "-"));
			p.add("extend:BlobSequenceNumber", StringUtils.nullToString(properties.getBlobSequenceNumber(), "-"));
			p.add("extend:BlobType", StringUtils.nullToString(properties.getBlobType(), "-"));
			p.add("extend:CacheControl", StringUtils.nullToString(properties.getCacheControl(), "-"));
			p.add("extend:ContentDisposition", StringUtils.nullToString(properties.getContentDisposition(), "-"));
			p.add("extend:ContentEncoding", StringUtils.nullToString(properties.getContentEncoding(), "-"));
			p.add("extend:ContentLanguage", StringUtils.nullToString(properties.getContentLanguage(), "-"));
			p.add("extend:ContentLength", StringUtils.nullToString(properties.getContentLength(), "-"));
			p.add("extend:ContentMd5", DigestUtils.format2Hex(properties.getContentMd5()));
			p.add("extend:ContentType", StringUtils.nullToString(properties.getContentType(), "-"));
			p.add("extend:CopyCompletionTime", StringUtils.nullToString(properties.getCopyCompletionTime(), "-"));
			p.add("extend:CopyId", StringUtils.nullToString(properties.getCopyId(), "-"));
			p.add("extend:CopyProgress", StringUtils.nullToString(properties.getCopyProgress(), "-"));
			p.add("extend:CopySource", StringUtils.nullToString(properties.getCopySource(), "-"));
			p.add("extend:CopyStatus", StringUtils.nullToString(properties.getCopyStatus(), "-"));
			p.add("extend:CopyStatusDescription", StringUtils.nullToString(properties.getCopyStatusDescription(), "-"));
			p.add("extend:CreationTime", StringUtils.nullToString(properties.getCreationTime(), "-"));
			p.add("extend:CustomerProvidedKeySha256", StringUtils.nullToString(properties.getCustomerProvidedKeySha256(), "-"));
			p.add("extend:DeletedTime", StringUtils.nullToString(properties.getDeletedTime(), "-"));
			p.add("extend:DestinationSnapshot", StringUtils.nullToString(properties.getDestinationSnapshot(), "-"));
			p.add("extend:EncryptionScope", StringUtils.nullToString(properties.getEncryptionScope(), "-"));
			p.add("extend:ETag", StringUtils.nullToString(properties.getETag(), "-"));
			p.add("extend:LastModified", StringUtils.nullToString(properties.getLastModified(), "-"));
			p.add("extend:LeaseDuration", StringUtils.nullToString(properties.getLeaseDuration(), "-"));
			p.add("extend:LeaseState", StringUtils.nullToString(properties.getLeaseState(), "-"));
			p.add("extend:LeaseStatus", StringUtils.nullToString(properties.getLeaseStatus(), "-"));
			p.add("extend:RehydratePriority", StringUtils.nullToString(properties.getRehydratePriority(), "-"));
			p.add("extend:RemainingRetentionDays", StringUtils.nullToString(properties.getRemainingRetentionDays(), "-"));
			p.add("extend:TagCount", StringUtils.nullToString(properties.getTagCount(), "-"));
			p.add("extend:IsAccessTierInferred", StringUtils.nullToString(properties.isAccessTierInferred(), "-"));
			p.add("extend:IsIncrementalCopy", StringUtils.nullToString(properties.isIncrementalCopy(), "-"));
			p.add("extend:IsSealed", StringUtils.nullToString(properties.isSealed(), "-"));
			p.add("extend:IsServerEncrypted", StringUtils.nullToString(properties.isServerEncrypted(), "-"));
		}
		return p;
	}

}
