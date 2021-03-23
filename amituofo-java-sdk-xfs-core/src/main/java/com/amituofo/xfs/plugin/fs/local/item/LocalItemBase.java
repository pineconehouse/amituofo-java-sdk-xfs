package com.amituofo.xfs.plugin.fs.local.item;

import java.io.File;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.local.LocalFileSystemEntryConfig;
import com.amituofo.xfs.service.FileSystem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemBase;

public abstract class LocalItemBase extends ItemBase<LocalDriver> implements FileSystem {
	public static final char SEPARATOR_CHAR = File.separatorChar;

	// private static int bufferSize = 1024 * 1024 * 10;
	protected final File file;

	public LocalItemBase(LocalDriver itemspace, File file) {
		super(itemspace);
		this.file = file;
	}

	@Override
	public char getPathSeparator() {
		return File.separatorChar;
	}

	public File getFile() {
		return file;
	}
	// @Override
	// public ItemLocation getLocationType() {
	// return ItemLocation.Local;
	// }

//	@Override
//	public RootItem getSection() {
//		if (super.rootitem == null) {
//			super.setSection(ItemUtils.toSection(file, false));
//		}
//		return super.getSection();
//	}

	@Override
	public FolderItem getParent() {
		// 有问题
		File parentFile = file.getParentFile();

		if (parentFile != null) {
			// parentFile.getAbsolutePath()
			FolderItem parent = ((LocalDriver) itemspace).newFolderItemInstance(parentFile.getPath());
			return parent;
		} else {
			return null;
		}
	}

	// @Override
	// public FolderItem getParent() {
	// // a
	// // a/
	// // /a
	// // a/b/
	// // /a/b
	// // /a/b/
	// String parentPath = URLUtils.getParentPath(this.getPath());
	//
	// if (parentPath == null) {
	// return null;
	// }
	//
	// FolderItem parent = ((LocalFileSystemEntry) fileSystemEntry).newFolderItemInstance(parentPath);
	// return parent;
	// }

//	@Override
//	public FolderItem[] getRoots() throws ServiceException {
//		// File[] viewRoots = FileSystemView.getFileSystemView().getRoots();
//		File[] drivers = File.listRoots();
//		LocalFolderItem[] roots = new LocalFolderItem[drivers.length];
//		// LocalFolderItem[] roots = new LocalFolderItem[drivers.length+viewRoots.length];
//		int i = 0;
//		for (; i < drivers.length; i++) {
//			File driver = drivers[i];
//			LocalFolderItem root = new LocalFolderItem(rootitem, (LocalFileSystemEntry) fileSystemEntry, preference, driver);
//			root.setName(driver.getPath());
//			// root.setData(driver);
//			root.setLastUpdateTime(null);
//			root.setCreateTime(null);
//			root.setPath(driver.getPath());
//			root.setSize(null);
//			// root.setType(ItemType.Directory);
//			// root.setParent(null);
//
//			roots[i] = root;
//		}
//
//		// for (; i < viewRoots.length + drivers.length; i++) {
//		// File viewRoot = viewRoots[i];
//		//
//		// LocalFolderItem root = new LocalFolderItem(preference);
//		// root.setName(viewRoot.getPath());
//		// // root.setData(driver);
//		// root.setLastUpdateTime(null);
//		// root.setCreateTime(null);
//		// root.setActualPath(viewRoot.getPath());
//		// root.setSize(null);
//		// // root.setType(ItemType.Directory);
//		// // root.setParent(null);
//		//
//		// roots[i] = root;
//		// }
//
//		return roots;
//	}

	@Override
	public boolean isSame(Item item) {
		if (!(isFromSameSystem(item))) {
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
		if (!(item instanceof LocalItemBase)) {
			return false;
		}

		if (!(item.getSystemName().equals(this.getSystemName()))) {
			return false;
		}

		return true;
	}

	@Override
	public String getSystemName() {
		return LocalFileSystemEntryConfig.SYSTEM_NAME;
	}

//	@Override
//	public String[] getSupportVersion() {
//		return new String[] { "" };
//	}

	@Override
	public boolean exists() throws ServiceException {
		return file.exists();
	}


//	public File toFile() {
//		return new File(this.getPath());
//	}

//	protected Item toItem(File file) {
//		LocalItemBase item;
//		if (file.isDirectory()) {
//			item = new LocalFolderItem(itemspace, file);
//		} else {
//			item = new LocalFileItem(itemspace, file);
//		}
//
//		item.setName(file.getName());
//		item.setSize(file.length());
//		item.setLastUpdateTime(file.lastModified());
//		item.setCreateTime(null);
//		item.setPath(file.getPath());
//		// item.setData(file);
//		// item.setParent(null);
//
//		return item;
//	}

}
