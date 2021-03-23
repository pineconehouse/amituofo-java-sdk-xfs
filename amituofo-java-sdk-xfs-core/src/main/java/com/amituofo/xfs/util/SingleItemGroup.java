package com.amituofo.xfs.util;

import java.util.ArrayList;
import java.util.List;

public class SingleItemGroup<HANDLE_ITEM> implements ItemGroup<HANDLE_ITEM> {
	private HANDLE_ITEM item;

	public SingleItemGroup(HANDLE_ITEM item) {
		this.item = item;
	}

	public SingleItemGroup() {
	}

	@Override
	public HANDLE_ITEM first() {
		return item;
	}

	@Override
	public HANDLE_ITEM next() {
		return null;
	}

	@Override
	public int size() {
		return item == null ? 0 : 1;
	}

	@Override
	public void add(HANDLE_ITEM item) {
		this.item = item;
	}

	@Override
	public List<HANDLE_ITEM> getList() {
		List<HANDLE_ITEM> items = new ArrayList<HANDLE_ITEM>();
		items.add(item);
		return items;
	}

}
