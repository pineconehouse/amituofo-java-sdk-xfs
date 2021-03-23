package com.amituofo.xfs.service.filter;

import com.amituofo.xfs.service.Item;

public class FilenameUnmatchFilter extends FilenameFilter {


	public FilenameUnmatchFilter(String pattern) {
		super(pattern);
	}

	@Override
	public boolean accept(Item file) {
		return !super.accept(file);
	}

}
