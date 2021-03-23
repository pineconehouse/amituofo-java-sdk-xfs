package com.amituofo.xfs.service;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.util.ItemUtils;

public abstract class ItemspaceBase<ENTRY extends FileSystemEntry, PREFERENCE extends FileSystemPreference> implements Itemspace, ItemInstanceCreator {
	// protected final CONFIG entryConfig;
	protected final ENTRY entry;
	protected final PREFERENCE preference;
	private FolderItem rootFolder;
	private FolderItem homeFolder;
	
	public ItemspaceBase(ENTRY entry) {
		super();
		this.entry = entry;
		this.preference = (PREFERENCE) entry.getPreference();
	}

	public ItemspaceBase(ENTRY entry, PREFERENCE preference) {
		super();
		this.entry = entry;
		this.preference = preference;
	}

	// protected abstract FolderItem createRootFolder();

	protected FolderItem createRootFolder() {
		FolderItem root = newFolderItemInstance(entry.getRootPath());
		((ItemHiddenFunction) root).setName(URLUtils.getLastNameFromPath(entry.getRootPath()));
		return root;
	}

	protected FolderItem createHomeFolder() {
		return createRootFolder();
	}
	
	@Override
	public ENTRY getFileSystemEntry() {
		return entry;
	}

	@Override
	public PREFERENCE getFileSystemPreference() {
		return preference;
	}

	@Override
	public ItemspaceSummary getSummary() {
		return new ItemspaceSummary(-1, -1, -1);
	}

	@Override
	public boolean isAvailable() throws ServiceException {
		return getHomeFolder().exists();
	}

	public FolderItem getRootFolder() {
		if (rootFolder == null) {
			rootFolder = createRootFolder();
		}
		return rootFolder;
	}
	
	public FolderItem getHomeFolder() {
		if (homeFolder == null) {
			homeFolder = createHomeFolder();
		}
		return homeFolder;
	}


	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public FileItem newFileItemInstance(FolderItem parentFolder, String name) {
		String fullpath = URLUtils.catPath(parentFolder.getPath(), name, entry.getSeparatorChar());
		FileItem item = newFileItemInstance(fullpath);
		((ItemHiddenFunction) item).setName(name);
		// ((ItemInnerFunc) item).setPath(fullpath);

		return item;
	}

	@Override
	public FolderItem newFolderItemInstance(FolderItem parentFolder, String name) {
		String fullpath = URLUtils.catPath(parentFolder.getPath(), name, entry.getSeparatorChar());
		FolderItem item = newFolderItemInstance(fullpath);
		((ItemHiddenFunction) item).setName(name);
		// ((ItemInnerFunc) item).setPath(fullpath);

		return item;
	}

	@Override
	public FileItem linkFile(String fullPath) throws ServiceException {
		FileItem item = this.newFileItemInstance(fullPath);

		String realFileName = URLUtils.getLastNameFromPath(fullPath);
		((ItemHiddenFunction) item).setName(realFileName);
		return item;
	}

	@Override
	public FolderItem linkFolder(String fullPath) throws ServiceException {
		FolderItem item = this.newFolderItemInstance(fullPath);

		String realFileName = URLUtils.getLastNameFromPath(fullPath);
		((ItemHiddenFunction) item).setName(realFileName);
		return item;
	}

	@Override
	public void list(final ListOption listOption, ItemHandler handler) throws ServiceException {
		FolderItem searchIn;
		String prefix = listOption.getPrefix();
		if (StringUtils.isNotEmpty(prefix)) {
			searchIn = this.newFolderItemInstance(prefix);
			ItemUtils.listAllItems(searchIn, listOption.getFilter(), listOption.isWithSubDirectory(), handler);
		} else {
			searchIn = this.getRootFolder();
			ItemUtils.listAllItems(searchIn, listOption.getPrefix(), listOption.getFilter(), listOption.isWithSubDirectory(), handler);
		}
		handler.handle(ItemEvent.EXEC_END, null);
	}

	@Override
	public void emptyingItemspace(final ItemHandler handler) throws ServiceException {
		list(null, new ItemHandler() {
			@Override
			public void exceptionCaught(Item data, Throwable e) {
				if (handler != null) {
					handler.exceptionCaught(data, e);
				}
			}

			@Override
			public HandleFeedback handle(Integer meta, Item item) {
				if (handler != null) {
					try {
						item.delete();
						HandleFeedback result = handler.handle(ItemEvent.ITEM_DELETED, item);
						return result;
					} catch (ServiceException e) {
						e.printStackTrace();
						HandleFeedback result = handler.handle(ItemEvent.ITEM_DELETE_FAILED, item);
						return result;
					}
				}
				return null;
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		ItemspaceBase a = (ItemspaceBase) obj;
		ItemspaceBase b = this;

		if (!a.getName().equals(b.getName())) {
			return false;
		}

		return a.getFileSystemEntry().equals(b.getFileSystemEntry());
	}

}
