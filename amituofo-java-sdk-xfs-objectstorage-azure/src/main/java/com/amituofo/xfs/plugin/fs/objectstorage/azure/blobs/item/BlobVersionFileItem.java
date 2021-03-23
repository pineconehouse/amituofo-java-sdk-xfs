package com.amituofo.xfs.plugin.fs.objectstorage.azure.blobs.item;

import java.io.InputStream;
import java.util.Map;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.azure.storage.blob.BlobClient;

public class BlobVersionFileItem extends BlobFileItem implements OSDVersionFileItem {

	public BlobVersionFileItem(BlobContainerspace namespace, String key, String versionId) {
		super(namespace, key);
		this.versionId = versionId;
	}

	@Override
	public InputStream getContent() throws ServiceException {
		BlobClient client = this.getContainerClient().getBlobVersionClient(this.getPath(), this.getVersionId());
		InputStream in = client.openInputStream();
		return in;
	}

	@Override
	public boolean delete() throws ServiceException {
		BlobClient client = this.getContainerClient().getBlobVersionClient(this.getPath(), this.getVersionId());
		client.delete();
		return true;
	}

	@Override
	public Map<String, String> getTags() {
		BlobClient client = this.getContainerClient().getBlobVersionClient(this.getPath(), this.getVersionId());
		Map<String, String> tags = client.getTags();
		return tags;
	}

	@Override
	public void setTags(Map<String, String> tags) {
		BlobClient client = this.getContainerClient().getBlobVersionClient(this.getPath(), this.getVersionId());
		client.setTags(tags);
	}

	@Override
	public Map<String, String> getMetadata() {
		BlobClient client = this.getContainerClient().getBlobVersionClient(this.getPath(), this.getVersionId());
		return client.getProperties().getMetadata();
	}

	@Override
	public void setMetadata(Map<String, String> metadata) {
		BlobClient client = this.getContainerClient().getBlobVersionClient(this.getPath(), this.getVersionId());
		client.setMetadata(metadata);
	}

	@Override
	public void upateProperties() throws ServiceException {
		BlobClient client = this.getContainerClient().getBlobVersionClient(this.getPath(), this.getVersionId());
		upateProperties(client);
	}
	// @Override
	// public void listACL(ObjectHandler<Integer, PermissionGrant> event) {
	// try {
	// AccessControlList acl = getHcpClient().getObjectACL(new GetACLRequest(getKey()).withVersionId(versionId));
	// Collection<PermissionGrant> allps = acl.getAllPermissions();
	// for (PermissionGrant permissionGrant : allps) {
	// event.handle(ItemEvent.ITEM_FOUND, permissionGrant);
	// }
	// event.handle(ItemEvent.EXEC_END, null);
	// } catch (Exception e) {
	// event.exceptionCaught(null, e);
	// }
	// }

}
