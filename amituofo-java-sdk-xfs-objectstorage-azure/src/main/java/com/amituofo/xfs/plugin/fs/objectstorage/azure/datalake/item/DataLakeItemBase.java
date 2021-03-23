package com.amituofo.xfs.plugin.fs.objectstorage.azure.datalake.item;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.URLUtils;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDItemBase;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.datalake.DataLakeFileSystemEntryConfig;
import com.amituofo.xfs.service.FileSystem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;
import com.azure.storage.file.datalake.DataLakeFileSystemClient;

public abstract class DataLakeItemBase extends OSDItemBase<DataLakeContainerspace> implements FileSystem {
	protected String permissions;
	protected String etag;
	protected String group;
	protected String owner;

	public DataLakeItemBase(DataLakeContainerspace namespace, String key) {
		super(namespace, key);
	}

	public DataLakeFileSystemClient getContainerClient() {
		return itemspace.getContainerClient();
	}

//	@Override
	public String getETag() {
		return etag;
	}

	public String getGroup() {
		return group;
	}

//	@Override
	public String getOwner() {
		return owner;
	}

	public String getPermissions() {
		return permissions;
	}

//	@Override
//	public String getBucketName() {
//		return itemspace.getName();
//	}

	// @Override
//	public String getEndpoint() {
//		return itemspace.getEndpoint();
//	}

	@Override
	public int getStatus() {
		return 0;
	}

	@Override
	public void setPath(String path) {
		int len = path.length();
		if (len > 0 && path.charAt(0) == '/') {
			path = path.substring(1);
		}
		super.setPath(path);
	}

	// @Override
	// public FolderItem getParent() {
	// String key = this.getPath();
	// if ("/".equals(key)) {
	// return null;
	// }
	//
	// int li = key.lastIndexOf("/", key.length() - 1);
	//// HCPFolderItem parent = new HCPFolderItem(preference, (HCPFileSystemEntry) fileSystemEntry, hcpNamespace, namespaceSetting);
	// HCPFolderItem parent = fileSystemEntry.newFolderItemInstance(namespaceSetting);
	//
	// String parentKey = (li <= 0 ? "/" : key.substring(0, li));
	// String parentName;
	//
	// if (li <= 0) {
	// parentName = hcpNamespace.getNamespace();
	// } else {
	// int li2 = parentKey.lastIndexOf("/", parentKey.length() - 1);
	// parentName = parentKey.substring(li2 + 1, li);
	// }
	// parent.setName(parentName);
	// parent.setPath(parentKey);
	//
	// return parent;
	// }

//	@Override
//	public FolderItem getParent() {
//		// a
//		// a/
//		// /a
//		// a/b/
//		// /a/b
//		// /a/b/
//		String parentPath = URLUtils.getParentPath(this.getPath());
//
//		if (parentPath == null) {
//			return null;
//		}
//
//		if (parentPath.length() == 0 || "/".equals(parentPath)) {
//			return itemspace.getRootFolder();
//		}
//
//		FolderItem parent = ((ItemInstanceCreator) itemspace).newFolderItemInstance(parentPath);
//		String name = URLUtils.getLastNameFromPath(parentPath);
//		((ItemHiddenFunction) parent).setName(name);
//		// ((ItemInnerFunc) parent).setPath(parentPath);
//
//		return parent;
//	}

	@Override
	public void rename(String newname) throws ServiceException {
		try {
			ValidUtils.invalidIfEmpty(newname, "A new name must be specified!");
			ValidUtils.invalidIfEqual(newname, this.getName(), "New name must be different with current name!");
		} catch (InvalidParameterException e) {
			throw new ServiceException(e);
		}

		String newpath = URLUtils.catPath(this.getParent().getPath(), newname, this.getPathSeparator());
		getContainerClient().getFileClient(this.getPath()).rename(getContainerClient().getFileSystemName(), newpath);
		// throw new ServiceException("Unsupport operation!");
	}
	
	@Override
	public String getSystemName() {
		return DataLakeFileSystemEntryConfig.SYSTEM_NAME;
	}

//	@Override
//	public String[] getSupportVersion() {
//		return new String[] {};
//	}

}
