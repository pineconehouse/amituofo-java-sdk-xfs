package com.amituofo.xfs.service.filter;

import java.util.ArrayList;
import java.util.List;

import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemFilter;

public class AndFilter extends ItemFilterCombination {
	public AndFilter() {
		super();
	}

	public AndFilter(ItemFilter filter1, ItemFilter filter2) {
		super(filter1, filter2);
	}

	@Override
	public boolean accept(Item file) {
		if (filters != null) {
			for (ItemFilter filter : filters) {
				if (!filter.accept(file)) {
					return false;
				}
			}
		}
		return true;
	}
}
