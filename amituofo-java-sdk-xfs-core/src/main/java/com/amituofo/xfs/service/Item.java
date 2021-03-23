package com.amituofo.xfs.service;

import com.amituofo.common.ex.ServiceException;

public interface Item extends FileSystem, Properties, Cloneable {
	char getPathSeparator();

	boolean isDirectory();

	boolean isFile();

	Item clone();

	FolderItem getParent();// throws ServiceException;

	FolderItem getRoot();

	Object getData();

	void setData(Object data);

	Itemspace getItemspace();

	Itemspace[] getItemspaces() throws ServiceException;

	boolean isSame(Item item);

	boolean isFromSameSystem(Item item);

	boolean equals(Object obj);

	// URI toURI();

	// RootItem getSection();
}
