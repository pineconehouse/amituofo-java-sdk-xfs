package com.amituofo.xfs.plugin.fs.objectstorage.azure.blobs.item;

import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDItemBase;
import com.amituofo.xfs.plugin.fs.objectstorage.azure.blobs.BlobFileSystemEntryConfig;
import com.amituofo.xfs.service.FileSystem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;
import com.azure.storage.blob.BlobContainerClient;

public abstract class BlobItemBase extends OSDItemBase<BlobContainerspace> implements FileSystem {
	public BlobItemBase(BlobContainerspace namespace, String key) {
		super(namespace, key);
	}

	public BlobContainerClient getContainerClient() {
		return itemspace.getContainerClient();
	}

//	@Override
//	public String getBucketName() {
//		return itemspace.getName();
//	}

	// @Override
	public String getEndpoint() {
		return itemspace.getEndpoint();
	}

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

	// @Override
	// public FolderItem[] getRoots() throws ServiceException {
	//// if (roots != null) {
	//// return roots;
	//// }
	//
	// List<NamespaceBasicSetting> namespaces = null;
	// try {
	// namespaces = hcpNamespace.listAccessibleNamespaces();
	// } catch (InvalidResponseException e) {
	// throw new ServiceException(e.getMessage(), e);
	// } catch (HSCException e) {
	// throw new ServiceException(e);
	// }
	//
	// List<HCPFolderItem> rootList = new ArrayList<HCPFolderItem>();
	// for (int i = 0; i < namespaces.size(); i++) {
	// NamespaceBasicSetting namespaceSetting = namespaces.get(i);
	//
	// HCPFolderItem item = fileSystemEntry.newFolderItemInstance(namespaceSetting);
	//// HCPNamespace newHCPClient = HCPClientFactory.getInstance().getHCPClient(hcpNamespace, namespaceSetting.getName());
	//
	//// HCPFolderItem item = new HCPFolderItem(preference, (HCPFileSystemEntry) fileSystemEntry, newHCPClient, namespaceSetting);
	// item.setName(namespaceSetting.getName());
	// // item.setData(hcpObjectEntry.getKey());
	// item.setPath("/");
	// // item.setParent(null);
	// // item.setType(ItemType.Directory);
	// // item.setCatelog(namespace);
	// item.setSize(null);
	// item.setLastUpdateTime(null);
	// item.setCreateTime(null);
	//
	// rootList.add(item);
	// // roots[max--] = item;
	// }
	//
	// // 避免两个桶一个http 一个https，会导致getroot失败
	// FolderItem[] roots = rootList.toArray(new HCPFolderItem[rootList.size()]);
	// return roots;
	// }

	@Override
	public String getSystemName() {
		return BlobFileSystemEntryConfig.SYSTEM_NAME;
	}

//	@Override
//	public String[] getSupportVersion() {
//		return new String[] {};
//	}

}
