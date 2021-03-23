package com.amituofo.xfs.util;

import java.util.List;

public interface ItemGroup<HANDLE_ITEM> {
	void add(HANDLE_ITEM item);

	HANDLE_ITEM first();

	HANDLE_ITEM next();
	
	List<HANDLE_ITEM> getList();

	int size();

}
