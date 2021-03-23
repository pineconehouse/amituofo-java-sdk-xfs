package com.amituofo.xfs.plugin.fs.memory.item;

import java.io.InputStream;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.memory.filesystem.MemoryFileSystem;
import com.amituofo.xfs.plugin.fs.memory.filesystem.MemoryFileSystemException;
import com.amituofo.xfs.service.ContentWriter;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.ItemType;

public class MemoryFileItem extends MemoryItemBase implements FileItem {
	public MemoryFileItem(MemoryFileSystem memfs, MemoryItemspace itemspace, String filepath) {
		super(itemspace, memfs);
		this.setPath(filepath);
	}

	@Override
	public void upateProperties() {
	}

	@Override
	public InputStream getContent() throws ServiceException {
		try {
			return memfs.read(this.getPath(), 0, -1);
		} catch (MemoryFileSystemException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete() throws ServiceException {
		try {
			return memfs.delete(this.getPath());
		} catch (MemoryFileSystemException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	@Override
	public ContentWriter getContentWriter() {
		return new MemoryContentWriter(this);
	}

	@Override
	public boolean exists() throws ServiceException {
		try {
			return memfs.exist(this.getPath());
		} catch (MemoryFileSystemException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	private void updateSysAttrs() throws MemoryFileSystemException {
		long size = memfs.getSize(this.getPath());
		this.setSize(size);
		long time = memfs.getLastModifiedTime(this.getPath());
		this.setLastUpdateTime(time);
	}

	@Override
	public Long getSize() {
		if (size == null) {
			try {
				updateSysAttrs();
			} catch (MemoryFileSystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return super.getSize();
	}

	@Override
	public Long getCreateTime() {
		if (createTime == null) {
			try {
				updateSysAttrs();
			} catch (MemoryFileSystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return super.getCreateTime();
	}

	@Override
	public Long getLastUpdateTime() {
		if (lastUpdateTime == null) {
			try {
				updateSysAttrs();
			} catch (MemoryFileSystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return super.getLastUpdateTime();
	}

}
