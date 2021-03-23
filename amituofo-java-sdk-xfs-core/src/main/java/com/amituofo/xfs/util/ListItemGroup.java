package com.amituofo.xfs.util;

import java.util.ArrayList;
import java.util.List;

public class ListItemGroup<HANDLE_ITEM> implements ItemGroup<HANDLE_ITEM> {
	private int index = 0;
	private int size = 0;
	private List<HANDLE_ITEM> items = new ArrayList<HANDLE_ITEM>();

	public ListItemGroup() {
	}

	@Override
	public void add(HANDLE_ITEM item) {
		items.add(item);
	}

	@Override
	public HANDLE_ITEM first() {
		index = 0;
		size = items.size();
		return items.get(index);
	}

	@Override
	public HANDLE_ITEM next() {
		index++;
		if (index < size) {
			return items.get(index);
		}
		return null;
	}

	@Override
	public int size() {
		return items.size();
	}

	@Override
	public List<HANDLE_ITEM> getList() {
		return items;
	}

}
