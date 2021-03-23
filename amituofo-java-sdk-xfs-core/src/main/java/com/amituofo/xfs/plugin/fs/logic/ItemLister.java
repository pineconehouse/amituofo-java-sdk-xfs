package com.amituofo.xfs.plugin.fs.logic;

import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;

public interface ItemLister {

	void list(ItemFilter filter, ItemHandler event);

}
