package com.amituofo.xfs.plugin.fs.objectstorage;

import com.amituofo.xfs.service.FileSystem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemBase;

public abstract class OSDItemBase<OSDBUCKET extends OSDBucketspace> extends ItemBase<OSDBUCKET> implements FileSystem {
	public static final char SEPARATOR_CHAR = '/';

	public OSDItemBase(OSDBUCKET bucket, String key) {
		super(bucket);
		this.setPath(key);
	}
	
//	public abstract String getBucketName();
//	public abstract String[] listAccessibleBuckets();

	@Override
	public char getPathSeparator() {
		return '/';
	}

//	@Override
//	public ItemLocation getLocationType() {
//		return ItemLocation.Remote;
//	}

//	@Override
//	public FolderItem getRoot() throws ServiceException {
//		FolderItem root = super.getRoot();
//
//		((ItemInnerFunc)root).setName(getBucketName());
//
//		return root;
//	}

	@Override
	public boolean isSame(Item item) {
		if (!(isFromSameSystem(item))) {
			return false;
		}

		OSDItemBase item1 = (OSDItemBase) item;
		if (!this.getItemspace().getName().equalsIgnoreCase(item1.getItemspace().getName())) {
			return false;
		}

		if (!this.getPath().equals(item.getPath())) {
			return false;
		}

		if (this.getType() != item.getType()) {
			return false;
		}

		if (this.getSize() != item.getSize()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isFromSameSystem(Item item) {
		if (!(item instanceof OSDItem)) {
			return false;
		}

		if (!(item.getSystemName().equals(this.getSystemName()))) {
			return false;
		}
		
		OSDFileSystemEntryConfig swdConfig = (OSDFileSystemEntryConfig) this.getFileSystemEntry().getEntryConfig();
		OSDFileSystemEntryConfig twdConfig = (OSDFileSystemEntryConfig) item.getFileSystemEntry().getEntryConfig();
		if (!swdConfig.getEndpoint().equalsIgnoreCase(twdConfig.getEndpoint())) {
			return false;
		}

		return true;
	}


}
