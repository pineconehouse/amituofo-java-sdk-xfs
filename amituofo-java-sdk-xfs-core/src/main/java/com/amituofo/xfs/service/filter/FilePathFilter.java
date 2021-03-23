package com.amituofo.xfs.service.filter;

import com.amituofo.common.util.FileUtils;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemFilter;

public class FilePathFilter implements ItemFilter {

	private String pattern;

	public FilePathFilter(String pattern) {
		super();
		this.pattern = pattern;
	}

	@Override
	public boolean accept(Item file) {
//		if (file.isDirectory()) {
//			return true;
//		}
			
		return FileUtils.wildcardMatch(pattern, file.getPath());
	}

}
