package com.amituofo.xfs.service.filter;

import java.util.ArrayList;
import java.util.List;

import com.amituofo.xfs.service.ItemFilter;

public abstract class ItemFilterCombination implements ItemFilter {
	protected ItemFilter[] filters = null;
	protected List<ItemFilter> filterList = new ArrayList<ItemFilter>();

	public ItemFilterCombination() {
	}

	public ItemFilterCombination(ItemFilter filter1, ItemFilter filter2) {
		add(filter1);
		add(filter2);
	}

	public boolean isEffective() {
		return getFilterCount() > 0;
	}

	public void add(ItemFilter filter) {
		if (filter != null && filter.isEffective()) {
			filterList.add(filter);
			filters = filterList.toArray(new ItemFilter[filterList.size()]);
		}
	}

	public int getFilterCount() {
		return filters != null ? filters.length : 0;
	}
}
