package com.amituofo.xfs.plugin.fs.objectstorage.azure.blobs.item;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;
import com.amituofo.xfs.service.ItemType;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobItemProperties;

public class BlobFolderItem extends BlobItemBase implements OSDFolderItem {

	public BlobFolderItem(BlobContainerspace namespace, String key) {
		super(namespace, key);
	}

	@Override
	public void list(ItemFilter filter, ItemHandler handler) {
		try {
			PagedIterable<BlobItem> result = null;
			result = getContainerClient().listBlobsByHierarchy(this.getPath());

			for (Iterator<BlobItem> it = result.iterator(); it.hasNext();) {
				BlobItem blobItem = (BlobItem) it.next();

				Item item;
				BlobItemProperties prop = blobItem.getProperties();
				String key = blobItem.getName();
				if (prop != null) {
//					String versionId = blobItem.getVersionId();
//					if(StringUtils.isNotEmpty(versionId)) {
//						item = ((BlobContainerspace) itemspace).newVersionFileItemInstance(key, versionId);
//					} else {
						item = ((ItemInstanceCreator) itemspace).newFileItemInstance(key);
//					}
					BlobFileItem.setItemProperties((BlobFileItem) item, blobItem, prop);
				} else {
					item = ((ItemInstanceCreator) itemspace).newFolderItemInstance(key);
					String name = URLUtils.getLastNameFromPath(key);
					((ItemHiddenFunction) item).setName(name);
				}

				if (filter != null && !filter.accept(item)) {
					continue;
				}

				HandleFeedback ef = handler.handle(ItemEvent.ITEM_FOUND, item);
				if (ef == HandleFeedback.interrupted) {
					handler.handle(ItemEvent.EXEC_END, null);
					return;
				}
			}

			handler.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			handler.exceptionCaught(this, e);
		}
	}

//	@Override
//	public boolean deleteEmptyFolder() throws ServiceException {
//		BlobClient client = getContainerClient().getBlobClient(this.getPath());
//		client.delete();
//		return true;
//	}

	// public boolean deleteVersion() throws ServiceException {
	// String key = (String) this.getActualPath();
	// try {
	// return getHcpClient().deleteObject(new DeleteObjectRequest(key).withVersionId(this.getSummary().getVersionId()));
	// } catch (InvalidResponseException e) {
	// throw new ServiceException(e.getMessage(), e);
	// } catch (HSCException e) {
	// throw new ServiceException(e);
	// }
	// }

	@Override
	public void copy(OSDFileItem source) throws ServiceException {
		// String sourceNamespaceName = ((BlobFileItem) source).getHcpClient().getNamespace();
		// String sourceKey = source.getPath();
		// String targetNamespaceName = this.getHcpClient().getNamespace();
		// String targetKey = URLUtils.catPath(this.getPath(), source.getName());
		// CopyObjectRequest request = new CopyObjectRequest()
		// //
		// .withSourceKey(sourceKey)
		// //
		// .withSourceNamespace(sourceNamespaceName)
		// //
		// .withTargetKey(targetKey)
		// //
		// .withTargetNamespace(targetNamespaceName)
		// //
		// .withCopyingMetadata(true)
		// //
		// .withCopyingOldVersion(false);
		//
		// if (source instanceof OSDVersionFileItem) {
		// request.withSourceVersion(source.getVersionId());
		// }
		//
		// try {
		// getHcpClient().copyObject(request);
		// } catch (InvalidResponseException e) {
		// throw new ServiceException(e.getMessage(), e);
		// } catch (HSCException e) {
		// throw new ServiceException(e);
		// }
	}

	@Override
	public boolean delete() {
		return delete(null);
	}

	@Override
	public boolean delete(final ItemHandler handler) {
		BlobClient client = getContainerClient().getBlobClient(this.getPath());
//		Response<Void> response = client.deleteWithResponse(null, null, null, null);
		client.delete();
		return true;
	}

//	@Override
//	public boolean createDirectory(String name) throws ServiceException {
//		String dirKey = URLUtils.catPath(this.getPath(), name);
//		BlobClient client = getContainerClient().getBlobClient(URLUtils.catPath(dirKey, "/delete-me.tmp"));
//		client.upload(new ByteArrayInputStream(new byte[0]), 0);
////		client.delete();
//		return true;
//	}

	@Override
	public boolean createDirectory() throws ServiceException {
		BlobClient client = getContainerClient().getBlobClient(URLUtils.catPath(getPath(), "/delete-me.tmp"));
		client.upload(new ByteArrayInputStream(new byte[0]), 0);
		return true;	}

	@Override
	public void rename(String newname) throws ServiceException {
		throw new ServiceException("Unsupport operation!");
	}

	@Override
	public boolean exists() throws ServiceException {
//		String key = getPath();
//		if (key.length() == 0 || "/".equals(key)) {
//			// Root path
//			return true;
//		}
//
//		return getBlobContainerClient().getBlobClient(this.getPath()).exists();
		
		// Always exist
		return true;
	}
}
