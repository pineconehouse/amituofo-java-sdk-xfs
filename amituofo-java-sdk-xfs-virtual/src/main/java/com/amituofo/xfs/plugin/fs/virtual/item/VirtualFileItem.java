package com.amituofo.xfs.plugin.fs.virtual.item;

import java.io.InputStream;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.io.PeriodicityInputStream;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFSI;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFileSystemException;
import com.amituofo.xfs.service.ContentWriter;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.ItemType;

public class VirtualFileItem extends VirtualItemBase implements FileItem {
	// public MemFileItem(MemFileSystemEntry fileSystemEntry) {
	// super(fileSystemEntry);
	// }
	public VirtualFileItem(VirtualItemspace itemspace, VirtualFSI virtualFSI, String filename) {
		super(itemspace, virtualFSI);

		this.setName(filename);
		this.setPath(URLUtils.catPath(virtualFSI.getFolderPath(), filename, this.getPathSeparator()));
		// this.setLastUpdateTime(System.currentTimeMillis());
		// this.setCreateTime(System.currentTimeMillis());

		// long[] sizeRange = fileSystemEntry.getEntryConfig().getFilesizeRange();
		// Long size = Long.valueOf(ThreadLocalRandom.current().nextLong(sizeRange[0], sizeRange[1]));
		// // Long size = Long.valueOf(RandomUtils.randomLong(sizeRange[0], sizeRange[1]));
		// this.setSize(size);
	}

	@Override
	public void upateProperties() {
	}

	@Override
	public InputStream getContent() throws ServiceException {
		return new PeriodicityInputStream((this.getName() + "-" + this.getSize()).hashCode(), this.getSize());
	}

	@Override
	public boolean delete() throws ServiceException {
		try {
			boolean deleted = virtualFSI.delete(this.getName(), ItemType.File);
			return deleted;
		} catch (VirtualFileSystemException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public ContentWriter getContentWriter() {
		return new VirtualContentWriter(this, virtualFSI);
	}

	@Override
	public boolean exists() throws ServiceException {
		try {
			return virtualFSI.exist(this.getName(), ItemType.File);
		} catch (VirtualFileSystemException e) {
			// e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	private void updateSysAttrs() {
		try {
			Long[] sysattrs = virtualFSI.getSystemAttribute(this.getName(), ItemType.File);
			this.setSize(sysattrs[0]);
			this.setCreateTime(sysattrs[1]);
			this.setLastUpdateTime(sysattrs[2]);
		} catch (VirtualFileSystemException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Long getSize() {
		if (size == null) {
			updateSysAttrs();
		}
		return super.getSize();
	}

	@Override
	public Long getCreateTime() {
		if (createTime == null) {
			updateSysAttrs();
		}
		return super.getCreateTime();
	}

	@Override
	public Long getLastUpdateTime() {
		if (lastUpdateTime == null) {
			updateSysAttrs();
		}
		return super.getLastUpdateTime();
	}

}
