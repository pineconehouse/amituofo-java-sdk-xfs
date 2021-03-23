package com.amituofo.xfs.plugin.fs.objectstorage.hcp;

import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemPreference;

public class HCPFileSystemPreference extends OSDFileSystemPreference {
	private boolean enablePurgeDeletion = false;
	private boolean showDeletedObjects = false;

	public HCPFileSystemPreference(boolean enablePurgeDeletion, boolean showDeletedObjects) {
		super();
		this.enablePurgeDeletion = enablePurgeDeletion;
		this.showDeletedObjects = showDeletedObjects;
	}

	public boolean isEnablePurgeDeletion() {
		return enablePurgeDeletion;
	}

	public void setEnablePurgeDeletion(boolean enablePurgeDeletion) {
		this.enablePurgeDeletion = enablePurgeDeletion;
	}

	public boolean isShowDeletedObjects() {
		return showDeletedObjects;
	}

	public void setShowDeletedObjects(boolean showDeletedObjects) {
		this.showDeletedObjects = showDeletedObjects;
	}

}
