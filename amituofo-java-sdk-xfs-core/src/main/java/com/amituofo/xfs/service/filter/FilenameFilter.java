package com.amituofo.xfs.service.filter;

import java.util.ArrayList;
import java.util.List;

import com.amituofo.common.util.FileUtils;
import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemFilter;

public class FilenameFilter implements ItemFilter {

	private String[] patterns;

	public FilenameFilter(String pattern) {
		super();
		String[] patternsStrs = pattern.split(";");
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < patternsStrs.length; i++) {
			patternsStrs[i] = patternsStrs[i].trim();
			if (StringUtils.isNotEmpty(patternsStrs[i])) {
				list.add(patternsStrs[i]);
			}
		}

		this.patterns = list.toArray(new String[list.size()]);
	}

	@Override
	public boolean accept(Item file) {
//		if (file.isDirectory()) {
//			return true;
//		}

		String name = file.getName();
		for (String pattern : patterns) {
			// 只要符合其中一个就过
			if (FileUtils.wildcardMatch(pattern, name)) {
				return true;
			}
		}

		return false;
	}

}
