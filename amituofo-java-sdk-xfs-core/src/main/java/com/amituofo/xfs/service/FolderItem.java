package com.amituofo.xfs.service;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;

public interface FolderItem extends Item {
	void list(ItemFilter filter, ItemHandler handler);
	
	default void list(ItemHandler handler) {
		list(null, handler);
	}

	default void listFolders(final ItemFilter filter, ItemHandler handler) {
		this.list(new ItemFilter() {

			@Override
			public boolean accept(Item item) {
				if (filter != null) {
					return item.isDirectory() && filter.accept(item);
				} else {
					return item.isDirectory();
				}
			}
		}, handler);
	}

//	boolean deleteEmptyFolder() throws ServiceException;

	boolean delete(ItemHandler handler);

	boolean createDirectory() throws ServiceException;

	default boolean createDirectory(String name) throws ServiceException {
		return this.linkFolder(name).createDirectory();
	}

	default ItemType getType() {
		return ItemType.Directory;
	}

	default boolean isDirectory() {
		return true;
	}

	default boolean isFile() {
		return false;
	}
	
	default boolean isRoot() {
		return this.equals(getItemspace().getRootFolder());
	}


//	FileItem linkFile(String fileName) throws ServiceException;

//	FolderItem linkFolder(String folderName) throws ServiceException;
	
	default FileItem linkFile(String fileName) throws ServiceException {
		char pathSeparator = this.getPathSeparator();
		String fullPath;
		FileItem item;

//		String realFileName = URLUtils.getLastNameFromPath(fileName, pathSeparator);
		// 文件名长度相等，代表这个不是一个路径
		// if (realFileName.length() == fileName.length()) {
		// if (fileName.charAt(0) == pathSeparator) {
		// fullPath = fileName;
		// } else {
		fullPath = URLUtils.catPath(this.getPath(), fileName, pathSeparator);
		// }

//		item = ((ItemInstanceCreator) getItemspace()).newFileItemInstance(fullPath);
//		((ItemHiddenFunction) item).setName(realFileName);
		item = this.getItemspace().linkFile(fullPath);
		return item;
	}

	default FolderItem linkFolder(String folderName) throws ServiceException {
		if (StringUtils.isEmpty(folderName)) {
			return (FolderItem) this;
		}

		char pathSeparator = this.getPathSeparator();
		String fullPath;
		FolderItem item;
		//
		// String realFileName = URLUtils.getLastNameFromPath(folderName, pathSeparator);
		// // 文件名长度相等，代表这个不是一个路径
		// // if (realFileName.length() == fileName.length()) {
		// // if (folderName.charAt(0) == pathSeparator) {
		// // fullPath = folderName;
		// // } else {
		fullPath = URLUtils.catPath(this.getPath(), folderName, pathSeparator);
		// // }
		//
		// item = ((ItemInstanceCreator) getItemspace()).newFolderItemInstance(fullPath);
		// ((ItemHiddenFunction) item).setName(realFileName);

		item = this.getItemspace().linkFolder(fullPath);
		return item;
	}

}
