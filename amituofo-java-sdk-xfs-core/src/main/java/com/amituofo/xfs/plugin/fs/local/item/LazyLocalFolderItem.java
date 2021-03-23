package com.amituofo.xfs.plugin.fs.local.item;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import com.amituofo.common.util.StringUtils;

public class LazyLocalFolderItem extends LocalFolderItem {

	public LazyLocalFolderItem(LocalDriver itemspace, File file) {
		super(itemspace, file);
	}

	@Override
	public String getName() {
		String val = super.getName();
		if (val == null) {
			val = file.getName();
			// Will be empty when root D:\\ C:\\
			if (StringUtils.isEmpty(val)) {
				val = file.getPath();
			}
			super.setName(val);
		}

		return val;
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
	public Long getLastUpdateTime() {
		Long val = super.getLastUpdateTime();
		if (val == null) {
			val = file.lastModified();
			super.setLastUpdateTime(val);
		}

		return val;
	}

}
