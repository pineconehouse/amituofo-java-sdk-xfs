package com.amituofo.xfs.plugin.fs.objectstorage.hcp.item;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import com.amituofo.common.api.ObjectHandler;
import com.amituofo.common.ex.HSCException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.service.ItemEvent;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.ObjectEntryIterator;
import com.hitachivantara.hcp.standard.define.ObjectState;
import com.hitachivantara.hcp.standard.model.HCPObject;
import com.hitachivantara.hcp.standard.model.HCPObjectEntry;
import com.hitachivantara.hcp.standard.model.HCPObjectEntrys;
import com.hitachivantara.hcp.standard.model.HCPObjectSummary;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadataSummary;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadataSummarys;
import com.hitachivantara.hcp.standard.model.request.impl.CheckObjectRequest;
import com.hitachivantara.hcp.standard.model.request.impl.GetObjectRequest;
import com.hitachivantara.hcp.standard.model.request.impl.ListMetadataRequest;
import com.hitachivantara.hcp.standard.model.request.impl.ListVersionRequest;

public class HCPDeletedFileItem extends HCPVersionFileItem {

	// public HCPDeletedFileItem(HCPBucketspace namespace, String key) {
	// this(namespace, key, null);
	// }

	public HCPDeletedFileItem(HCPBucketspace namespace, String key, String versionId) {
		super(namespace, key, versionId);
	}

	public String getVersionId() {
		if (versionId == null) {
			// pick a version id if did not assigned
			HCPObjectEntrys versions;
			try {
				versions = getHcpClient().listVersions(new ListVersionRequest(this.getPath()).withDeletedObject(true));

				ObjectEntryIterator it = versions.iterator();

				List<HCPObjectEntry> vs = it.next(10000);
				for (HCPObjectEntry hcpObjectEntry : vs) {
					if (hcpObjectEntry.getState() == ObjectState.created) {
						versionId = hcpObjectEntry.getVersionId();
						// Update the size to current version
						summary = hcpObjectEntry;
//						summary.setSize(hcpObjectEntry.getSize());
//						summary.setContentLength(hcpObjectEntry.getSize());
//						summary.setIngestTime(hcpObjectEntry.getIngestTime());
//						summary.setChangeTime(hcpObjectEntry.getChangeTime());
//						summary.setContentHash(hcpObjectEntry.getContentHash());
//						summary.setETag(hcpObjectEntry.getETag());
//						summary.setCustomMetadatas(hcpObjectEntry.getCustomMetadatas());
//						summary.setHasMetadata(hcpObjectEntry.hasMetadata());
						super.setSize(hcpObjectEntry.getSize());
						super.setLastUpdateTime(hcpObjectEntry.getIngestTime());
						break;
					}
				}
				it.abort();
			} catch (InvalidResponseException e) {
				e.printStackTrace();
			} catch (HSCException e) {
				e.printStackTrace();
			}
		}

		return versionId;
	}

	public HCPObjectSummary updateSummary() throws ServiceException {
		if (summary == null) {
			try {
				summary = getHcpClient().getObjectSummary(new CheckObjectRequest(this.getKey(), getVersionId()));
			} catch (InvalidResponseException e) {
				throw new ServiceException(e.getMessage(), e);
			} catch (HSCException e) {
				throw new ServiceException("Path not exist or format incorrect!", e);
			}

			setHCPItemProperties(this, summary);
		}

		return summary;
	}

	@Override
	public boolean exists() throws ServiceException {
		try {
			return getHcpClient().doesObjectExist(new CheckObjectRequest(this.getPath()).withVersionId(getVersionId()).withDeletedObject(true));
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void listVersions(ObjectHandler<Integer, OSDVersionFileItem> event) {
		try {
			HCPObjectEntrys entrys = getHcpClient().listVersions(new ListVersionRequest(this.getKey()).withDeletedObject(true));
			ObjectEntryIterator it = entrys.iterator();
			List<HCPObjectEntry> versions;
			while ((versions = it.next(20)) != null) {
				for (HCPObjectEntry version : versions) {
					if (version.getState() == ObjectState.created) {
						HCPVersionFileItem versionFileItem = (HCPVersionFileItem) itemspace.newDeletedFileItemInstance(version.getKey(), version.getVersionId());

						setHCPItemProperties(versionFileItem, version);

						event.handle(ItemEvent.ITEM_FOUND, versionFileItem);
					}
				}
			}

			event.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			event.exceptionCaught(null, e);
		}
	}

	@Override
	public InputStream getContent() throws ServiceException {
		HCPObject obj;
		try {

			// if (defaultVersionId == null) {
			// throw new HSCException("No content, Available version not found!");
			// }

			obj = getHcpClient().getObject(new GetObjectRequest(this.getPath()).withDeletedObject(true).withVersionId(getVersionId()));
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}

		InputStream in = obj.getContent();

		return in;
	}

	@Override
	public boolean delete() throws ServiceException {
		return super.purge();
	}

	@Override
	public HCPMetadataItem[] listMetadatas() throws ServiceException {
		try {
			HCPMetadataSummarys metas = getHcpClient().listMetadatas(new ListMetadataRequest(getKey(), getVersionId()).withDeletedObject(true));
			Collection<HCPMetadataSummary> c = metas.getMetadatas();

			HCPMetadataItem[] metaItems = new HCPMetadataItem[c.size()];
			int i = 0;
			for (HCPMetadataSummary hcpMetadataSummary : c) {
				metaItems[i] = itemspace.newVersionMetadataFileItemInstance(this.getKey(), getVersionId(), hcpMetadataSummary);
				setHCPItemProperties(metaItems[i], this.getSummary());

				// metaItems[i] = (HCPMetadataItem) setHCPItemProperties(
				// new HCPVersionMetadataItem(
				// (HCPItemNamespace) itemspace,
				// (HCPFileSystemEntry) fileSystemEntry,
				// preference,
				// getHcpClient(),
				// namespaceSetting,
				// hcpMetadataSummary),
				// this.getSummary());
				i++;
			}

			return metaItems;
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}
	}

}
