package com.amituofo.xfs.plugin.fs.logic;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.xfs.config.EntryConfigBase;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;
import com.amituofo.xfs.service.FileSystemType;

public class LogicFileSystemEntryConfig extends EntryConfigBase {
	public static final String SYSTEM_NAME = "Logic File System";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP327409830";
	private ItemLister lister;
	private boolean enableCache = false;

	// public LogicFileSystemEntryConfig() {
	// this("");
	// }

	public LogicFileSystemEntryConfig(String name, ItemLister lister) {
		super(LogicFileSystemEntryConfig.class, SYSTEM_ID, name, FileSystemType.Remote, URL_ROOT_PATH, URL_PS);
		this.lister = lister;
	}

	@Override
	public FileSystemEntry createFileSystemEntry() {
		return new LogicFileSystemEntry(this, (LogicFileSystemPreference) createPreference(), lister);
	}

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new LogicFileSystemEntry(this, (LogicFileSystemPreference) perference, lister);
	}

	@Override
	public FileSystemPreference createPreference() {
		LogicFileSystemPreference p = new LogicFileSystemPreference();
		p.setEnableCache(enableCache);
		return p;
	}

	@Override
	protected void validate() throws InvalidParameterException {

	}

	public void setEnableCache(boolean enable) {
		this.enableCache = enable;
	}

}
