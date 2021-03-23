package com.amituofo.xfs.plugin.fs.memory.item;

import com.amituofo.xfs.plugin.fs.memory.MemoryFileSystemEntry;
import com.amituofo.xfs.plugin.fs.memory.MemoryFileSystemPreference;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.ItemspaceBase;

public class MemoryItemspace extends ItemspaceBase<MemoryFileSystemEntry, MemoryFileSystemPreference> {
	private String name;

	public MemoryItemspace(MemoryFileSystemEntry fileSystemEntry, String name) {
		super(fileSystemEntry);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public FolderItem newFolderItemInstance(String fullpath) {
		return new MemoryFolderItem(entry.getDefaultMemoryFileSystem(), this, fullpath);
	}

	@Override
	public FileItem newFileItemInstance(String fullpath) {
		return new MemoryFileItem(entry.getDefaultMemoryFileSystem(), this, fullpath);
	}

	// @Override
	// protected FolderItem createRootFolder() {
	// return newFolderItemInstance("/");
	// }

}
