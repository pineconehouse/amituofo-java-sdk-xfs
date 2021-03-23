package com.amituofo.xfs.plugin.fs.webdav.item;

import java.io.IOException;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.webdav.WebDavFileSystemEntry;
import com.amituofo.xfs.plugin.fs.webdav.WebDavFileSystemEntryConfig;
import com.amituofo.xfs.service.FileSystem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemBase;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;
import com.github.sardine.Sardine;

public abstract class WebDavItemBase extends ItemBase<WebDavItemspace> implements FileSystem {
	public static final char SEPARATOR_CHAR = '/';
	protected final Sardine sardine;
	protected String uri = null;

	public WebDavItemBase(WebDavItemspace itemspace, String filepath) {
		super(itemspace);
		this.sardine = itemspace.getFileSystemEntry().getSardine();
		this.setPath(filepath);
		this.uri = URLUtils.requestPathEncode(filepath, "utf-8");
	}

	@Override
	public boolean delete() throws ServiceException {
		try {
			sardine.delete(this.getPathUrl());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean exists() throws ServiceException {
		try {
			return sardine.exists(this.getPathUrl());
		} catch (IOException e) {
			e.printStackTrace();
			// return false;
			throw new ServiceException(e);
		}
	}

	@Override
	public void rename(String newfilename) throws ServiceException {
		try {
			String newfilepath;

			String parentPath = URLUtils.getParentPath(this.getPath(), this.getPathSeparator(), getFileSystemEntry().getRootPath());
			newfilepath = URLUtils.catPath(parentPath, newfilename, itemspace.getFileSystemEntry().getSeparatorChar());

			sardine.move(this.getPathUrl(), newfilepath);
		} catch (IOException e) {
			// e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	public String getPathUrl() {
		if (uri == null) {
			uri = URLUtils.requestPathEncode(this.getPath(), "utf-8");
		}

		String url = URLUtils.catPath(((WebDavFileSystemEntry) this.getFileSystemEntry()).getEntryConfig().getHttpHostUrl(), uri);
		return url;
	}

	@Override
	public int getStatus() {
		return 0;
	}

	@Override
	public char getPathSeparator() {
		return '/';
	}

	// @Override
	// public FolderItem getParent() {
	// String parentPath = URLUtils.getParentPath(this.getPath());
	//
	// if (parentPath == null) {
	// return null;
	// }
	//
	// // VirtualFSI parentMemVirtualFSI = fileSystemEntry.getVirtualFileSystem().getVirtualFSI(parentPath, ItemType.Directory);
	// // VirtualFolderItem parent = new VirtualFolderItem(rootitem, fileSystemEntry, preference, parentMemVirtualFSI);
	// FolderItem parent = ((ItemInstanceCreator) itemspace).newFolderItemInstance(parentPath);
	// ((ItemHiddenFunction) parent).setName(URLUtils.getLastNameFromPath(parentPath));
	// // ((ItemInnerFunc) parent).setPath(parentPath);
	//
	// return parent;
	// }

	@Override
	public boolean isSame(Item item) {
		if (!(isFromSameSystem(item))) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isFromSameSystem(Item item) {
		if (!(item instanceof WebDavItemBase)) {
			return false;
		}

		return true;
	}

	@Override
	public String getSystemName() {
		return WebDavFileSystemEntryConfig.SYSTEM_NAME;
	}

	// @Override
	// public String[] getSupportVersion() {
	// return new String[] { "" };
	// }

}
