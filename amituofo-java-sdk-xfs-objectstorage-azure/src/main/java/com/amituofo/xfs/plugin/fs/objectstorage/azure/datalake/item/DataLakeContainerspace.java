package com.amituofo.xfs.plugin.fs.objectstorage.azure.datalake.item;

import java.util.Iterator;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDBucketspace;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDItemInstanceCreator;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.common.AzureStorageContainerspace;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.datalake.DataLakeFileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.datalake.DataLakeFileSystemPreference;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ListOption;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.file.datalake.DataLakeFileSystemClient;
import com.azure.storage.file.datalake.models.ListPathsOptions;
import com.azure.storage.file.datalake.models.PathItem;

public class DataLakeContainerspace extends AzureStorageContainerspace<DataLakeFileSystemClient, DataLakeFileSystemEntry, DataLakeFileSystemPreference>
		implements OSDBucketspace, OSDItemInstanceCreator {

	public DataLakeContainerspace(DataLakeFileSystemEntry entry, DataLakeFileSystemClient containerClient) {
		super(entry,containerClient);
	}

	@Override
	public String getName() {
		return getContainerClient().getFileSystemName();
	}

	@Override
	protected FolderItem createRootFolder() {
		FolderItem root = newFolderItemInstance("");
		((ItemHiddenFunction) root).setName(getName());
		return root;
	}

	@Override
	public FolderItem newFolderItemInstance(String key) {
		DataLakeFolderItem folder = new DataLakeFolderItem(this, key);
		return folder;
	}

	@Override
	public FileItem newFileItemInstance(String key) {
		DataLakeFileItem folder = new DataLakeFileItem(this, key);
		return folder;
	}

	@Override
	public OSDVersionFileItem newVersionFileItemInstance(String key, String versionId) {
		return null;
	}

	@Override
	public void list(final ListOption listOption, final ItemHandler handler) throws ServiceException {
		try {
			ItemFilter filter = listOption.getFilter();
			PagedIterable<PathItem> resultlisting;

			// if (listOption.isWithSubDirectory()) {
			if (StringUtils.isNotEmpty(listOption.getPrefix())) {
				resultlisting = getContainerClient().listPaths(new ListPathsOptions().setRecursive(true).setPath(listOption.getPrefix()), null);
			} else {
				resultlisting = getContainerClient().listPaths();
			}
			// } else {
			// if (StringUtils.isNotEmpty(listOption.getPrefix())) {
			// resultlisting = containerClient.l.listDataLakesByHierarchy(listOption.getPrefix());
			// } else {
			// resultlisting = containerClient.listDataLakesByHierarchy("/", null, null);
			// }
			// }

			for (Iterator<PathItem> it = resultlisting.iterator(); it.hasNext();) {
				PathItem datalakeItem = (PathItem) it.next();
				// System.out.println("\t" + blobItem.getName() + "\t" + blobItem.getVersionId() + "\t"+blobItem.getProperties().getContentLength());
				Item item;

				String fullpath = datalakeItem.getName();
				if (datalakeItem.isDirectory()) {
					item = this.newFolderItemInstance(fullpath);
					DataLakeFolderItem.setItemProperties((DataLakeFolderItem) item, datalakeItem);
				} else {
					item = this.newFileItemInstance(fullpath);
					DataLakeFileItem.setItemProperties((DataLakeFileItem) item, datalakeItem);
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
