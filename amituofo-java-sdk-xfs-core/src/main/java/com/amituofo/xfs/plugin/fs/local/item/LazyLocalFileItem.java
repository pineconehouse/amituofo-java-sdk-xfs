package com.amituofo.xfs.plugin.fs.local.item;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import com.amituofo.common.ex.ServiceException;

public class LazyLocalFileItem extends LocalFileItem {

	public LazyLocalFileItem(LocalDriver itemspace, File file) {
		super(itemspace, file);
	}

	@Override
	public String getName() {
		String val = super.getName();
		if (val == null) {
			val = file.getName();
			super.setName(val);
		}

		return val;
	}

	@Override
	public String getNameOnly() {
		getName();
		return super.getNameOnly();
	}

	@Override
	public String getExt() {
		getName();
		return super.getExt();
	}

	@Override
	public String getPath() {
		String val = super.getPath();
		if (val == null) {
			// val = file.getAbsolutePath();
			val = file.getPath();
			super.setPath(val);
		}

		return val;
	}

	@Override
	public Long getSize() {
		Long val = super.getSize();
		if (val == null) {
			val = file.length();
			super.setSize(val);
		}

		return val;
	}

	@Override
	public Long getLastUpdateTime() {
		Long val = super.getLastUpdateTime();
		if (val == null) {
			val = file.lastModified();
			super.setLastUpdateTime(val);
		}

		return val;
	}

	@Override
	public Long getCreateTime() {
		Long val = super.getCreateTime();
		if (val == null) {
			try {
				BasicFileAttributes fatr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
				FileTime time = fatr.creationTime();
				if (time != null) {
					val = time.toMillis();
				}

				super.setCreateTime(val);
			} catch (IOException e) {
			}
		}

		return val;
	}
	
	@Override
	public Long getLastAccessTime() {
		Long val = super.getLastAccessTime();
		if (val == null) {
			try {
				BasicFileAttributes fatr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
				FileTime time = fatr.lastAccessTime();
				if (time != null) {
					val = time.toMillis();
				}

				super.setLastAccessTime(val);
			} catch (IOException e) {
			}
		}

		return val;

	}


	@Override
	public void upateProperties() throws ServiceException {
		this.setSize(file.length());
		setLastUpdateTime(file.lastModified());
		setCreateTime(null);
		getCreateTime();
		setLastAccessTime(null);
		getLastAccessTime();
	}

}
