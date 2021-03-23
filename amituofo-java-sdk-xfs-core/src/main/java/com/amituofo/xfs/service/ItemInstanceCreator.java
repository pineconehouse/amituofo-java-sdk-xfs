package com.amituofo.xfs.service;

public interface ItemInstanceCreator {
	FileItem newFileItemInstance(FolderItem parentFolder, String name);

	FolderItem newFolderItemInstance(FolderItem parentFolder, String name);

	FolderItem newFolderItemInstance(String fullpath);

	FileItem newFileItemInstance(String fullpath);

}
