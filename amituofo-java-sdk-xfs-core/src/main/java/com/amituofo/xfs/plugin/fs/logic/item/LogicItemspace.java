package com.amituofo.xfs.plugin.fs.logic.item;

import com.amituofo.xfs.plugin.fs.logic.ItemLister;
import com.amituofo.xfs.plugin.fs.logic.LogicFileSystemEntry;
import com.amituofo.xfs.plugin.fs.logic.LogicFileSystemPreference;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.ItemspaceBase;

public class LogicItemspace extends ItemspaceBase<LogicFileSystemEntry, LogicFileSystemPreference> {

	private ItemLister lister;

	public LogicItemspace(LogicFileSystemEntry entry, ItemLister lister) {
		super(entry);
		this.lister=lister;
	}

	@Override
	public String getName() {
		return entry.getName();// getQuery();
	}

	@Override
	public FolderItem newFolderItemInstance(String path) {
		return new LogicFolderItem(this, lister);
	}

	@Override
	public FileItem newFileItemInstance(String path) {
		return null;
	}
}
