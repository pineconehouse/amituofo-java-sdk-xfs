package com.amituofo.xfs.plugin.fs.objectstorage.azure.datalake.item;

import java.time.OffsetDateTime;
import java.util.Iterator;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;
import com.amituofo.xfs.service.ItemType;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.file.datalake.models.ListPathsOptions;
import com.azure.storage.file.datalake.models.PathItem;

public class DataLakeFolderItem extends DataLakeItemBase implements FolderItem {

	public DataLakeFolderItem(DataLakeContainerspace namespace, String key) {
		super(namespace, key);
	}

	@Override
	public void list(ItemFilter filter, ItemHandler handler) {
		try {
			PagedIterable<PathItem> result = null;
			result = getContainerClient().listPaths(new ListPathsOptions().setRecursive(false).setPath(this.getPath()), null);
			for (Iterator<PathItem> it = result.iterator(); it.hasNext();) {
				PathItem datalakeItem = (PathItem) it.next();

				Item item;
				String fullpath = datalakeItem.getName();
				if (datalakeItem.isDirectory()) {
					item = ((ItemInstanceCreator) itemspace).newFolderItemInstance(fullpath);
					DataLakeFolderItem.setItemProperties((DataLakeFolderItem) item, datalakeItem);
				} else {
					item = ((ItemInstanceCreator) itemspace).newFileItemInstance(fullpath);
					DataLakeFileItem.setItemProperties((DataLakeFileItem) item, datalakeItem);
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
//		getContainerClient().deleteDirectory(this.getPath());
//		return true;
//	}

	// @Override
	// public void copy(OSDFileItem source) throws ServiceException {
	// }

	@Override
	public boolean delete() {
		return delete(null);
	}

	@Override
	public boolean delete(final ItemHandler handler) {
		getContainerClient().deleteDirectory(this.getPath());
		return true;
	}

	@Override
	public boolean createDirectory() throws ServiceException {
		getContainerClient().createDirectory(this.getPath());
		return true;
	}

	@Override
	public void rename(String newname) throws ServiceException {
		String key = getPath();
		if (key.length() == 0 || "/".equals(key)) {
			// Root path
			return;
		}

		super.rename(newname);
		// throw new ServiceException("Unsupport operation!");
	}

	@Override
	public boolean exists() throws ServiceException {
		String key = getPath();
		if (key.length() == 0 || "/".equals(key)) {
			// Root path
			return true;
		}

		// Always exist
		return getContainerClient().getFileClient(this.getPath()).exists();
		// return true;
	}
	
	public static DataLakeFolderItem setItemProperties(DataLakeFolderItem item, PathItem datalakeItem) {
		String name = URLUtils.getLastNameFromPath(datalakeItem.getName());
		((ItemHiddenFunction) item).setName(name);

		OffsetDateTime time = datalakeItem.getLastModified();
		if (time != null) {
			item.setLastUpdateTime(time.toInstant().toEpochMilli());
		}

//		item.setSize(datalakeItem.getContentLength());
		item.etag=datalakeItem.getETag();
		item.group=datalakeItem.getGroup();
		item.owner=datalakeItem.getOwner();
		item.permissions=datalakeItem.getPermissions();
		
		return item;
	}
}
