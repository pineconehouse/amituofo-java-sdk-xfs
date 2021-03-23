package com.amituofo.xfs.plugin.fs.objectstorage.hcp.item;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import com.amituofo.common.api.ObjectHandler;
import com.amituofo.common.ex.HSCException;
import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.DigestUtils;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.service.ContentHash;
import com.amituofo.xfs.service.ContentWriter;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemProperties;
import com.amituofo.xfs.service.ItemType;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.common.util.ValidUtils;
import com.hitachivantara.hcp.standard.api.ObjectEntryIterator;
import com.hitachivantara.hcp.standard.model.HCPObject;
import com.hitachivantara.hcp.standard.model.HCPObjectEntry;
import com.hitachivantara.hcp.standard.model.HCPObjectEntrys;
import com.hitachivantara.hcp.standard.model.HCPObjectSummary;
import com.hitachivantara.hcp.standard.model.PutObjectResult;
import com.hitachivantara.hcp.standard.model.Retention;
import com.hitachivantara.hcp.standard.model.metadata.AccessControlList;
import com.hitachivantara.hcp.standard.model.metadata.Annotation;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadataSummary;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadataSummarys;
import com.hitachivantara.hcp.standard.model.metadata.HCPSystemMetadata;
import com.hitachivantara.hcp.standard.model.metadata.PermissionGrant;
import com.hitachivantara.hcp.standard.model.metadata.S3CompatibleMetadata;
import com.hitachivantara.hcp.standard.model.request.impl.CopyObjectRequest;
import com.hitachivantara.hcp.standard.model.request.impl.DeleteObjectRequest;
import com.hitachivantara.hcp.standard.model.request.impl.GetACLRequest;
import com.hitachivantara.hcp.standard.model.request.impl.ListMetadataRequest;

public class HCPFileItem extends HCPItemBase implements OSDFileItem {
	protected HCPObjectSummary summary;

	public HCPFileItem(HCPBucketspace namespace, String key) {
		super(namespace, key);
		// this.summary = summary;
	}

	public HCPObjectSummary getSummary() {
		// if(summary==null) {
		//
		// }
		return summary;
	}

	@Override
	public void upateProperties() throws ServiceException {
		this.summary = null;
		updateSummary();
	}

	public HCPObjectSummary updateSummary() throws ServiceException {
		if (summary == null) {
			try {
				summary = getHcpClient().getObjectSummary(this.getPath());
			} catch (InvalidResponseException e) {
				throw new ServiceException(e.getMessage(), e);
			} catch (HSCException e) {
				throw new ServiceException("Path not exist or format incorrect!", e);
			}

			setHCPItemProperties(this, summary);
		}

		return summary;
	}

	public HCPObjectSummary updateSummary(PutObjectResult result) throws ServiceException {
		if (summary == null) {
			updateSummary();
		} else {
			summary.setETag(result.getETag());
			summary.setContentHash(result.getContentHash());
			summary.setContentLength(result.getContentLength());
			summary.setSize(result.getContentLength());
			summary.setIngestTime(result.getIngestTime());
			summary.setChangeTime(result.getIngestTime());
			summary.setVersionId(result.getVersionId());

			setHCPItemProperties(this, summary);
		}

		return summary;
	}

	public void upateProperties(PutObjectResult result) {
		if (summary != null) {
			summary.setSize(result.getContentLength());
			summary.setETag(result.getETag());
			summary.setContentHash(result.getContentHash());
			summary.setContentLength(result.getContentLength());
			summary.setIngestTime(result.getIngestTime());
			summary.setChangeTime(result.getIngestTime());
			summary.setVersionId(result.getVersionId());
		}

		this.setSize(result.getContentLength());
		this.setLastUpdateTime(result.getIngestTime());
		this.setCreateTime(result.getIngestTime());
	}

	public void setSummary(HCPObjectSummary summary) {
		this.summary = summary;
	}

	// @Override
	// public int getStatus() {
	// if (((HCPObjectEntry) getSummary()).getState() == ObjectState.deleted) {
	// return Item.ITEM_STATUS_DELETED;
	// }
	//
	// return 0;
	// }

	@Override
	public String getETag() {
		return getSummary().getETag();
	}
	
	@Override
	public ContentHash getContentHash() {
		ContentHash ch = super.getContentHash();
		if (ch == null) {
			ch = new ContentHash(getSummary().getHashAlgorithmName(), getSummary().getContentHash());
			super.setContentHash(ch);
		}
		return ch;
	}

	@Override
	public String getVersionId() {
		return getSummary().getVersionId();
	}

	// @Override
	// public String getVersionId() {
	// return summary.getVersionId();
	// }

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
		return getSummary().getOwner();
	}

	// @Override
	// public String getStorageClass() {
	// return "";
	// }

	@Override
	public InputStream getContent() throws ServiceException {
		HCPObject obj;
		try {
			// if (itemspace.isShowDeletedObjects()) {
			// if (((HCPObjectEntry) summary).getState() == ObjectState.deleted) {
			// HCPObjectEntrys versions = getHcpClient().listVersions(new ListVersionRequest(this.getPath()).withDeletedObject(true));
			// ObjectEntryIterator it = versions.iterator();
			//
			// String lastVersionId = null;
			// List<HCPObjectEntry> vs = it.next(10000);
			// for (HCPObjectEntry hcpObjectEntry : vs) {
			// if (hcpObjectEntry.getState() == ObjectState.created) {
			// lastVersionId = hcpObjectEntry.getVersionId();
			// summary.setSize(hcpObjectEntry.getSize());
			// summary.setContentLength(hcpObjectEntry.getSize());
			// summary.setIngestTime(hcpObjectEntry.getIngestTime());
			// super.setSize(hcpObjectEntry.getSize());
			// super.setLastUpdateTime(hcpObjectEntry.getIngestTime());
			// break;
			// }
			// }
			// it.abort();
			//
			// if (lastVersionId == null) {
			// throw new HSCException("No content, Available version not found!");
			// }
			//
			// obj = getHcpClient().getObject(new GetObjectRequest(this.getPath()).withDeletedObject(true).withVersionId(lastVersionId));
			// } else {
			// obj = getHcpClient().getObject(new
			// GetObjectRequest(this.getPath()).withDeletedObject(true).withVersionId(this.getSummary().getVersionId()));
			// }
			//
			// } else {
			obj = getHcpClient().getObject(this.getPath());
			// }
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}

		InputStream in = obj.getContent();

		return in;
	}

	@Override
	public ContentWriter getContentWriter() {
		return new HCPContentWriter(this);
	}

	@Override
	public Item clone() {
		// HCPFileItem clone = new HCPFileItem(preference, (HCPFileSystemEntry) fileSystemEntry, hcpNamespace, namespaceSetting);
		HCPFileItem clone = (HCPFileItem) super.clone();
		// clone.setParent(this.getParent());
		// clone.setCatelog(this.getCatelog());
		// clone.setName(this.getName());
		// clone.setKey(this.getKey());
		// clone.setData(this.getData());
		// clone.setType(this.getType());
		// clone.setSize(this.getSize());
		// clone.setCreateTime(this.getCreateTime());
		// clone.setLastUpdateTime(this.getLastUpdateTime());
		clone.setSummary(this.getSummary());
		return clone;
	}

	@Override
	public void listVersions(ObjectHandler<Integer, OSDVersionFileItem> event) {
		try {
			HCPObjectEntrys entrys = getHcpClient().listVersions(this.getKey());
			ObjectEntryIterator it = entrys.iterator();
			List<HCPObjectEntry> versions;
			while ((versions = it.next(20)) != null) {
				for (HCPObjectEntry version : versions) {
					HCPVersionFileItem versionFileItem = (HCPVersionFileItem) itemspace.newVersionFileItemInstance(version.getKey(), version.getVersionId());

					setHCPItemProperties(versionFileItem, version);

					event.handle(ItemEvent.ITEM_FOUND, versionFileItem);
				}
			}

			event.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			event.exceptionCaught(null, e);
		}
	}

	public void listACL(ObjectHandler<Integer, PermissionGrant> event) {
		try {
			// TODO
			AccessControlList acl = getHcpClient().getObjectACL(new GetACLRequest(this.getPath())); // .withDeletedObject(isShowDeletedObjects())
			Collection<PermissionGrant> allps = acl.getAllPermissions();
			for (PermissionGrant permissionGrant : allps) {
				event.handle(ItemEvent.ITEM_FOUND, permissionGrant);
			}
			event.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			event.exceptionCaught(null, e);
		}
	}

	public HCPMetadataItem createMetadata(String name) {
		HCPMetadataItem metaItem = itemspace.newMetadataFileItemInstance(this.getKey(), new HCPMetadataSummary(name));
		setHCPItemProperties(metaItem, this.getSummary());

		return metaItem;
	}

	public void setSystemMetadata(Boolean isHold, Boolean isIndex, Boolean isSherd, String owner, String retention) throws ServiceException {
		HCPSystemMetadata metadata = new HCPSystemMetadata();

		int uc = 0;
		if (isHold != null) {
			metadata.setHold(isHold);
			uc++;
		}
		if (isIndex != null) {
			metadata.setIndex(isIndex);
			uc++;
		}
		if (isSherd != null) {
			metadata.setShred(isSherd);
			uc++;
		}
		if (owner != null) {
			metadata.setOwner(owner);
			uc++;
		}
		if (retention != null) {
			metadata.setRetention(new Retention(retention));
			uc++;
		}
		if (uc != 0) {
			try {
				getHcpClient().setSystemMetadata(this.getKey(), metadata);
			} catch (InvalidResponseException e) {
				throw new ServiceException(e.getMessage(), e);
			} catch (HSCException e) {
				throw new ServiceException(e);
			}
		} else {
		}
	}

	public void putMetadata(S3CompatibleMetadata s3CompatibleMetadata) throws ServiceException {
		try {
			getHcpClient().putMetadata(getKey(), s3CompatibleMetadata);
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}
	}

	public HCPMetadataItem[] listMetadatas() throws ServiceException {
		try {

			HCPMetadataSummarys metas = getHcpClient().listMetadatas(new ListMetadataRequest(this.getKey()));
			Collection<HCPMetadataSummary> c = metas.getMetadatas();

			HCPMetadataItem[] metaItems = new HCPMetadataItem[c.size()];
			int i = 0;
			for (HCPMetadataSummary hcpMetadataSummary : c) {
				metaItems[i] = itemspace.newMetadataFileItemInstance(this.getKey(), hcpMetadataSummary);
				setHCPItemProperties(metaItems[i], this.getSummary());
				i++;
			}

			return metaItems;
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete() throws ServiceException {
		try {
			boolean deleted = getHcpClient().deleteObject(new DeleteObjectRequest(getKey()).withPurge(itemspace.isEnablePurgeDeletion()));

			return deleted;
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void copy(OSDFileItem source) throws ServiceException {
		String sourceNamespaceName = source.getItemspace().getName();
		String sourceKey = source.getKey();
		String targetNamespaceName = this.itemspace.getNamespace();
		String targetKey = URLUtils.catPath(this.getParent().getPath(), this.getName());
		CopyObjectRequest request = new CopyObjectRequest()
				//
				.withSourceKey(sourceKey)
				//
				.withSourceNamespace(sourceNamespaceName)
				//
				.withTargetKey(targetKey)
				//
				.withTargetNamespace(targetNamespaceName)
				//
				.withCopyingMetadata(true)
				//
				.withCopyingOldVersion(false);

		if (source instanceof OSDVersionFileItem) {
			request.withSourceVersion(source.getVersionId());
		}

		try {
			getHcpClient().copyObject(request);
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void rename(String newname) throws ServiceException {
		try {
			ValidUtils.invalidIfEmpty(newname, "A new name must be specified!");
			ValidUtils.invalidIfEqual(newname, this.getName(), "New name must be different with current name!");
		} catch (InvalidParameterException e) {
			throw new ServiceException(e);
		}

		HCPFileItem cloneItem = (HCPFileItem) this.clone();

		cloneItem.setName(newname);

		cloneItem.copy(this);

		if (cloneItem.exists()) {
			this.delete();
		}
	}

	@Override
	public boolean exists() throws ServiceException {
		try {
			return getHcpClient().doesObjectExist(this.getPath());
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}
	}

	public boolean purge() throws ServiceException {
		String key = (String) this.getPath();
		try {
			boolean deleted = getHcpClient().deleteObject(new DeleteObjectRequest(key).withPurge(true));

			return deleted;
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}
	}

	public boolean privilegedPurge(String reason) throws ServiceException {
		String key = (String) this.getPath();
		try {
			boolean deleted = getHcpClient().deleteObject(new DeleteObjectRequest(key).withPurge(true).withPrivileged(true, reason));

			return deleted;
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}
	}

	public void hold(boolean hold) throws ServiceException {
		String key = (String) this.getPath();
		try {
			HCPSystemMetadata metadata = new HCPSystemMetadata();
			metadata.setHold(hold);
			getHcpClient().setSystemMetadata(key, metadata);
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public String getURL() {
		OSDFileSystemEntryConfig config = (OSDFileSystemEntryConfig) this.getFileSystemEntry().getEntryConfig();
		String endpoint = config.getEndpoint().toLowerCase();
		String url = (config.isUseSSL() ? "https://" : "http://") + this.getItemspace().getName() + "." + endpoint + "/rest";
		url = URLUtils.catPath(url, this.getPath(), '/');

		HCPObjectSummary tmpsummary = ((HCPFileItem) this).getSummary();
		if (tmpsummary != null) {
			url += "?version=" + tmpsummary.getVersionId();
		}
		return url;
	}

	@Override
	public ItemProperties getProperties() throws ServiceException {
		ItemProperties p = super.getProperties();
		if (this.isFile()) {
			HCPObjectSummary summary = updateSummary();

			String customMetadatas = "";
			Annotation[] annotations = summary.getCustomMetadatas();
			if (annotations != null && annotations.length != 0) {
				// annotations.
				// for (Annotation annotation : annotations) {
				// customMetadatas+=annotation.getName();
				// }
				customMetadatas = String.valueOf(annotations.length);
			} else {
				customMetadatas = "-";
			}

			p.add("extend:ChangeTime", StringUtils.nullToString(summary.getChangeTime(), "-"));
			p.add("extend:ContentHash", StringUtils.nullToString(summary.getContentHash(), "-"));
			p.add("extend:CustomMetadatas", customMetadatas);
			p.add("extend:Domain", StringUtils.nullToString(summary.getDomain(), "-"));
			p.add("extend:Dpl", StringUtils.nullToString(summary.getDpl(), "-"));
			p.add("extend:ETag", StringUtils.nullToString(summary.getETag(), "-"));
			p.add("extend:HashAlgorithmName", StringUtils.nullToString(summary.getHashAlgorithmName(), "-"));
			p.add("extend:IngestProtocol", StringUtils.nullToString(summary.getIngestProtocol(), "-"));
			p.add("extend:IngestTime", StringUtils.nullToString(summary.getIngestTime(), "-"));
			p.add("extend:Key", StringUtils.nullToString(summary.getKey(), "-"));
			p.add("extend:Name", StringUtils.nullToString(summary.getName(), "-"));
			p.add("extend:Owner", StringUtils.nullToString(summary.getOwner(), "-"));
			p.add("extend:PosixGroupIdentifier", StringUtils.nullToString(summary.getPosixGroupIdentifier(), "-"));
			p.add("extend:PosixUserID", StringUtils.nullToString(summary.getPosixUserID(), "-"));
			p.add("extend:Retention", StringUtils.nullToString(summary.getRetention(), "-"));
			p.add("extend:RetentionClass", StringUtils.nullToString(summary.getRetentionClass(), "-"));
			p.add("extend:RetentionString", StringUtils.nullToString(summary.getRetentionString(), "-"));
			p.add("extend:Size", StringUtils.nullToString(summary.getSize(), "-"));
			p.add("extend:Type", StringUtils.nullToString(summary.getType(), "-"));
			p.add("extend:VersionId", StringUtils.nullToString(summary.getVersionId(), "-"));
			p.add("extend:HasAcl", StringUtils.nullToString(summary.hasAcl(), "-"));
			p.add("extend:HasMetadata", StringUtils.nullToString(summary.hasMetadata(), "-"));
			p.add("extend:IsAnnotation", StringUtils.nullToString(summary.isAnnotation(), "-"));
			p.add("extend:IsDirectory", StringUtils.nullToString(summary.isDirectory(), "-"));
			p.add("extend:IsHold", StringUtils.nullToString(summary.isHold(), "-"));
			p.add("extend:IsIndexed", StringUtils.nullToString(summary.isIndexed(), "-"));
			p.add("extend:IsObject", StringUtils.nullToString(summary.isObject(), "-"));
			p.add("extend:IsReplicated", StringUtils.nullToString(summary.isReplicated(), "-"));
			p.add("extend:IsReplicationCollision", StringUtils.nullToString(summary.isReplicationCollision(), "-"));
			p.add("extend:IsShred", StringUtils.nullToString(summary.isShred(), "-"));
			p.add("extend:IsSymlink", StringUtils.nullToString(summary.isSymlink(), "-"));
		}
		return p;
	}

	public static HCPFileItem setHCPItemProperties(HCPFileItem item, HCPObjectSummary hcpObjectEntry) {
		item.setName(hcpObjectEntry.getName());
		// item.setData(hcpObjectEntry.getKey());
		// item.setPath(hcpObjectEntry.getKey());
		// item.setParent(parent);
		// item.setType(itemType);
		// item.setCatelog(namespace);
		item.setSize(hcpObjectEntry.getSize());
		item.setLastUpdateTime(hcpObjectEntry.getIngestTime());
		item.setCreateTime(hcpObjectEntry.getIngestTime());

		item.setSummary(hcpObjectEntry);
		return item;
	}

	public static HCPFileItem setHCPItemProperties(HCPFileItem item, HCPObjectEntry hcpObjectEntry) {
		item.setName(hcpObjectEntry.getName());
		// item.setData(hcpObjectEntry.getKey());
		// item.setPath(hcpObjectEntry.getKey());
		// item.setParent(parent);
		// item.setType(itemType);
		// item.setCatelog(namespace);
		item.setSize(hcpObjectEntry.getSize());
		item.setLastUpdateTime(hcpObjectEntry.getIngestTime());
		item.setCreateTime(hcpObjectEntry.getIngestTime());

		item.setSummary(hcpObjectEntry);

		item.state = hcpObjectEntry.getState();

		return item;
	}

}
