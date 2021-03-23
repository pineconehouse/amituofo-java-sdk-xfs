package com.amituofo.xfs.plugin.fs.objectstorage.azure.datalake.item;

import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.common.item.AzureStorageFileItem;
import com.amituofo.xfs.service.ContentWriter;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemProperties;
import com.amituofo.xfs.service.ItemType;
import com.azure.storage.file.datalake.DataLakeFileClient;
import com.azure.storage.file.datalake.models.PathItem;
import com.azure.storage.file.datalake.models.PathProperties;
import com.azure.storage.file.datalake.sas.DataLakeServiceSasSignatureValues;
import com.azure.storage.file.datalake.sas.FileSystemSasPermission;

public class DataLakeFileItem extends DataLakeItemBase implements AzureStorageFileItem {
	// protected String versionId = null;

	public DataLakeFileItem(DataLakeContainerspace namespace, String key) {
		super(namespace, key);
	}

	@Override
	public Map<String, String> getMetadata() {
		DataLakeFileClient client = this.getContainerClient().getFileClient(this.getPath());
		return client.getProperties().getMetadata();
	}

	@Override
	public void setMetadata(Map<String, String> metadata) throws ServiceException {
		try {
			DataLakeFileClient client = this.getContainerClient().getFileClient(this.getPath());
			client.setMetadata(metadata);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	@Override
	public InputStream getContent() throws ServiceException {
		// DataLakeFileClient client = getContainerClient().getFileClient(this.getPath());
		// ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// client.read(stream);
		// // XXX TODO
		// InputStream in = new ByteArrayInputStream(stream.toByteArray());
		// return in;

		DataLakeFileClient client = getContainerClient().getFileClient(this.getPath());
		InputStream in = client.getBlockBlobClient().openInputStream();
		return in;
	}

	@Override
	public ContentWriter getContentWriter() {
		return new DataLakeContentWriter(this);
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

	// DataLakeClient client = getDataLakeContainerClient().getDataLakeClient(this.getPath());
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
		getContainerClient().deleteFile(this.getPath());
		return true;
	}

	@Override
	public boolean exists() throws ServiceException {
		DataLakeFileClient client = getContainerClient().getFileClient(this.getPath());
		return client.exists();
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

			DataLakeFileClient client = getContainerClient().getFileClient(this.getPath());
			// OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(1);
			Instant instant = expiration.toInstant();
			ZoneOffset current = ZoneOffset.systemDefault().getRules().getOffset(instant);
			// OffsetDateTime expiryTime =instant.atOffset(ZoneOffset.UTC);
			OffsetDateTime expiryTime = expiration.toInstant().atOffset(current);
			// OffsetDateTime expiryTime = ZoneOffset.systemDefault().getRules().getOffset(expiration.toInstant());
			FileSystemSasPermission permission = new FileSystemSasPermission().setReadPermission(readPermission).setDeletePermission(deletePermission)
					.setWritePermission(writePermission);

			// DataLakeServiceSasSignatureValues values = new DataLakeServiceSasSignatureValues(expiryTime,
			// permission).setStartTime(OffsetDateTime.now()).setProtocol(SasProtocol.HTTPS_ONLY)
			// .setContentDisposition(contentDisposition)
			// ;

			String urlstr = client.generateSas(new DataLakeServiceSasSignatureValues(expiryTime, permission));
			url = new URL(client.getFileUrl() + "?" + urlstr);
			// System.out.println("Pre-Signed URL: " + url.toString());
			// } catch (AmazonServiceException e) {
			// throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

		return url;
	}

	public static DataLakeFileItem setItemProperties(DataLakeFileItem item, PathItem datalakeItem) {
		String name = URLUtils.getLastNameFromPath(datalakeItem.getName());
		((ItemHiddenFunction) item).setName(name);

		OffsetDateTime time = datalakeItem.getLastModified();
		if (time != null) {
			item.setLastUpdateTime(time.toInstant().toEpochMilli());
		}

		item.setSize(datalakeItem.getContentLength());
		item.etag = datalakeItem.getETag();
		item.group = datalakeItem.getGroup();
		item.owner = datalakeItem.getOwner();
		item.permissions = datalakeItem.getPermissions();

		return item;
	}

	@Override
	public String getURL() {
		// https://song99.datalake.core.windows.net/quickstartdatalakes1/LGPL2.txt11
		// DataLakeFileSystemEntryConfig config = this.getItemspace().getFileSystemEntry().getEntryConfig();
		// return config.getProtocol().name().toLowerCase()
		// + "://"
		// + config.getAccesskey()
		// + "."
		// + config.getEndpoint()
		// + "/"
		// + this.getItemspace().getName()
		// + URLUtils.catPath("/", this.getPath(), '/');

		DataLakeFileClient client = this.getContainerClient().getFileClient(this.getPath());
		return client.getFileUrl();
	}

	@Override
	public void upateProperties() throws ServiceException {
		DataLakeFileClient client = this.getContainerClient().getFileClient(this.getPath());
		if (!client.exists()) {
			return;
		}
		PathProperties prop = client.getProperties();
		OffsetDateTime time = prop.getLastModified();
		if (time != null) {
			this.setLastUpdateTime(time.toInstant().toEpochMilli());
		}
		time = prop.getCreationTime();
		if (time != null) {
			this.setCreateTime(time.toInstant().toEpochMilli());
		}
		this.setSize(prop.getFileSize());
	}

	@Override
	public ItemProperties getProperties() throws ServiceException {
		ItemProperties p = super.getProperties();
		if (this.isFile()) {
			DataLakeFileClient client = this.getContainerClient().getFileClient(this.getPath());
			PathProperties properties = client.getProperties();
			p.add("extend:AccessTier", StringUtils.nullToString(properties.getAccessTier(), "-"));
			p.add("extend:AccessTierChangeTime", StringUtils.nullToString(properties.getAccessTierChangeTime(), "-"));
			p.add("extend:ArchiveStatus", StringUtils.nullToString(properties.getArchiveStatus(), "-"));
			p.add("extend:CacheControl", StringUtils.nullToString(properties.getCacheControl(), "-"));
			p.add("extend:ContentDisposition", StringUtils.nullToString(properties.getContentDisposition(), "-"));
			p.add("extend:ContentEncoding", StringUtils.nullToString(properties.getContentEncoding(), "-"));
			p.add("extend:ContentLanguage", StringUtils.nullToString(properties.getContentLanguage(), "-"));
			p.add("extend:ContentMd5", StringUtils.nullToString(properties.getContentMd5(), "-"));
			p.add("extend:ContentType", StringUtils.nullToString(properties.getContentType(), "-"));
			p.add("extend:CopyCompletionTime", StringUtils.nullToString(properties.getCopyCompletionTime(), "-"));
			p.add("extend:CopyId", StringUtils.nullToString(properties.getCopyId(), "-"));
			p.add("extend:CopyProgress", StringUtils.nullToString(properties.getCopyProgress(), "-"));
			p.add("extend:CopySource", StringUtils.nullToString(properties.getCopySource(), "-"));
			p.add("extend:CopyStatus", StringUtils.nullToString(properties.getCopyStatus(), "-"));
			p.add("extend:CopyStatusDescription", StringUtils.nullToString(properties.getCopyStatusDescription(), "-"));
			p.add("extend:CreationTime", StringUtils.nullToString(properties.getCreationTime(), "-"));
			p.add("extend:EncryptionKeySha256", StringUtils.nullToString(properties.getEncryptionKeySha256(), "-"));
			p.add("extend:ETag", StringUtils.nullToString(properties.getETag(), "-"));
			p.add("extend:FileSize", StringUtils.nullToString(properties.getFileSize(), "-"));
			p.add("extend:LastModified", StringUtils.nullToString(properties.getLastModified(), "-"));
			p.add("extend:LeaseDuration", StringUtils.nullToString(properties.getLeaseDuration(), "-"));
			p.add("extend:LeaseState", StringUtils.nullToString(properties.getLeaseState(), "-"));
			p.add("extend:LeaseStatus", StringUtils.nullToString(properties.getLeaseStatus(), "-"));
			// p.add("extend:Metadata", StringUtils.nullToString(properties.getMetadata(), "-"));
			p.add("extend:isDirectory", StringUtils.nullToString(properties.isDirectory(), "-"));
			p.add("extend:isIncrementalCopy", StringUtils.nullToString(properties.isIncrementalCopy(), "-"));
			p.add("extend:isServerEncrypted", StringUtils.nullToString(properties.isServerEncrypted(), "-"));
		}
		return p;
	}

}
