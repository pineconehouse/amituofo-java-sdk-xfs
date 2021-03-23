package com.amituofo.xfs.service.filter;

import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemFilter;

public class OrFilter extends ItemFilterCombination {
	public OrFilter() {
		super();
	}

	public OrFilter(ItemFilter filter1, ItemFilter filter2) {
		super(filter1, filter2);
	}

	@Override
	public boolean accept(Item file) {
		if (filters != null) {
			for (ItemFilter filter : filters) {
				if (filter.accept(file)) {
					return true;
				}
			}

			return false;
		} else {
			return true;
		}
	}
}
