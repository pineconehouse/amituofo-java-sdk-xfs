package com.amituofo.xfs.plugin.fs.logic;

import com.amituofo.xfs.service.impl.FileSystemPreferenceBase;

public class LogicFileSystemPreference extends FileSystemPreferenceBase {
	boolean enableCache = false;

	public boolean isEnableCache() {
		return enableCache;
	}

	public void setEnableCache(boolean enableCache) {
		this.enableCache = enableCache;
	}
	
}
