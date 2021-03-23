package com.amituofo.xfs.plugin.fs.objectstorage.azure.blobs.item;

import java.util.Iterator;
import java.util.Locale;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDBucketspace;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDItemInstanceCreator;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.blobs.BlobFileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.blobs.BlobFileSystemPreference;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.common.AzureStorageContainerspace;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ListOption;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobItemProperties;
import com.azure.storage.blob.models.ListBlobsOptions;

public class BlobContainerspace extends AzureStorageContainerspace<BlobContainerClient, BlobFileSystemEntry, BlobFileSystemPreference> implements OSDBucketspace, OSDItemInstanceCreator {

	public BlobContainerspace(BlobFileSystemEntry entry, BlobContainerClient containerClient) {
		super(entry, containerClient);
	}

	@Override
	public String getName() {
		return getContainerClient().getBlobContainerName();
	}

	@Override
	protected FolderItem createRootFolder() {
		FolderItem root = newFolderItemInstance("");
		((ItemHiddenFunction) root).setName(getName());
		return root;
	}

	@Override
	public FolderItem newFolderItemInstance(String key) {
		BlobFolderItem folder = new BlobFolderItem(this, key);
		return folder;
	}

	@Override
	public FileItem newFileItemInstance(String key) {
		BlobFileItem folder = new BlobFileItem(this, key);
		return folder;
	}

	@Override
	public OSDVersionFileItem newVersionFileItemInstance(String key, String versionId) {
		return new BlobVersionFileItem(this, key, versionId);
	}

	// public HCPMetadataItem newMetadataFileItemInstance(String key, HCPMetadataSummary hcpMetadataSummary) {
	// return new HCPMetadataItem(this, key, hcpMetadataSummary);
	// }
	//
	// public HCPMetadataItem newVersionMetadataFileItemInstance(String key, String versionId, HCPMetadataSummary hcpMetadataSummary) {
	// return new HCPVersionMetadataItem(this, key, versionId, hcpMetadataSummary);
	// }

	@Override
	public String getEndpoint() {
		return String.format(Locale.ROOT, "https://%s.blob.core.windows.net", this.getName());
	}

	// protected boolean isShowDeletedObjects() {
	// return preference.isShowDeletedObjects() && namespaceSetting.isVersioningEnabled();
	// }
	//
	// protected boolean isEnablePurgeDeletion() {
	// return preference.isEnablePurgeDeletion() && namespaceSetting.isVersioningEnabled();
	// }
	@Override
	public void list(final ListOption listOption, final ItemHandler handler) throws ServiceException {
		try {
			ItemFilter filter = listOption.getFilter();
			PagedIterable<BlobItem> resultlisting;

			if (listOption.isWithSubDirectory()) {
				if (StringUtils.isNotEmpty(listOption.getPrefix())) {
					resultlisting = getContainerClient().listBlobs(new ListBlobsOptions().setPrefix(listOption.getPrefix()), null);
				} else {
					resultlisting = getContainerClient().listBlobs();
				}
			} else {
				if (StringUtils.isNotEmpty(listOption.getPrefix())) {
					resultlisting = getContainerClient().listBlobsByHierarchy(listOption.getPrefix());
				} else {
					resultlisting = getContainerClient().listBlobsByHierarchy("/", null, null);
				}
			}

			for (Iterator<BlobItem> it = resultlisting.iterator(); it.hasNext();) {
				BlobItem blobItem = (BlobItem) it.next();
				// System.out.println("\t" + blobItem.getName() + "\t" + blobItem.getVersionId() + "\t"+blobItem.getProperties().getContentLength());
				Item item;
				BlobItemProperties prop = blobItem.getProperties();
				String key = blobItem.getName();
				if (prop != null) {
					item = this.newFileItemInstance(key);
					BlobFileItem.setItemProperties((BlobFileItem) item, blobItem, prop);
				} else {
					item = this.newFolderItemInstance(key);
					String name = URLUtils.getLastNameFromPath(key);
					((ItemHiddenFunction) item).setName(name);
				}

				if (filter != null && !filter.accept(item)) {
					continue;
				}

				HandleFeedback result = handler.handle(ItemEvent.ITEM_FOUND, item);
				if (result == HandleFeedback.interrupted) {
					return;
				}
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			handler.handle(ItemEvent.EXEC_END, null);
		}
	}

}
