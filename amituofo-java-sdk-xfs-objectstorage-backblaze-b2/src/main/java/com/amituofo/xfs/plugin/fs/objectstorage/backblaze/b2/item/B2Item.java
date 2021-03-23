package com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2.item;

import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDItemBase;
import com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2.B2FileSystemEntryConfig;
import com.amituofo.xfs.service.ContentHash;
import com.amituofo.xfs.service.FileSystem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;
import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2FileVersion;
import com.backblaze.b2.client.structures.B2GetFileInfoByNameRequest;

public abstract class B2Item extends OSDItemBase<B2Bucketspace> implements FileSystem {

	protected String fileId;
	protected boolean isHide;

	public B2Item(B2Bucketspace bucket, String key) {
		super(bucket, key);
	}

	public B2StorageClient getB2Client() {
		return itemspace.getB2Client();
	}

	@Override
	public String getSystemName() {
		return B2FileSystemEntryConfig.SYSTEM_NAME;
	}

	@Override
	public Item clone() {
		B2Item clone = (B2Item) super.clone();
		clone.fileId = this.fileId;
		return clone;
	}

	@Override
	public char getPathSeparator() {
		return '/';
	}

	@Override
	public int getStatus() {
		return isHide ? Item.ITEM_STATUS_HIDDEN : 0;
	}

	public boolean isHide() {
		return isHide;
	}

	public String getFileId() {
		if (fileId == null) {
			// B2GetFileInfoByNameRequest request=B2GetFileInfoByNameRequest.builder(this.getItemspace().getName(), this.getName()).build();
			// getB2Client().getFileInfoByName(request);
			try {
				B2FileVersion info = getB2Client().getFileInfoByName(this.getItemspace().getName(), this.getPath());
				fileId = info.getFileId();
			} catch (B2Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return fileId;
	}

	// @Override
	// public FolderItem getParent() {
	// // a
	// // a/
	// // /a
	// // a/b/
	// // /a/b
	// // /a/b/
	// String parentPath = URLUtils.getParentPath(this.getPath(), SEPARATOR_CHAR, "/");
	//
	// // if (parentPath == null) {
	// // return null;
	// // }
	//
	// if ("/".equals(parentPath)) {
	// return itemspace.getRootFolder();
	// }
	//
	// String name = URLUtils.getLastNameFromPath(parentPath);
	// FolderItem parent = ((ItemInstanceCreator) itemspace).newFolderItemInstance(parentPath);
	// ((ItemHiddenFunction) parent).setName(name);
	// // ((ItemInnerFunc) parent).setPath(parentPath);
	//
	// return parent;
	// }

	@Override
	public void setPath(String path) {
		int len = path.length();
		if (len > 0 && path.charAt(0) == '/') {
			path = path.substring(1);
		}
		super.setPath(path);
	}

	// @Override
	// public String getPath() {
	// String path = super.getPath();
	// if (path.length() == 0) {
	// return "/";
	// }
	//
	// return path;
	// }

	// @Override
	// public String[] getSupportVersion() {
	// // TODO Auto-generated method stub
	// return new String[] { "x.x" };
	// }

	protected Item createItem(B2FileVersion file) {
		String fullpath = file.getFileName();
		String filename = URLUtils.getLastNameFromPath(fullpath);
		Item item;
		if (file.isFolder()) {
			item = ((ItemInstanceCreator) itemspace).newFolderItemInstance(fullpath);
		} else {
			item = ((ItemInstanceCreator) itemspace).newFileItemInstance(fullpath);
			((ItemHiddenFunction) item).setSize(file.getContentLength());
			((B2FileItem) item).setContentHash(new ContentHash(file.getContentMd5()));
		}
		((ItemHiddenFunction) item).setName(filename);
		((ItemHiddenFunction) item).setLastUpdateTime(file.getUploadTimestamp());
		((ItemHiddenFunction) item).setCreateTime(file.getUploadTimestamp());

		((B2Item) item).fileId = file.getFileId();
		((B2Item) item).isHide = file.isHide();

		return item;
	}

	// protected FolderItem createDirItem(String dirKey) throws ServiceException {
	// String key = getPathSeparator() + dirKey;
	//
	// FolderItem item = ((ItemInstanceCreator) itemspace).newFolderItemInstance(key);
	// ((ItemHiddenFunction) item).setName(URLUtils.getLastNameFromPath(key));
	// // item.setData(hcpObjectEntry.getKey());
	// // ((ItemInnerFunc)item).setPath(fileSystemEntry.getEntryConfig().getRootPath() + dirKey);
	// // item.setParent(this);
	// // item.setType(ItemType.Directory);
	// // item.setSize(null);
	//
	// // item.setSummary(hcpObjectEntry);
	// return item;
	// }

	@Override
	public boolean isFromSameSystem(Item item) {
		if (!(item instanceof B2Item)) {
			return false;
		}

		if (!(item.getSystemName().equals(this.getSystemName()))) {
			return false;
		}
		return true;
	}

}
