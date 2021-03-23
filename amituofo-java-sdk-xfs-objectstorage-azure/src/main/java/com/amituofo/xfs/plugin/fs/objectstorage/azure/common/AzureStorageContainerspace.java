package com.amituofo.xfs.plugin.fs.objectstorage.azure.common;

import com.amituofo.xfs.plugin.fs.objectstorage.OSDBucketspace;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDItemInstanceCreator;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemspaceBase;

public abstract class AzureStorageContainerspace<CLIENT, ENTRY extends AzureStorageFileSystemEntry, PREFERENCE extends AzureStorageFileSystemPreference> extends ItemspaceBase<ENTRY, PREFERENCE>
		implements OSDBucketspace, OSDItemInstanceCreator {
	protected final CLIENT fslient;

	public AzureStorageContainerspace(ENTRY entry, CLIENT containerClient) {
		super(entry);
		this.fslient = containerClient;
	}

	@Override
	protected FolderItem createRootFolder() {
		FolderItem root = newFolderItemInstance("");
		((ItemHiddenFunction) root).setName(getName());
		return root;
	}

	@Override
	public String getEndpoint() {
		return ((AzureStorageFileSystemEntryConfig) entry.getEntryConfig()).getEndpoint();
	}

	public CLIENT getContainerClient() {
		return fslient;
	}

}
