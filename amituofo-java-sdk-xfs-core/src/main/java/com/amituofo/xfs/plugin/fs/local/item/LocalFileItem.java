package com.amituofo.xfs.plugin.fs.local.item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.FileUtils;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.service.ContentWriter;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.ItemType;

public class LocalFileItem extends LocalItemBase implements FileItem {
	public LocalFileItem(LocalDriver itemspace, File file) {
		super(itemspace, file);
	}

	@Override
	public InputStream getContent() throws ServiceException {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void rename(String newname) throws ServiceException {
		try {
			ValidUtils.invalidIfEmpty(newname, "A new name must be specified!");
			ValidUtils.invalidIfEqual(newname, this.getName(), "New name must be different with current name!");
		} catch (InvalidParameterException e) {
			throw new ServiceException(e);
		}

		File target = new File(FileUtils.catPath(this.getParent().getPath(), newname));
		boolean ok = file.renameTo(target);

		if (!ok) {
			throw new ServiceException("Unable to rename file. You may not have the appropriate permissions.");
		}
	}

	@Override
	public boolean delete() {
		return file.delete();
	}

	@Override
	public ContentWriter getContentWriter() {
		return new LocalContentWriter(this);
	}

	@Override
	public void upateProperties() throws ServiceException {
		this.setSize(file.length());
		this.setLastUpdateTime(file.lastModified());
		this.setCreateTime(null);
	}

}
