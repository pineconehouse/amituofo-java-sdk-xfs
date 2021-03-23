package com.amituofo.xfs.plugin.fs.memory;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.config.EntryConfigBase;
import com.amituofo.xfs.plugin.fs.memory.filesystem.GoogleJimfs;
import com.amituofo.xfs.plugin.fs.memory.filesystem.MemoryFileSystem;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;
import com.amituofo.xfs.service.FileSystemType;

public class MemoryFileSystemEntryConfig extends EntryConfigBase {
	public static final String SYSTEM_NAME = "Memory File System";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP53467290";

	public static final String MAX_MEMORY_STORAGE_SIZE = "MAX_MEMORY_STORAGE_SIZE";
	public static final String MAX_MEMORY_STORAGE_SIZE_IN_PRECENT = "MAX_MEMORY_STORAGE_SIZE_IN_PRECENT";

	public MemoryFileSystemEntryConfig() {
		super(MemoryFileSystemEntryConfig.class, SYSTEM_ID, "", FileSystemType.Remote, URL_ROOT_PATH, URL_PS);
	}

	@Override
	protected void validate() throws InvalidParameterException {
		ValidUtils.invalidIfNotA2zOr0to9(this.getName(), "Name must consist of English letters and numbers (a-z A-Z 0-9)");
	}

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new MemoryFileSystemEntry(this, (MemoryFileSystemPreference) perference);
	}

	@Override
	public FileSystemPreference createPreference() {
		return new MemoryFileSystemPreference();
	}

	public Class<? extends MemoryFileSystem> getMemoryFileSystem() {
		return GoogleJimfs.class;
	}

	public int getRootCount() {
		return 1;
	}

	public Long getMemoryStorageSize() {
		long storageSize = config.getLong(MAX_MEMORY_STORAGE_SIZE, -1L);
		if (storageSize == -1) {
			double precent = this.getMemoryStorageSizeInPrecent();
			long maxmem = Runtime.getRuntime().maxMemory();
			storageSize = (long) (maxmem * precent);
		}

		return storageSize;
	}

	public void setMemoryStorageSize(long size) {
		config.set(MAX_MEMORY_STORAGE_SIZE, size);
	}

	public double getMemoryStorageSizeInPrecent() {
		return config.getDouble(MAX_MEMORY_STORAGE_SIZE_IN_PRECENT, (double) 0.1);
	}

	public void setMemoryStorageSizeInPrecent(double precent) {
		if (precent > 1) {
			precent = 1;
		}
		if (precent <= 0) {
			precent = (double) 0.1;
		}
		config.set(MAX_MEMORY_STORAGE_SIZE_IN_PRECENT, precent);
	}

	@Override
	public void validateConfig() throws InvalidParameterException {
		super.validateConfig();

		long size = getMemoryStorageSize();
		long maxmem = Runtime.getRuntime().maxMemory();
		ValidUtils.invalidIfGreaterThan(size, maxmem, "The specified memory size exceeds the available memory size");

	}

}
