package com.amituofo.xfs.plugin.fs.objectstorage.hcp.item;

import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDItemBase;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.HCPFileSystemEntryConfig;
import com.amituofo.xfs.service.FileSystem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;
import com.hitachivantara.hcp.standard.api.HCPNamespace;
import com.hitachivantara.hcp.standard.define.ObjectState;

public abstract class HCPItemBase extends OSDItemBase<HCPBucketspace> implements FileSystem {
	protected ObjectState state;

	public HCPItemBase(HCPBucketspace namespace, String key) {
		super(namespace, key);
	}

	public HCPNamespace getHcpClient() {
		return itemspace.getHcpClient();
	}

//	@Override
//	public String getBucketName() {
//		return itemspace.getNamespace();
//	}

	// @Override
	public String getEndpoint() {
		return itemspace.getEndpoint();
	}

	@Override
	public int getStatus() {
		if (state == ObjectState.deleted) {
			return Item.ITEM_STATUS_DELETED;
		}

		return 0;
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
//		if ("/".equals(parentPath)) {
//			// name = hcpNamespace.getNamespace();
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
		return HCPFileSystemEntryConfig.SYSTEM_NAME;
	}

//	@Override
//	public String[] getSupportVersion() {
//		return new String[] { "8.x" };
//	}

}
