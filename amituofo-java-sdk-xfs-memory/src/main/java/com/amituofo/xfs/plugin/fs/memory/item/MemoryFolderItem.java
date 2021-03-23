package com.amituofo.xfs.plugin.fs.memory.item;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.memory.filesystem.MemoryFileSystem;
import com.amituofo.xfs.plugin.fs.memory.filesystem.MemoryFileSystemException;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemType;

public class MemoryFolderItem extends MemoryItemBase implements FolderItem {
	public MemoryFolderItem(MemoryFileSystem memfs, MemoryItemspace itemspace, String filepath) {
		super(itemspace, memfs);
		this.setPath(filepath);
	}

	@Override
	public void list(ItemFilter filter, ItemHandler handler) {
		try {
			memfs.list(this.getPath(), filter, handler);
		} catch (MemoryFileSystemException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean delete() throws ServiceException {
		return delete(null);
	}

	@Override
	public boolean delete(ItemHandler handler) {
		boolean deleted = false;
		try {
			deleted = memfs.delete(this.getPath());
			if (handler != null) {
				handler.handle(deleted ? ItemEvent.ITEM_DELETED : ItemEvent.ITEM_DELETE_FAILED, this);
			}
		} catch (MemoryFileSystemException e) {
			// e.printStackTrace();
			if (handler != null) {
				handler.exceptionCaught(this, e);
			}
		}
		return deleted;
	}

	// @Override
	// public boolean deleteEmptyFolder() throws ServiceException {
	// return delete(null);
	// }

	// @Override
	// public boolean createDirectory(String name) throws ServiceException {
	// try {
	// String folderPath = URLUtils.catPath(this.getPath(), name, itemspace.getFileSystemEntry().getSeparatorChar());
	// memfs.newDirectorys(folderPath);
	// return true;
	// } catch (MemoryFileSystemException e) {
	// e.printStackTrace();
	// return false;
	// }
	// }

	@Override
	public boolean createDirectory() throws ServiceException {
		try {
			memfs.newDirectorys(this.getPath());
			return true;
		} catch (MemoryFileSystemException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean exists() throws ServiceException {
		try {
			return memfs.exist(this.getPath());
		} catch (MemoryFileSystemException e) {
			e.printStackTrace();
			return false;
		}
	}

}
