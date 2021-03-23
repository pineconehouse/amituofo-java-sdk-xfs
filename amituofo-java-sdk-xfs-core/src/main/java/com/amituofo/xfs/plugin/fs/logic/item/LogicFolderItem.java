package com.amituofo.xfs.plugin.fs.logic.item;

import java.util.ArrayList;
import java.util.List;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.logic.ItemLister;
import com.amituofo.xfs.plugin.fs.logic.LogicFileSystemEntryConfig;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FileSystemConfig;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemBase;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemType;

public class LogicFolderItem extends ItemBase<LogicItemspace> implements FolderItem {
	protected final ItemLister lister;
	protected final List<LogicFileItem> memoryList = new ArrayList<LogicFileItem>();

	public LogicFolderItem(LogicItemspace itemspace, ItemLister lister) {
		super(itemspace);
		this.lister = lister;
		super.setCreateTime(System.currentTimeMillis());
		super.setLastUpdateTime(System.currentTimeMillis());
		super.setName(itemspace.getName());
		super.setPath(itemspace.getFileSystemEntry().getRootPath());
	}

//	public void listFolders(final ItemFilter filter, ItemHandler handler) {
//	}

	public synchronized void list(ItemFilter filter, final ItemHandler handler) {
		if (!itemspace.getFileSystemPreference().isEnableCache() || memoryList.size() == 0) {
			lister.list(filter, new ItemHandler() {

				@Override
				public void exceptionCaught(Item data, Throwable e) {
					handler.exceptionCaught(data, e);
				}

				@Override
				public HandleFeedback handle(Integer meta, Item obj) {
					if (obj != null) {
						if (obj.isFile()) {
							LogicFileItem logicFileItem = new LogicFileItem((FileItem) obj);
							memoryList.add(logicFileItem);
							return handler.handle(meta, logicFileItem);
						}
					} else {
						return handler.handle(meta, null);
					}
					
					return null;
				}
			});
		} else {
			for (LogicFileItem item : memoryList) {
				HandleFeedback result = handler.handle(ItemEvent.ITEM_FOUND, item);
				if (result == HandleFeedback.interrupted) {
					return;
				}
			}

			handler.handle(ItemEvent.EXEC_END, null);
		}
	}

	public boolean exists() throws ServiceException {
		return true;
	}

	public boolean delete(ItemHandler handler) {
		return false;
	}

	public boolean delete() throws ServiceException {
		return false;
	}

	public void rename(String newname) throws ServiceException {
	}

	public Item clone() {
		return new LogicFolderItem(this.getItemspace(), lister);
	}

	public FolderItem getParent() {
		return null;
	}

	public boolean deleteEmptyFolder() throws ServiceException {
		return false;
	}

	public FolderItem getRoot() {
		return this;
	}

	public boolean equals(Object obj) {
		return false;
	}

	public boolean createDirectory(String name) throws ServiceException {
		return false;
	}

	public boolean createDirectory() throws ServiceException {
		return false;
	}

	@Override
	public char getPathSeparator() {
		return FileSystemConfig.URL_PS;
	}

	@Override
	public boolean isSame(Item item) {
		return false;
	}

	@Override
	public boolean isFromSameSystem(Item item) {
		return false;
	}

	@Override
	public String getSystemName() {
		return LogicFileSystemEntryConfig.SYSTEM_NAME;
	}

//	@Override
//	public String[] getSupportVersion() {
//		return null;
//	}

}
