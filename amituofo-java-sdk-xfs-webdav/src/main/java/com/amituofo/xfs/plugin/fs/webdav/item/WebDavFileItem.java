package com.amituofo.xfs.plugin.fs.webdav.item;

import java.io.IOException;
import java.io.InputStream;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.service.ContentWriter;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.ItemType;

public class WebDavFileItem extends WebDavItemBase implements FileItem {
	public WebDavFileItem(WebDavItemspace itemspace, String filepath) {
		super(itemspace, filepath);
	}

	@Override
	public void upateProperties() {
		try {
			sardine.getPrincipals(this.getPathUrl());
			// super.setFileStatus(hadoopfs.getFileStatus(this.getFilePath()));
//			FileStatus fileStatus = hadoopfs.getFileStatus(this.getFilePath());
//			((ItemHiddenFunction) this).setSize(fileStatus.getLen());
//			((ItemHiddenFunction) this).setLastUpdateTime(fileStatus.getModificationTime());
//			((ItemHiddenFunction) this).setCreateTime(fileStatus.getModificationTime());
//			((ItemHiddenFunction) this).setLastAccessTime(fileStatus.getAccessTime());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public InputStream getContent() throws ServiceException {
		try {
			return sardine.get(this.getPathUrl());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	@Override
	public ContentWriter getContentWriter() {
		return new WebDavContentWriter(this);
	}

}
