package com.amituofo.xfs.plugin.fs.webdav.item;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.webdav.WebDavFileSystemEntry;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemType;
import com.amituofo.xfs.service.ItemspaceBase;
import com.github.sardine.DavResource;

public class WebDavFolderItem extends WebDavItemBase implements FolderItem {
	public WebDavFolderItem(WebDavItemspace itemspace, String filepath) {
		super(itemspace, filepath);
	}

	@Override
	public void list(ItemFilter filter, ItemHandler handler) {
		try {
			List<DavResource> list = sardine.list(this.getPathUrl());
			for (DavResource davResource : list) {
				// 忽略列出的本目录
				if (davResource.isDirectory() &&  this.getPath().equals(davResource.getPath())) {
					continue;
				}

				Item item = toItem(davResource);

				if (filter != null && !filter.accept(item)) {
					continue;
				}

				handler.handle(ItemEvent.ITEM_FOUND, item);
			}

			handler.handle(ItemEvent.EXEC_END, null);

		} catch (IOException e) {
			e.printStackTrace();
			handler.exceptionCaught(null, e);
		}
	}

	private Item toItem(DavResource fileStatus) {
		Item item;
		// String name = fileStatus.getDisplayName();
		// String fullpath = URLUtils.catPath(this.getPath(), name, getFileSystemEntry().getSeparatorChar());
		String fullpath = fileStatus.getPath();
		if (fileStatus.isDirectory()) {
			item = ((ItemspaceBase) itemspace).newFolderItemInstance(fullpath);
		} else {
			item = ((ItemspaceBase) itemspace).newFileItemInstance(fullpath);
		}

		Date cTime = fileStatus.getCreation();
		Date mTime = fileStatus.getModified();
		// ((WebDavItemBase) item).uri = fileStatus.getHref().toString();
		// ((HadoopHDFSItemBase) item).setFileStatus(fileStatus);
		((ItemHiddenFunction) item).setName(fileStatus.getDisplayName());
		((ItemHiddenFunction) item).setSize(fileStatus.getContentLength());
		if (mTime != null) {
			((ItemHiddenFunction) item).setLastUpdateTime(mTime.getTime());
		}
		if (cTime != null) {
			((ItemHiddenFunction) item).setCreateTime(cTime.getTime());
		}
		// ((ItemHiddenFunction) item).setLastAccessTime(fileStatus.get);

		return item;
	}

	@Override
	public boolean exists() throws ServiceException {
		if ("/".equals(this.getPath())) {
			return true;
		}

		return super.exists();
	}

	@Override
	public boolean delete(ItemHandler handler) {
		boolean deleted;
		try {
			deleted = super.delete();
			if (handler != null) {
				handler.handle(deleted ? ItemEvent.ITEM_DELETED : ItemEvent.ITEM_DELETE_FAILED, this);
			}
			return deleted;
		} catch (ServiceException e) {
			e.printStackTrace();
			if (handler != null) {
				handler.exceptionCaught(this, e);
			}
			return false;
		}
	}

//	@Override
//	public boolean deleteEmptyFolder() throws ServiceException {
//		return delete(null);
//	}

	public String getPathUrl(String name) {
		String path = URLUtils.catPath(this.getPath(), name);
		String url = URLUtils.catPath(((WebDavFileSystemEntry) this.getFileSystemEntry()).getEntryConfig().getHost(), path);
		return URLUtils.urlEncode(url, "utf-8");
	}

//	@Override
//	public boolean createDirectory(String name) throws ServiceException {
//		try {
//			sardine.createDirectory(getPathUrl(name));
//			return true;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}

	@Override
	public boolean createDirectory() throws ServiceException {
		try {
			sardine.createDirectory(this.getPathUrl());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
