package com.amituofo.xfs.service;

import com.amituofo.common.api.DataFilter;

public interface ItemFilter extends DataFilter<Item> {
	default boolean isEffective() {
		return true;
	}
}
