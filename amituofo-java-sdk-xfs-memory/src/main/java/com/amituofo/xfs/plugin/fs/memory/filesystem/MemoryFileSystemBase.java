package com.amituofo.xfs.plugin.fs.memory.filesystem;

import com.amituofo.xfs.plugin.fs.memory.MemoryFileSystemEntry;
import com.amituofo.xfs.plugin.fs.memory.item.MemoryItemspace;

public abstract class MemoryFileSystemBase implements MemoryFileSystem {
	protected final MemoryFileSystemEntry fileSystemEntry;
//	protected final MemoryItemspace itemspace;

	public MemoryFileSystemBase(MemoryFileSystemEntry fileSystemEntry) {
		super();
		this.fileSystemEntry = fileSystemEntry;
//		this.itemspace = fileSystemEntry.getDefaultItemspace();
	}
	
	protected MemoryItemspace getItemspace() {
		return fileSystemEntry.getDefaultItemspace();
	}
}
