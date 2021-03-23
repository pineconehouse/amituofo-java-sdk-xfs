package com.amituofo.xfs.plugin.fs.local.item;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.plugin.fs.local.LocalFileSystemEntry;
import com.amituofo.xfs.plugin.fs.local.LocalFileSystemPreference;
import com.amituofo.xfs.plugin.fs.local.LocalFileSystemView;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemspaceBase;
import com.amituofo.xfs.service.ItemspaceSummary;

public class LocalDriver extends ItemspaceBase<LocalFileSystemEntry, LocalFileSystemPreference> {

	private File driver;
	private final boolean isDefaultView;

	public LocalDriver(LocalFileSystemEntry entry, LocalFileSystemPreference preference, File driver) {
		super(entry, preference);
		this.driver = driver;
		this.isDefaultView = preference.getFileSystemView() == LocalFileSystemView.DEFAULT_FILE_SYSTEM_VIEW;
	}

	@Override
	public String getName() {
		if (isDefaultView) {
			return driver.getPath();
		} else {
			String name = driver.getName();
			if (StringUtils.isEmpty(name)) {
				return driver.getPath();
			}

			return name;
		}
	}

	@Override
	public ItemspaceSummary getSummary() {
		long total = driver.getTotalSpace();
		return new ItemspaceSummary(-1, total, total - driver.getFreeSpace());
	}

	@Override
	protected FolderItem createRootFolder() {
		// File file;
		// if (isDefaultView) {
		// file = new File(driver.getPath());
		// } else {
		// file = LocalFileSystemEntry.fileSystemView.createFileObject(driver.getPath());
		// }

		FolderItem root = newFolderItemInstance(driver);
		// ((ItemInnerFunc) root).setName(getName());
		// ((ItemInnerFunc) root).setPath(driver.getPath());
		// root.setSize(null);
		// root.setCreateTime(null);
		// root.setLastUpdateTime(null);
		// root.setParent(null);
		// root.setCatelog(null);
		// root.setData(null);
		return root;
	}

	@Override
	public FileItem newFileItemInstance(String filepath) {
		File file = new File(filepath);
		return newFileItemInstance(file);

		// if (isDefaultView) {
		// File file = new File(filepath);
		// return new LazyLocalFileItem(this, file);
		// } else {
		// File file = FileSystemView.getFileSystemView().createFileObject(filepath);
		// return new LazyLocalFSVFileItem(this, file);
		// }
	}

	@Override
	public FolderItem newFolderItemInstance(String filepath) {
		File file = new File(filepath);
		return newFolderItemInstance(file);

		// if (isDefaultView) {
		// File file = new File(filepath);
		// return new LazyLocalFolderItem(this, file);
		// } else {
		// File file = FileSystemView.getFileSystemView().createFileObject(filepath);
		// return new LazyLocalFSVFolderItem(this, file);
		// }
	}

	public FolderItem newFolderItemInstance(File file) {
		LocalFolderItem item;
		if (isDefaultView) {
			item = new LazyLocalFolderItem(this, file);
		} else {
			item = new LazyLocalFSVFolderItem(this, file);
		}

		return item;
	}

	//
	public FileItem newFileItemInstance(File file) {
		FileItem item;
		if (isDefaultView) {
			item = new LazyLocalFileItem(this, file);
		} else {
			item = new LazyLocalFSVFileItem(this, file);
		}
		return item;
	}

	public Item newItemInstance(File file) {
		Item item;
		if (file.isDirectory()) {
			item = newFolderItemInstance(file);
		} else {
			item = newFileItemInstance(file);
		}

		return item;
	}

}
