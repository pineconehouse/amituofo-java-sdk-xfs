package com.amituofo.xfs.service.filter;

import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemType;

public class FileTypeFilter implements ItemFilter {
	private ItemType type;

	public FileTypeFilter(ItemType type) {
		super();
		this.type = type;
	}

	@Override
	public boolean accept(Item file) {
		return file.getType() == type;
	}

}
