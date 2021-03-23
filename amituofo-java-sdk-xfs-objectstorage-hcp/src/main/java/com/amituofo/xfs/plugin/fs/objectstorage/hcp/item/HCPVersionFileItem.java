package com.amituofo.xfs.plugin.fs.objectstorage.hcp.item;

import java.io.InputStream;
import java.util.Collection;

import com.amituofo.common.api.ObjectHandler;
import com.amituofo.common.ex.HSCException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.service.ItemEvent;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.model.HCPObject;
import com.hitachivantara.hcp.standard.model.metadata.AccessControlList;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadataSummary;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadataSummarys;
import com.hitachivantara.hcp.standard.model.metadata.PermissionGrant;
import com.hitachivantara.hcp.standard.model.request.impl.DeleteObjectRequest;
import com.hitachivantara.hcp.standard.model.request.impl.GetACLRequest;
import com.hitachivantara.hcp.standard.model.request.impl.GetObjectRequest;
import com.hitachivantara.hcp.standard.model.request.impl.ListMetadataRequest;

public class HCPVersionFileItem extends HCPFileItem implements OSDVersionFileItem {
	protected String versionId;

	public HCPVersionFileItem(HCPBucketspace namespace, String key, String versionId) {
		super(namespace, key);
		this.versionId = versionId;
	}

	public String getVersionId() {
		return versionId;
	}

	@Override
	public InputStream getContent() throws ServiceException {
		HCPObject obj;
		try {
			obj = getHcpClient().getObject(new GetObjectRequest(getKey()).withVersionId(getVersionId()).withDeletedObject(itemspace.isShowDeletedObjects()));
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
		try {
			boolean deleted = getHcpClient().deleteObject(new DeleteObjectRequest(getKey()).withVersionId(getVersionId()));

			return deleted;
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public HCPMetadataItem[] listMetadatas() throws ServiceException {
		try {
			HCPMetadataSummarys metas = getHcpClient().listMetadatas(new ListMetadataRequest(getKey(), getVersionId()));
			Collection<HCPMetadataSummary> c = metas.getMetadatas();

			HCPMetadataItem[] metaItems = new HCPMetadataItem[c.size()];
			int i = 0;
			for (HCPMetadataSummary hcpMetadataSummary : c) {
				metaItems[i] = itemspace.newVersionMetadataFileItemInstance(this.getKey(), getVersionId(), hcpMetadataSummary);
				setHCPItemProperties(metaItems[i], this.getSummary());

//				metaItems[i] = (HCPMetadataItem) setHCPItemProperties(
//						new HCPVersionMetadataItem(
//								(HCPItemNamespace) itemspace,
//								(HCPFileSystemEntry) fileSystemEntry,
//								preference,
//								getHcpClient(),
//								namespaceSetting,
//								hcpMetadataSummary),
//						this.getSummary());
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
	public void rename(String newname) throws ServiceException {
		// unsupport
	}

	@Override
	public void listACL(ObjectHandler<Integer, PermissionGrant> event) {
		try {
			AccessControlList acl = getHcpClient().getObjectACL(new GetACLRequest(getKey()).withVersionId(getVersionId()));
			Collection<PermissionGrant> allps = acl.getAllPermissions();
			for (PermissionGrant permissionGrant : allps) {
				event.handle(ItemEvent.ITEM_FOUND, permissionGrant);
			}
			event.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			event.exceptionCaught(null, e);
		}
	}

}
